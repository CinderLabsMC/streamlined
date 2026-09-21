package net.streamlinedmod.streamlined.fabric;

import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

final class FabricEnergyAdapter extends SnapshotParticipant<Long> implements EnergyStorage {
    private final SimpleEnergy energy;

    FabricEnergyAdapter(SimpleEnergy energy) {
        this.energy = energy;
    }

    @Override
    public long insert(long maxAmount, TransactionContext tx) {
        long moved = energy.insert(maxAmount, true);
        if (moved > 0) {
            updateSnapshots(tx);
            energy.insert(moved, false);
        }
        return moved;
    }

    @Override
    public long extract(long maxAmount, TransactionContext tx) {
        long moved = energy.extract(maxAmount, true);
        if (moved > 0) {
            updateSnapshots(tx);
            energy.extract(moved, false);
        }
        return moved;
    }

    @Override public long getAmount() { return energy.getAmount(); }
    @Override public long getCapacity() { return energy.getCapacity(); }
    @Override protected Long createSnapshot() { return energy.getAmount(); }
    @Override protected void readSnapshot(Long snapshot) { energy.setAmount(snapshot); }
}
