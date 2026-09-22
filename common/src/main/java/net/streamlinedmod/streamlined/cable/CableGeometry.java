package net.streamlinedmod.streamlined.cable;

import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;

public record CableGeometry(int collisionWidth, Identifier blockModel, Identifier itemModel, Identifier animation) {

    private static final Identifier SMALL_BLOCK_MODEL = id("block/small_cable");
    private static final Identifier SMALL_ITEM_MODEL = id("block/small_cable_item");
    private static final Identifier SMALL_ANIMATION = id("block/small_cable");

    private static final Identifier MEDIUM_BLOCK_MODEL = id("block/medium_cable");
    private static final Identifier MEDIUM_ITEM_MODEL = id("block/medium_cable_item");
    private static final Identifier MEDIUM_ANIMATION = id("block/medium_cable");

    private static final Identifier LARGE_BLOCK_MODEL = id("block/large_cable");
    private static final Identifier LARGE_ITEM_MODEL = id("block/large_cable_item");
    private static final Identifier LARGE_ANIMATION = id("block/large_cable");

    public CableGeometry {
        if (collisionWidth < 1 || collisionWidth > 16) {
            throw new IllegalArgumentException("Cable width must be between 1 and 16 pixels");
        }
    }

    public static CableGeometry small() {
        return new CableGeometry(4, SMALL_BLOCK_MODEL, SMALL_ITEM_MODEL, SMALL_ANIMATION);
    }

    public static CableGeometry medium() {
        return new CableGeometry(6, MEDIUM_BLOCK_MODEL, MEDIUM_ITEM_MODEL, MEDIUM_ANIMATION);
    }

    public static CableGeometry large() {
        return new CableGeometry(8, LARGE_BLOCK_MODEL, LARGE_ITEM_MODEL, LARGE_ANIMATION);
    }

    public static CableGeometry extraLarge() {
        return new CableGeometry(12, LARGE_BLOCK_MODEL, LARGE_ITEM_MODEL, LARGE_ANIMATION);
    }

    public static CableGeometry custom(int collisionWidth, String blockModel, String itemModel, String animation) {
        return new CableGeometry(collisionWidth, id(blockModel), id(itemModel), id(animation));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, path);
    }
}
