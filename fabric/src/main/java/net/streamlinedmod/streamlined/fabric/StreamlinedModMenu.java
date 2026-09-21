package net.streamlinedmod.streamlined.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.streamlinedmod.streamlined.example.ExampleConfig;

/** Zeigt den Cloth-Config-Screen in ModMenu (Mods -> Streamlined -> Config). */
public final class StreamlinedModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ExampleConfig::screen;
    }
}
