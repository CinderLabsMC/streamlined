package net.streamlinedmod.streamlined.block;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntityType;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.streamlinedmod.streamlined.cable.CableType;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyCableNetworks;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.streamlinedmod.streamlined.storage.StorageProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class Cable extends CinderGeoBlockEntity implements EnergyProvider, StorageProvider {

    private static final Direction[] DIRECTIONS = Direction.values();

    public static final CinderBlockEntityType<Cable> BLOCK_ENTITY = ModBlocks.REGISTRAR.blockEntity("cable", Cable::new);

    static {
        EnergyBridge.register(BLOCK_ENTITY);
    }

    public static @NonNull CinderBlockType<Cable> register(@NonNull CableType type) {
        var geometry = type.geometry();
        var shapes = createShapes(geometry.collisionWidth());

        return CinderBlock.builder(ModBlocks.REGISTRAR, type.name())
                .blockEntity(BLOCK_ENTITY)
                .data(type)
                .dynamicShape((state, level, pos, context) -> shapes[connectionMask(level, pos, type.type())])
                .geo(geo -> geo
                        .model(geometry.blockModel())
                        .itemModel(geometry.itemModel())
                        .animation(geometry.animation())
                        .texture(type.texture()))
                .register();
    }

    public Cable(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
        super(type, pos, state);
    }

    public @NonNull CableType type() {
        return data(CableType.class);
    }

    public long rate() {
        return type().rate();
    }

    @Override
    public @Nullable SimpleEnergy getEnergy(@Nullable Direction side) {
        return null;
    }

    @Override
    public void adjustBones(@NonNull CinderBones bones) {
        int connections = level == null ? 0 : connectionMask(level, worldPosition, type().type());

        for (var dir : DIRECTIONS) {
            bones.visible(dir.getSerializedName(), (connections & (1 << dir.ordinal())) != 0);
        }
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (level instanceof ServerLevel server) {
            EnergyCableNetworks.add(server, this);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level instanceof ServerLevel server) {
            EnergyCableNetworks.remove(server, this);
        }
    }

    public static int connectionMask(@NonNull BlockGetter level, @NonNull BlockPos pos, CableType.@NonNull Type type) {
        int mask = 0;

        for (var dir : DIRECTIONS) {
            if (isConnected(level, pos, dir, type)) {
                mask |= 1 << dir.ordinal();
            }
        }

        return mask;
    }

    public static boolean isConnected(@NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction dir, CableType.@NonNull Type type) {
        var target = pos.relative(dir);
        var neighbor = level.getBlockEntity(target);

        if (neighbor instanceof Cable cable) {
            return cable.type().type() == type;
        }

        return switch (type) {
            case ENERGY -> {
                if (neighbor instanceof EnergyProvider provider && provider.getEnergy(dir.getOpposite()) != null) {
                    yield true;
                }
                if (level instanceof Level real) {
                    yield EnergyBridge.findExternal(real, target, dir.getOpposite()) != null;
                }
                yield false;
            }
            case STORAGE -> neighbor instanceof StorageProvider || neighbor instanceof Container;
        };
    }

    private static VoxelShape[] createShapes(int width) {
        double min = (16 - width) / 2d;
        double max = min + width;

        var core = Block.box(min, min, min, max, max, max);

        var arms = new VoxelShape[DIRECTIONS.length];

        arms[Direction.NORTH.ordinal()] = Block.box(min, min, 0, max, max, min);
        arms[Direction.SOUTH.ordinal()] = Block.box(min, min, max, max, max, 16);
        arms[Direction.WEST.ordinal()] = Block.box(0, min, min, min, max, max);
        arms[Direction.EAST.ordinal()] = Block.box(max, min, min, 16, max, max);
        arms[Direction.DOWN.ordinal()] = Block.box(min, 0, min, max, min, max);
        arms[Direction.UP.ordinal()] = Block.box(min, max, min, max, 16, max);

        var shapes = new VoxelShape[1 << DIRECTIONS.length];

        for (int mask = 0; mask < shapes.length; mask++) {
            var shape = core;

            for (var dir : DIRECTIONS) {
                if ((mask & (1 << dir.ordinal())) != 0) {
                    shape = Shapes.or(shape, arms[dir.ordinal()]);
                }
            }

            shapes[mask] = shape;
        }

        return shapes;
    }
}
