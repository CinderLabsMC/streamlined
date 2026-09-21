package net.streamlinedmod.streamlined.fabric;

import dev.architectury.registry.client.gui.MenuScreenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.streamlinedmod.streamlined.client.GeneratorScreen;
import net.streamlinedmod.streamlined.menu.ModMenus;

public final class StreamlinedFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreenRegistry.registerScreenFactory(ModMenus.GENERATOR.get(), GeneratorScreen::new);
    }
}
