package net.streamlinedmod.streamlined.block;

import net.cinderlabsmc.cinderlib.block.CinderRegistrar;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.cable.ModCables;

public final class ModBlocks {

    public static final CinderRegistrar REGISTRAR = CinderRegistrar.create(Streamlined.MOD_ID);

    private ModBlocks() {}

    public static void init() {
        REGISTRAR.load(ModCables.class, Battery.class, Generator.class, RackShelf.class);
        REGISTRAR.register();
    }
}
