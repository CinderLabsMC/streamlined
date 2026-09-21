package net.streamlinedmod.streamlined.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.streamlinedmod.streamlined.blockentity.BatteryBlockEntity;
import net.streamlinedmod.streamlined.blockentity.ModBlockEntities;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public final class BatteryBlock extends Block implements EntityBlock {

    public static final RegistrySupplier<Block> BATTERY = ModBlocks.block("battery", BatteryBlock::new);

    public static final RegistrySupplier<BlockEntityType<BatteryBlockEntity>> BATTERY_BE = ModBlockEntities.BLOCK_ENTITIES.register("battery",
            () -> new BlockEntityType<>(BatteryBlockEntity::new, Set.of(BATTERY.get())));

    static {
        EnergyBridge.register(BATTERY_BE);
    }

    public static void init() {}

    BatteryBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BatteryBlockEntity(pos, state);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof EnergyProvider p && p.getEnergy(null) != null) {
            var energy = p.getEnergy(null);
            assert energy != null;
            player.sendSystemMessage(Component.literal(energy.getAmount() + " / " + energy.getCapacity() + " FE"));
        }
        return InteractionResult.SUCCESS;
    }
}
