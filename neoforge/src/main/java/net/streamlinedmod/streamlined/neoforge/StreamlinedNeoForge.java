package net.streamlinedmod.streamlined.neoforge;

import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.example.ExampleConfig;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.streamlinedmod.streamlined.datagen.ModDataGenerators;
import java.util.Set;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.streamlinedmod.streamlined.client.GeneratorScreen;
import net.streamlinedmod.streamlined.client.StorageTerminalScreen;
import net.streamlinedmod.streamlined.client.WorkstationScreen;
import net.streamlinedmod.streamlined.menu.ModMenus;

@Mod(Streamlined.MOD_ID)
public final class StreamlinedNeoForge {

    public StreamlinedNeoForge(IEventBus modBus, ModContainer container) {
        Streamlined.init();
        EnergyBridge.setExternalLookup(new NeoForgeExternalEnergy());
        modBus.addListener(StreamlinedNeoForge::registerCapabilities);
        modBus.addListener(GatherDataEvent.Server.class, event ->
                event.createReloadableRegistryObjects(ModDataGenerators.reloadableRegistries(NeoForgeBlockLootTables::new), Set.of(Streamlined.MOD_ID)));

        modBus.addListener(RegisterMenuScreensEvent.class, event -> {
            event.register(ModMenus.GENERATOR.get(), GeneratorScreen::new);
            event.register(ModMenus.STORAGE_TERMINAL.get(), StorageTerminalScreen::new);
            event.register(ModMenus.WORKSTATION.get(), WorkstationScreen::new);
        });
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
