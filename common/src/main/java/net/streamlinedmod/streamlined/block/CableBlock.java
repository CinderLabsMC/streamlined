package net.streamlinedmod.streamlined.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.streamlinedmod.streamlined.blockentity.CableBlockEntity;
import net.streamlinedmod.streamlined.cable.CableType;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.storage.StorageProvider;
import org.jspecify.annotations.NonNull;

public final class CableBlock extends Block implements EntityBlock {

    private static final Direction[] DIRECTIONS = Direction.values();

    private final CableType type;
    private final VoxelShape[] shapes;

    public CableBlock(CableType type, Properties props) {
        super(props);

        this.type = type;
        this.shapes = createShapes(type.geometry().collisionWidth());
    }

    public CableType type() {
        return type;
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return shapes[connectionMask(level, pos, this.type.type())];
    }

    public static int connectionMask(BlockGetter level, BlockPos pos, CableType.Type type) {
        int mask = 0;

        for (var dir : DIRECTIONS) {
            if (!isConnected(level, pos, dir, type)) {
                continue;
            }

            mask |= 1 << dir.ordinal();
        }

        return mask;
    }

    public static boolean isConnected(BlockGetter level, BlockPos pos, Direction dir, CableType.Type type) {
        var target = pos.relative(dir);
        var neighbor = level.getBlockEntity(target);

        if (neighbor instanceof CableBlockEntity cableBlockEntity) {
            CableType neighborCableType = cableBlockEntity.type();
            if (neighborCableType == null) {
                return false;
            }

            return neighborCableType.type() == type;
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
            case STORAGE -> {
                if (neighbor instanceof StorageProvider) {
                    yield true;
                }
                if (level instanceof Level real) {
                    yield neighbor instanceof Container;
                }
                yield false;
            }
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
                if ((mask & (1 << dir.ordinal())) == 0) {
                    continue;
                }

                shape = Shapes.or(shape, arms[dir.ordinal()]);
            }

            shapes[mask] = shape;
        }

        return shapes;
    }
}