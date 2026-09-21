package net.streamlinedmod.streamlined.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class EnergyBridge {

    private static final List<Supplier<? extends BlockEntityType<?>>> TYPES = new ArrayList<>();

    public interface ExternalLookup {
        @Nullable EnergyHandle find(Level level, BlockPos pos, Direction side);
    }

    private static ExternalLookup externalLookup = (_, _, _) -> null;

    private EnergyBridge() {}

    public static void setExternalLookup(ExternalLookup lookup) {
        externalLookup = lookup;
    }

    public static @Nullable EnergyHandle findExternal(Level level, BlockPos pos, Direction side) {
        return externalLookup.find(level, pos, side);
    }

    public static void register(Supplier<? extends BlockEntityType<?>> type) {
        TYPES.add(type);
    }

    public static List<Supplier<? extends BlockEntityType<?>>> types() {
        return List.copyOf(TYPES);
    }
}
