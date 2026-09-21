package net.streamlinedmod.streamlined.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyHandle;
import org.jspecify.annotations.Nullable;

final class NeoForgeExternalEnergy implements EnergyBridge.ExternalLookup {
    @Override
    public @Nullable EnergyHandle find(Level level, BlockPos pos, Direction side) {
        EnergyHandler handler = level.getCapability(Capabilities.Energy.BLOCK, pos, side);
        if (handler == null) {
            return null;
        }
        return new EnergyHandle() {
            @Override
            public long insert(long max, boolean simulate) {
                try (Transaction tx = Transaction.openRoot()) {
                    int moved = handler.insert(clamp(max), tx);
                    if (!simulate) tx.commit();
                    return moved;
                }
            }

            @Override
            public long extract(long max, boolean simulate) {
                try (Transaction tx = Transaction.openRoot()) {
                    int moved = handler.extract(clamp(max), tx);
                    if (!simulate) tx.commit();
                    return moved;
                }
            }
        };
    }

    private static int clamp(long value) {
        return (int) Math.min(value, Integer.MAX_VALUE);
    }
}
