package net.streamlinedmod.streamlined.pipe;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.block.ModBlocks;
import net.streamlinedmod.streamlined.energy.EnergyCableNetworks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModPipes {

    private static final List<Entry> PIPES = new ArrayList<>();

    // TODO: Fluid pipes
    // TODO: Item pipes
    // TODO: Gas pipes
    // TODO: Heat pipes

    private ModPipes() {}

    public static void init() {}

    public static List<Entry> values() {
        return List.copyOf(PIPES);
    }

    public static Set<Block> blocks() {
        return PIPES.stream().map(Entry::block).map(RegistrySupplier::get).collect(Collectors.toUnmodifiableSet());
    }

    private static Entry register(PipeType type) {
        /*var block = ModBlocks.block(type.name(), props -> new PipeBlock(type, props), (value, props) -> new CableItem(type, value, props));

        var entry = new Entry(type, block);
        CABLES.add(entry);

        return entry;*/
        return null;
    }

    public record Entry(PipeType type, RegistrySupplier<Block> block) {}
}
