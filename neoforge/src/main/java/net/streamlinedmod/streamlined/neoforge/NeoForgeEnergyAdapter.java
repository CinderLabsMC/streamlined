package net.streamlinedmod.streamlined.neoforge;

import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/** Stellt einen {@link SimpleEnergy} als NeoForge-EnergyHandler (FE) bereit. */
final class NeoForgeEnergyAdapter extends SnapshotJournal<Long> implements EnergyHandler {
    private final SimpleEnergy energy;

    NeoForgeEnergyAdapter(SimpleEnergy energy) {
        this.energy = energy;
    }

    @Override
    public int insert(int amount, TransactionContext tx) {
        int moved = (int) energy.insert(amount, true);
        if (moved > 0) {
            updateSnapshots(tx);
            energy.insert(moved, false);
        }
        return moved;
    }

    @Override
    public int extract(int amount, TransactionContext tx) {
        int moved = (int) energy.extract(amount, true);
        if (moved > 0) {
            updateSnapshots(tx);
            energy.extract(moved, false);
        }
        return moved;
    }

    @Override public long getAmountAsLong() { return energy.getAmount(); }
    @Override public long getCapacityAsLong() { return energy.getCapacity(); }
    @Override protected Long createSnapshot() { return energy.getAmount(); }
    @Override protected void revertToSnapshot(Long snapshot) { energy.setAmount(snapshot); }
}
