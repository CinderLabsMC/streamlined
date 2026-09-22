package net.streamlinedmod.streamlined.block;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.streamlinedmod.streamlined.blockentity.ModBlockEntities;
import net.streamlinedmod.streamlined.blockentity.RackShelfBlockEntity;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.item.RackShelfItem;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public final class RackShelfBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final RegistrySupplier<Block> RACK_SHELF = ModBlocks.block("rack_shelf", RackShelfBlock::new, RackShelfItem::new);

    public static final RegistrySupplier<BlockEntityType<RackShelfBlockEntity>> RACK_SHELF_BE = ModBlockEntities.BLOCK_ENTITIES.register("rack_shelf",
            () -> new BlockEntityType<>(RackShelfBlockEntity::new, Set.of(RACK_SHELF.get())));

    static {
        EnergyBridge.register(RACK_SHELF_BE);
    }

    public static void init() {}

    RackShelfBlock(Properties props) {
        super(props.noOcclusion());
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new RackShelfBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (level.isClientSide() || type != RACK_SHELF_BE.get()) return null;
        return (BlockEntityTicker<T>) (BlockEntityTicker<RackShelfBlockEntity>) RackShelfBlockEntity::serverTick;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof RackShelfBlockEntity generator) {
            MenuRegistry.openExtendedMenu((ServerPlayer) player, generator);
        }
        return InteractionResult.SUCCESS;
    }
}
