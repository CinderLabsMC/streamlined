package net.streamlinedmod.streamlined.neoforge;

import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.example.ExampleConfig;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Streamlined.MOD_ID)
public final class StreamlinedNeoForge {
    public StreamlinedNeoForge(IEventBus modBus, ModContainer container) {
        Streamlined.init();
        modBus.addListener(StreamlinedNeoForge::registerCapabilities);
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (IConfigScreenFactory) (mod, parent) -> ExampleConfig.screen(parent));
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (var type : EnergyBridge.types()) {
            event.registerBlockEntity(Capabilities.Energy.BLOCK, type.get(), (be, side) -> {
                var energy = be instanceof EnergyProvider p ? p.getEnergy(side) : null;
                return energy == null ? null : new NeoForgeEnergyAdapter(energy);
            });
        }
    }
}
