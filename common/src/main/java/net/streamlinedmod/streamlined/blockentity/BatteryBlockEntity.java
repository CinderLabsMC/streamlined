package net.streamlinedmod.streamlined.blockentity;

import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.streamlinedmod.streamlined.block.BatteryBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class BatteryBlockEntity extends BlockEntity implements EnergyProvider {
    private final SimpleEnergy energy = new SimpleEnergy(10_000, 100, 100);

    public BatteryBlockEntity(BlockPos pos, BlockState state) {
        super(BatteryBlock.BATTERY_BE.get(), pos, state);
    }

    @Override
    public @Nullable SimpleEnergy getEnergy(@Nullable Direction side) {
        return energy;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("energy", energy.getAmount());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy.setAmount(input.getLongOr("energy", 0));
    }
}
