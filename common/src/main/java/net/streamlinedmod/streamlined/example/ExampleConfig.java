package net.streamlinedmod.streamlined.example;

import net.streamlinedmod.streamlined.Streamlined;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.gui.screens.Screen;

/** Beispiel: Cloth-Config (AutoConfig). */
@Config(name = Streamlined.MOD_ID)
public class ExampleConfig implements ConfigData {
    public boolean greeting = true;
    public int value = 5;

    public static void init() {
        AutoConfig.register(ExampleConfig.class, Toml4jConfigSerializer::new);
    }

    /** Config-Screen (Cloth Config); von ModMenu (Fabric) bzw. NeoForge unter Mods -> Config geöffnet. */
    public static Screen screen(Screen parent) {
        return AutoConfigClient.getConfigScreen(ExampleConfig.class, parent).get();
    }

    public static ExampleConfig get() {
        return AutoConfig.getConfigHolder(ExampleConfig.class).getConfig();
    }
}
