package net.streamlinedmod.streamlined.cable;

import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;

public record CableGeometry(int collisionWidth, float renderScale, Identifier blockModel, Identifier itemModel, Identifier animation) {

    private static final int BASE_WIDTH = 4;

    private static final Identifier STANDARD_BLOCK_MODEL = id("block/cable");
    private static final Identifier STANDARD_ITEM_MODEL = id("block/cable_item");
    private static final Identifier STANDARD_ANIMATION = id("block/cable");

    public CableGeometry {
        if(collisionWidth < 1 || collisionWidth > 16) {
            throw new IllegalArgumentException("Cable width must be between 1 and 16 pixels");
        }

        if (renderScale <= 0) {
            throw new IllegalArgumentException("Cable render scale must be greater than 0");
        }
    }

    public static CableGeometry standard(int width) {
        return new CableGeometry(width, width / (float) BASE_WIDTH, STANDARD_BLOCK_MODEL, STANDARD_ITEM_MODEL, STANDARD_ANIMATION);
    }

    public static CableGeometry custom(int collisionWidth, String blockModel, String itemModel) {
        return custom(collisionWidth, blockModel, itemModel, "block/cable");
    }

    public static CableGeometry custom(int collisionWidth, String blockModel, String itemModel, String animation) {
        return new CableGeometry(collisionWidth, 1, id(blockModel), id(itemModel), id(animation));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, path);
    }
}
