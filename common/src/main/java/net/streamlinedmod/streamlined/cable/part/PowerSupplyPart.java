package net.streamlinedmod.streamlined.cable.part;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.Cable;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import org.jspecify.annotations.NonNull;

public final class PowerSupplyPart extends CablePart {

    private static final long CAPACITY = 20_000;
    private static final long MAX_INPUT = 1_000;

    private final SimpleEnergy energy = new SimpleEnergy(CAPACITY, MAX_INPUT, 0);

    public PowerSupplyPart(@NonNull Cable host, @NonNull Direction side) {
        super(PartType.POWER_SUPPLY, host, side);
    }

    public @NonNull SimpleEnergy energy() {
        return energy;
    }

    public long drain(long max, boolean simulate) {
        long drained = Math.min(max, energy.getAmount());

        if (!simulate && drained > 0) {
            energy.setAmount(energy.getAmount() - drained);
            host.setChanged();
        }

        return drained;
    }

    @Override
    public @NonNull InteractionResult onUse(@NonNull Player player) {
        if (!player.level().isClientSide()) {
            player.sendSystemMessage(Component.translatable("message." + Streamlined.MOD_ID + ".power_supply", energy.getAmount(), energy.getCapacity()));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void save(@NonNull ValueOutput output) {
        output.putLong("energy", energy.getAmount());
    }

    @Override
    public void load(@NonNull ValueInput input) {
        energy.setAmount(input.getLongOr("energy", 0));
    }
}
