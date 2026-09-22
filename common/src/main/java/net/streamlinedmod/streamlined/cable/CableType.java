package net.streamlinedmod.streamlined.cable;

import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;

public record CableType(String name, long rate, Identifier texture, CableGeometry geometry, Type type) {

    public enum Type {
        ENERGY,
        STORAGE
    }

    public CableType {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Cable name cannot be blank");
        }

        if (rate <= 0) {
            throw new IllegalArgumentException("Cable rate must be greater than 0");
        }

        if (texture == null) {
            throw new IllegalArgumentException("Cable texture cannot be null");
        }

        if (geometry == null) {
            throw new IllegalArgumentException("Cable geometry cannot be null");
        }
    }

    public static CableType small(String name, long rate, Type type) {
        return new CableType(name, rate, texture(name), CableGeometry.small(), type);
    }

    public static CableType medium(String name, long rate, Type type) {
        return new CableType(name, rate, texture(name), CableGeometry.medium(), type);
    }

    public static CableType large(String name, long rate, Type type) {
        return new CableType(name, rate, texture(name), CableGeometry.large(), type);
    }

    public static CableType extraLarge(String name, long rate, Type type) {
        return new CableType(name, rate, texture(name), CableGeometry.extraLarge(), type);
    }

    public static CableType storage(String name, long channels, int collisionWidth) {
        return new CableType(name, channels, texture(name), CableGeometry.storage(collisionWidth), Type.STORAGE);
    }

    public static CableType custom(String name, long rate, int collisionWidth, String blockModel, String itemModel, String animation, Type type) {
        return new CableType(name, rate, texture(name), CableGeometry.custom(collisionWidth, blockModel, itemModel, animation), type);
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "textures/block/" + name + ".png");
    }
}