package net.streamlinedmod.streamlined.energy;

import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class EnergyBridge {
    private static final List<Supplier<? extends BlockEntityType<?>>> TYPES = new ArrayList<>();

    private EnergyBridge() {}

    public static void register(Supplier<? extends BlockEntityType<?>> type) {
        TYPES.add(type);
    }

    public static List<Supplier<? extends BlockEntityType<?>>> types() {
        return List.copyOf(TYPES);
    }
}
