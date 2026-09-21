package net.streamlinedmod.streamlined.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.streamlinedmod.streamlined.blockentity.GeneratorBlockEntity;
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

public final class GeneratorBlock extends Block implements EntityBlock {

    public static final RegistrySupplier<Block> GENERATOR = ModBlocks.block("generator", GeneratorBlock::new);

    public static final RegistrySupplier<BlockEntityType<GeneratorBlockEntity>> GENERATOR_BE = ModBlockEntities.BLOCK_ENTITIES.register("generator",
            () -> new BlockEntityType<>(GeneratorBlockEntity::new, Set.of(GENERATOR.get())));

    static {
        EnergyBridge.register(GENERATOR_BE);
    }

    public static void init() {}

    GeneratorBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new GeneratorBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (level.isClientSide() || type != GENERATOR_BE.get()) return null;
        return (BlockEntityTicker<T>) (BlockEntityTicker<GeneratorBlockEntity>) GeneratorBlockEntity::serverTick;
    }
}
