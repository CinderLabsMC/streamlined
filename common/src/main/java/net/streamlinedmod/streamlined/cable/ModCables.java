package net.streamlinedmod.streamlined.cable;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.block.CableBlock;
import net.streamlinedmod.streamlined.block.ModBlocks;
import net.streamlinedmod.streamlined.item.CableItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModCables {

    private static final List<Entry> CABLES = new ArrayList<>();

    public static final Entry COPPER = register(CableType.standard("copper_energy_cable", 100, 4));
    public static final Entry ALUMINIUM = register(CableType.standard("aluminium_energy_cable", 200, 4));

    private ModCables() {}

    public static void init() {}

    public static List<Entry> values() {
        return List.copyOf(CABLES);
    }

    public static Set<Block> blocks() {
        return CABLES.stream().map(Entry::block).map(RegistrySupplier::get).collect(Collectors.toUnmodifiableSet());
    }

    private static Entry register(CableType type) {
        var block = ModBlocks.block(type.name(), props -> new CableBlock(type, props), (value, props) -> new CableItem(type, value, props));

        var entry = new Entry(type, block);
        CABLES.add(entry);

        return entry;
    }

    public record Entry(CableType type, RegistrySupplier<Block> block) {}
}