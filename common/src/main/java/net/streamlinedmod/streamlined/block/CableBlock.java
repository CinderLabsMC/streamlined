package net.streamlinedmod.streamlined.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.streamlinedmod.streamlined.blockentity.CableBlockEntity;
import net.streamlinedmod.streamlined.blockentity.ModBlockEntities;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public final class CableBlock extends Block implements EntityBlock {


    public static final RegistrySupplier<Block> CABLE = ModBlocks.block("cable", CableBlock::new);

    public static final RegistrySupplier<BlockEntityType<CableBlockEntity>> CABLE_BE = ModBlockEntities.BLOCK_ENTITIES.register("cable",
            () -> new BlockEntityType<>(CableBlockEntity::new, Set.of(CABLE.get())));

    static {
        EnergyBridge.register(CABLE_BE);
    }

    public static void init() {}

    CableBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (level.isClientSide() || type != CABLE_BE.get()) return null;
        return (BlockEntityTicker<T>) (BlockEntityTicker<CableBlockEntity>) CableBlockEntity::serverTick;
    }
}
