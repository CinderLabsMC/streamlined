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

    public long getAmount() { return amount; }
    public long getCapacity() { return capacity; }
    public void setAmount(long amount) { this.amount = Math.clamp(amount, 0, capacity); }

    /** @return tatsächlich eingefügte Menge */
    public long insert(long max, boolean simulate) {
        long moved = Math.min(Math.min(max, maxInsert), capacity - amount);
        if (moved > 0 && !simulate) amount += moved;
        return Math.max(moved, 0);
    }

    /** @return tatsächlich entnommene Menge */
    public long extract(long max, boolean simulate) {
        long moved = Math.min(Math.min(max, maxExtract), amount);
        if (moved > 0 && !simulate) amount -= moved;
        return Math.max(moved, 0);
    }
}
