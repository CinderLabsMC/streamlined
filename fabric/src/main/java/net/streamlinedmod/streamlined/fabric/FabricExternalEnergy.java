package net.streamlinedmod.streamlined.fabric;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyHandle;
import org.jspecify.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

final class FabricExternalEnergy implements EnergyBridge.ExternalLookup {

    @Override
    public @Nullable EnergyHandle find(Level level, BlockPos pos, Direction side) {
        EnergyStorage storage = EnergyStorage.SIDED.find(level, pos, side);
        if (storage == null) {
            return null;
        }
        return new EnergyHandle() {
            @Override
            public long insert(long max, boolean simulate) {
                if (!storage.supportsInsertion()) return 0;
                try (Transaction tx = Transaction.openOuter()) {
                    long moved = storage.insert(max, tx);
                    if (!simulate) tx.commit();
                    return moved;
                }
            }

            @Override
            public long extract(long max, boolean simulate) {
                if (!storage.supportsExtraction()) return 0;
                try (Transaction tx = Transaction.openOuter()) {
                    long moved = storage.extract(max, tx);
                    if (!simulate) tx.commit();
                    return moved;
                }
            }
        };
    }
}
