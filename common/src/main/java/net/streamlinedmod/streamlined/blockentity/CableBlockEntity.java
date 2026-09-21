package net.streamlinedmod.streamlined.blockentity;

import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.streamlinedmod.streamlined.block.CableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class CableBlockEntity extends BlockEntity implements EnergyProvider {
    private final SimpleEnergy energy = new SimpleEnergy(200, 100, 100);

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(CableBlock.CABLE_BE.get(), pos, state);
    }

    @Override
    public @Nullable SimpleEnergy getEnergy(@Nullable Direction side) {
        return energy;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CableBlockEntity cable) {
        for (Direction dir : Direction.values()) {
            if (!(level.getBlockEntity(pos.relative(dir)) instanceof EnergyProvider provider)) continue;
            SimpleEnergy other = provider.getEnergy(dir.getOpposite());
            if (other == null) continue;
            // Richtung nach relativem Füllstand: von voller nach leerer
            boolean push = cable.energy.getAmount() * other.getCapacity() > other.getAmount() * cable.energy.getCapacity();
            SimpleEnergy from = push ? cable.energy : other;
            SimpleEnergy to = push ? other : cable.energy;
            long moved = to.insert(from.extract(Long.MAX_VALUE, true), true);
            if (moved > 0) {
                to.insert(from.extract(moved, false), false);
                cable.setChanged();
            }
        }
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
