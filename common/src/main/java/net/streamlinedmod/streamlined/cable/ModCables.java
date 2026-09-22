package net.streamlinedmod.streamlined.cable;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.block.CableBlock;
import net.streamlinedmod.streamlined.block.ModBlocks;
import net.streamlinedmod.streamlined.energy.EnergyCableNetworks;
import net.streamlinedmod.streamlined.item.CableItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModCables {

    private static final List<Entry> CABLES = new ArrayList<>();

    // Energy cables - rate = flow rate
    public static final Entry COPPER_ENERGY = register(CableType.small("copper_energy_cable", 250, CableType.Type.ENERGY));
    public static final Entry ALUMINIUM_ENERGY = register(CableType.small("aluminium_energy_cable", 500, CableType.Type.ENERGY));
    public static final Entry SILVER_ENERGY = register(CableType.small("silver_energy_cable", 1000, CableType.Type.ENERGY));
    public static final Entry BRONZE_ENERGY = register(CableType.medium("bronze_energy_cable", 8000, CableType.Type.ENERGY));
    public static final Entry BRASS_ENERGY = register(CableType.medium("brass_energy_cable", 16000, CableType.Type.ENERGY));
    public static final Entry ELECTRUM_ENERGY = register(CableType.medium("electrum_energy_cable", 32000, CableType.Type.ENERGY));
    public static final Entry PLATINUM_ENERGY = register(CableType.large("platinium_energy_cable", 150000, CableType.Type.ENERGY));
    public static final Entry TUNGSTEN_ENERGY = register(CableType.large("tungsten_energy_cable", 300000, CableType.Type.ENERGY));
    public static final Entry IRIDIUM_ENERGY = register(CableType.large("iridium_energy_cable", 750000, CableType.Type.ENERGY));
    public static final Entry SUPER_CONDUCTOR_ENERGY = register(CableType.custom("superconductor_energy_cable",
            EnergyCableNetworks.UNLIMITED,
            12,
            "block/superconductor_energy_cable",
            "block/superconductor_energy_cable_item",
            "block/superconductor_energy_cable",
            CableType.Type.ENERGY));

    // Storage cables - rate = channels
    public static final Entry SERIAL_STORAGE = register(CableType.small("serial_storage_cable", 2, CableType.Type.STORAGE));
    public static final Entry COAXIAL_STORAGE = register(CableType.small("coaxial_storage_cable", 4, CableType.Type.STORAGE));
    public static final Entry RJ45_STORAGE = register(CableType.small("rj45_storage_cable", 8, CableType.Type.STORAGE));
    public static final Entry CAT8_STORAGE = register(CableType.medium("cat8_storage_cable", 16, CableType.Type.STORAGE));
    public static final Entry FIBER_OPTIC_STORAGE = register(CableType.medium("fiber_optic_storage_cable", 24, CableType.Type.STORAGE));
    public static final Entry DENSE_FIBER_STORAGE = register(CableType.custom("dense_fiber_storage_cable",
            32,
            8,
            "block/dense_fiber_storage_cable",
            "block/dense_fiber_storage_cable_item",
            "block/dense_fiber_storage_cable",
            CableType.Type.STORAGE));
    public static final Entry RESONANT_CRYSTAL_STORAGE = register(CableType.custom("resonant_crystal_storage_cable",
            64,
            8,
            "block/resonant_crystal_storage_cable",
            "block/resonant_crystal_storage_cable_item",
            "block/resonant_crystal_storage_cable",
            CableType.Type.STORAGE));
    public static final Entry QUANTUM_MATRIX_STORAGE = register(CableType.custom("quantum_matrix_storage_cable",
            128,
            12,
            "block/quantum_matrix_storage_cable",
            "block/quantum_matrix_storage_cable_item",
            "block/quantum_matrix_storage_cable",
            CableType.Type.STORAGE));

    private ModCables() {
    }

    public static void init() {
    }

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

    public record Entry(CableType type, RegistrySupplier<Block> block) {
    }
}
