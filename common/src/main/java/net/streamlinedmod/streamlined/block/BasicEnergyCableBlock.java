package net.streamlinedmod.streamlined.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.streamlinedmod.streamlined.blockentity.BasicEnergyCableBlockEntity;
import net.streamlinedmod.streamlined.blockentity.ModBlockEntities;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public final class BasicEnergyCableBlock extends Block implements EntityBlock {

    private static final VoxelShape CORE = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape NORTH = Block.box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SOUTH = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape WEST = Block.box(0, 6, 6, 6, 10, 10);
    private static final VoxelShape EAST = Block.box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape DOWN = Block.box(6, 0, 6, 10, 6, 10);
    private static final VoxelShape UP = Block.box(6, 10, 6, 10, 16, 10);

    public static final RegistrySupplier<Block> CABLE = ModBlocks.block("basic_energy_cable", BasicEnergyCableBlock::new);

    public static final RegistrySupplier<BlockEntityType<BasicEnergyCableBlockEntity>> CABLE_BE = ModBlockEntities.BLOCK_ENTITIES.register("basic_energy_cable",
            () -> new BlockEntityType<>(BasicEnergyCableBlockEntity::new, Set.of(CABLE.get())));

    static {
        EnergyBridge.register(CABLE_BE);
    }

    public static void init() {
    }

    BasicEnergyCableBlock(Properties props) {
        super(props);
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BasicEnergyCableBlockEntity(pos, state);
    }

    public static boolean isConnected(BlockGetter level, BlockPos pos, Direction dir) {
        BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
        return neighbor instanceof BasicEnergyCableBlockEntity
                || neighbor instanceof EnergyProvider provider && provider.getEnergy(dir.getOpposite()) != null;
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        VoxelShape shape = CORE;

        for (Direction direction : Direction.values()) {
            if (isConnected(level, pos, direction)) {
                shape = Shapes.or(shape, switch (direction) {
                    case NORTH -> NORTH;
                    case SOUTH -> SOUTH;
                    case WEST -> WEST;
                    case EAST -> EAST;
                    case UP -> UP;
                    case DOWN -> DOWN;
                });
            }
        }

        return shape;
    }
}
