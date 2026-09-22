package net.streamlinedmod.streamlined.pipe;

import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;

public record PipeType(String name, long rate, Identifier texture, PipeGeometry geometry, Type type) {

    public enum Type {
        FLUID,
        GAS,
        HEAT,
        ITEM
    }

    public PipeType {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Cable name cannot be blank");
        }

        if (rate <= 0) {
            throw new IllegalArgumentException("Cable flow rate must be greater than 0");
        }

        if (texture == null) {
            throw new IllegalArgumentException("Cable texture cannot be null");
        }

        if (geometry == null) {
            throw new IllegalArgumentException("Cable geometry cannot be null");
        }
    }

    public static PipeType small(String name, long rate, Type type) {
        return new PipeType(name, rate, texture(name), PipeGeometry.small(), type);
    }

    public static PipeType medium(String name, long rate, Type type) {
        return new PipeType(name, rate, texture(name), PipeGeometry.medium(), type);
    }

    public static PipeType large(String name, long rate, Type type) {
        return new PipeType(name, rate, texture(name), PipeGeometry.large(), type);
    }

    public static PipeType custom(String name, long rate, int collisionWidth, String blockModel, String itemModel, String animation, Type type) {
        return new PipeType(name, rate, texture(name), PipeGeometry.custom(collisionWidth, blockModel, itemModel, animation), type);
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "textures/block/" + name + ".png");
    }
}