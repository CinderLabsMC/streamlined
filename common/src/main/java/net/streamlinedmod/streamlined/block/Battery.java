package net.streamlinedmod.streamlined.block;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntity;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class Battery extends CinderBlockEntity implements EnergyProvider {

    public static final CinderBlockType<Battery> TYPE = CinderBlock.builder(ModBlocks.REGISTRAR, "battery")
            .blockEntity(Battery::new)
            .register();

    static {
        EnergyBridge.register(TYPE::blockEntityType);
    }

    private final SimpleEnergy energy = new SimpleEnergy(10_000, 100, 100);

    public Battery(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NonNull SimpleEnergy getEnergy(@Nullable Direction side) {
        return energy;
    }

    @Override
    public @NonNull InteractionResult onUse(@NonNull Player player, @NonNull BlockHitResult hit) {
        if (!player.level().isClientSide()) {
            player.sendSystemMessage(Component.literal(energy.getAmount() + " / " + energy.getCapacity() + " FE"));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("energy", energy.getAmount());
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        energy.setAmount(input.getLongOr("energy", 0));
    }
}
