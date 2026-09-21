package net.streamlinedmod.streamlined.cable;

import com.jcraft.jorbis.Block;
import dev.architectury.registry.registries.RegistrySupplier;
import net.streamlinedmod.streamlined.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModCables {

    private static final List<Entry> CABLES = new ArrayList<>();

    public static final Entry COPPER = register(CableType.standard("copper_energy_cable", 100, 4));

    private ModCables() {}

    public static void init() {}

    public static List<Entry> values() {
        return List.copyOf(CABLES);
    }

    public static Set<Block> blocks() {
        return CABLES.stream().map(Entry::blockRegistrySupplier).map(RegistrySupplier::get).collect(Collectors.toUnmodifiableSet());
    }

    private static Entry register(CableType type) {
        var block = ModBlocks.block(type.name(), props -> new )
    }


    public record Entry(CableType type, RegistrySupplier<Block> blockRegistrySupplier) {}

}
