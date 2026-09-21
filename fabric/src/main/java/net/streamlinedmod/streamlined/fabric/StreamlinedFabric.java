package net.streamlinedmod.streamlined.fabric;

import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.fabricmc.api.ModInitializer;
import team.reborn.energy.api.EnergyStorage;

public final class StreamlinedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Streamlined.init();
        for (var type : EnergyBridge.types()) {
            EnergyStorage.SIDED.registerForBlockEntity((be, side) -> {
                var energy = be instanceof EnergyProvider p ? p.getEnergy(side) : null;
                return energy == null ? null : new FabricEnergyAdapter(energy);
            }, type.get());
        }
    }
}
