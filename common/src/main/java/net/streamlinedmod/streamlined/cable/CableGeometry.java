package net.streamlinedmod.streamlined.cable;

import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;

public record CableGeometry(int collisionWidth, Identifier blockModel, Identifier itemModel, Identifier animation) {

    private static final Identifier ANIMATION = id("block/small_cable");

    public CableGeometry {
        if (collisionWidth < 1 || collisionWidth > 16) {
            throw new IllegalArgumentException("Cable width must be between 1 and 16 pixels");
        }
    }

    public static CableGeometry small() {
        return energy(4);
    }

    public static CableGeometry medium() {
        return energy(6);
    }

    public static CableGeometry large() {
        return energy(8);
    }

    public static CableGeometry extraLarge() {
        return energy(12);
    }

    public static CableGeometry energy(int collisionWidth) {
        return generated("energy_cable", collisionWidth);
    }

    public static CableGeometry storage(int collisionWidth) {
        return generated("storage_cable", collisionWidth);
    }

    public static CableGeometry custom(int collisionWidth, String blockModel, String itemModel, String animation) {
        return new CableGeometry(collisionWidth, id(blockModel), id(itemModel), id(animation));
    }

    private static CableGeometry generated(String prefix, int collisionWidth) {
        var model = "block/" + prefix + "_" + collisionWidth;
        return new CableGeometry(collisionWidth, id(model), id(model + "_item"), ANIMATION);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, path);
    }
}
