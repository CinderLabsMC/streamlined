package net.streamlinedmod.streamlined.cable.part;

import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.streamlinedmod.streamlined.block.Cable;
import net.streamlinedmod.streamlined.cable.CableType;
import net.streamlinedmod.streamlined.item.ModItems;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

public enum PartType {

    STORAGE_TERMINAL("storage_terminal", 12, 2, 10, true, StorageTerminalPart::new),
    POWER_SUPPLY("power_supply", 10, 4, 10, true, PowerSupplyPart::new),
    CABLE_SEPARATOR("cable_separator", 8, 1, 8, false, CableSeparatorPart::new);

    private static final double HIT_TOLERANCE = 0.001;
    private static final double WINDOW_DEPTH = 0.5;

    private final String id;
    private final boolean storageOnly;
    private final BiFunction<Cable, Direction, CablePart> factory;
    private final VoxelShape[] shapes = new VoxelShape[Direction.values().length];
    private final VoxelShape[] windows = new VoxelShape[Direction.values().length];

    PartType(String id, int size, int depth, int window, boolean storageOnly, BiFunction<Cable, Direction, CablePart> factory) {
        this.id = id;
        this.storageOnly = storageOnly;
        this.factory = factory;

        for (var side : Direction.values()) {
            shapes[side.ordinal()] = shape(side, size, depth);
            windows[side.ordinal()] = shape(side, window, WINDOW_DEPTH);
        }
    }

    public static @Nullable PartType byId(@NonNull String id) {
        for (var type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }

        return null;
    }

    public @NonNull String id() {
        return id;
    }

    public boolean allowedOn(CableType.@NonNull Type cable) {
        return !storageOnly || cable == CableType.Type.STORAGE;
    }

    public @NonNull String bone(@NonNull Direction side) {
        return id + "_" + side.getSerializedName();
    }

    public @NonNull VoxelShape shape(@NonNull Direction side) {
        return shapes[side.ordinal()];
    }

    public @NonNull VoxelShape window(@NonNull Direction side) {
        return windows[side.ordinal()];
    }

    public boolean contains(@NonNull Direction side, @NonNull Vec3 local) {
        return shape(side).bounds().inflate(HIT_TOLERANCE).contains(local);
    }

    public boolean windowContains(@NonNull Direction side, @NonNull Vec3 local) {
        return window(side).bounds().inflate(HIT_TOLERANCE).contains(local);
    }

    public @NonNull Item item() {
        return switch (this) {
            case STORAGE_TERMINAL -> ModItems.STORAGE_TERMINAL.get();
            case POWER_SUPPLY -> ModItems.POWER_SUPPLY.get();
            case CABLE_SEPARATOR -> ModItems.CABLE_SEPARATOR.get();
        };
    }

    public @NonNull CablePart create(@NonNull Cable host, @NonNull Direction side) {
        return factory.apply(host, side);
    }

    private static VoxelShape shape(Direction side, double size, double depth) {
        double min = (16 - size) / 2d;
        double max = min + size;

        return switch (side) {
            case NORTH -> Block.box(min, min, 0, max, max, depth);
            case SOUTH -> Block.box(min, min, 16 - depth, max, max, 16);
            case WEST -> Block.box(0, min, min, depth, max, max);
            case EAST -> Block.box(16 - depth, min, min, 16, max, max);
            case DOWN -> Block.box(min, 0, min, max, depth, max);
            case UP -> Block.box(min, 16 - depth, min, max, 16, max);
        };
    }
}
