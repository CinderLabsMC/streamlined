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
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.streamlinedmod.streamlined.client.GeneratorScreen;
import net.streamlinedmod.streamlined.menu.ModMenus;

@Mod(Streamlined.MOD_ID)
public final class StreamlinedNeoForge {

    public StreamlinedNeoForge(IEventBus modBus, ModContainer container) {
        Streamlined.init();
        EnergyBridge.setExternalLookup(new NeoForgeExternalEnergy());
        modBus.addListener(StreamlinedNeoForge::registerCapabilities);

        modBus.addListener(RegisterMenuScreensEvent.class, event -> event.register(ModMenus.GENERATOR.get(), GeneratorScreen::new));
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
