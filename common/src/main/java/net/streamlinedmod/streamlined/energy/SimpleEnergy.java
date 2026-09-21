package net.streamlinedmod.streamlined.energy;

public class SimpleEnergy {
    private final long capacity;
    private final long maxInsert;
    private final long maxExtract;
    private long amount;

    public SimpleEnergy(long capacity, long maxInsert, long maxExtract) {
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    public long getAmount() {
        return amount;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setAmount(long amount) {
        this.amount = Math.clamp(amount, 0, capacity);
    }

    public long insert(long max, boolean simulate) {
        long moved = Math.min(Math.min(max, maxInsert), capacity - amount);
        if (moved > 0 && !simulate) {
            amount += moved;
        }
        return Math.max(moved, 0);
    }

    public long extract(long max, boolean simulate) {
        long moved = Math.min(Math.min(max, maxExtract), amount);
        if (moved > 0 && !simulate) {
            amount -= moved;
        }
        return Math.max(moved, 0);
    }

    /**
     * Verschiebt höchstens {@code max} Energie von {@code from} nach {@code to}. Beachtet Kapazität und
     * Ein-/Ausgabelimits beider Seiten mit einer einzigen Simulation.
     *
     * @return tatsächlich verschobene Menge
     */
    public static long transfer(SimpleEnergy from, SimpleEnergy to, long max) {
        long moved = Math.min(from.extract(max, true), to.insert(max, true));
        if (moved > 0) {
            from.extract(moved, false);
            to.insert(moved, false);
        }
        return moved;
    }
}
