package net.streamlinedmod.streamlined.energy;

public interface EnergyHandle {

    long insert(long max, boolean simulate);

    long extract(long max, boolean simulate);

}
