package net.streamlinedmod.streamlined.cable;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.block.Cable;
import net.streamlinedmod.streamlined.energy.EnergyCableNetworks;

import java.util.ArrayList;
import java.util.List;

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
    public static final Entry SUPER_CONDUCTOR_ENERGY = register(CableType.extraLarge("superconductor_energy_cable", EnergyCableNetworks.UNLIMITED, CableType.Type.ENERGY));

    // Storage cables - rate = channels
    public static final Entry SERIAL_STORAGE = register(CableType.storage("serial_storage_cable", 2, 4));
    public static final Entry COAXIAL_STORAGE = register(CableType.storage("coaxial_storage_cable", 4, 4));
    public static final Entry RJ45_STORAGE = register(CableType.storage("rj45_storage_cable", 8, 4));
    public static final Entry CAT8_STORAGE = register(CableType.storage("cat8_storage_cable", 16, 6));
    public static final Entry FIBER_OPTIC_STORAGE = register(CableType.storage("fiber_optic_storage_cable", 24, 6));
    public static final Entry DENSE_FIBER_STORAGE = register(CableType.storage("dense_fiber_storage_cable", 32, 8));
    public static final Entry RESONANT_CRYSTAL_STORAGE = register(CableType.storage("resonant_crystal_storage_cable", 64, 8));
    public static final Entry QUANTUM_MATRIX_STORAGE = register(CableType.storage("quantum_matrix_storage_cable", 128, 12));

    private ModCables() {
    }

    public static void init() {
    }

    public static List<Entry> values() {
        return List.copyOf(CABLES);
    }

    private static Entry register(CableType type) {
        var entry = new Entry(type, Cable.register(type).blockEntry());
        CABLES.add(entry);

        return entry;
    }

    public record Entry(CableType type, RegistrySupplier<Block> block) {
    }
}
