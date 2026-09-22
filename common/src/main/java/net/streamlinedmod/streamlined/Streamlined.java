package net.streamlinedmod.streamlined;

import net.streamlinedmod.streamlined.block.ModBlocks;
import net.streamlinedmod.streamlined.client.StreamlinedClient;
import net.streamlinedmod.streamlined.createmodetab.ModCreativeModeTabs;
import net.streamlinedmod.streamlined.example.ExampleConfig;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.streamlinedmod.streamlined.example.ExampleEntities;
import net.streamlinedmod.streamlined.example.ExampleEvents;
import net.streamlinedmod.streamlined.energy.EnergyCableNetworks;
import net.streamlinedmod.streamlined.menu.ModMenus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Streamlined {

    public static final String MOD_ID = "streamlined";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private Streamlined() {}

    public static void init() {
        ExampleConfig.init();

        ModBlocks.init();

        ModCreativeModeTabs.init();

        ModMenus.init();
        EnergyCableNetworks.init();

        ExampleEntities.init();
        ExampleEvents.init();

        EnvExecutor.runInEnv(Env.CLIENT, () -> StreamlinedClient::init);

        LOGGER.info("Streamlined initialized (greeting={})", ExampleConfig.get().greeting);
    }
}
