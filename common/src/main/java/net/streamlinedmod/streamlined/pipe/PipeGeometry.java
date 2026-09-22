package net.streamlinedmod.streamlined.pipe;

import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;

public record PipeGeometry(int collisionWidth, Identifier blockModel, Identifier itemModel, Identifier animation) {

    private static final Identifier SMALL_BLOCK_MODEL = id("block/small_pipe");
    private static final Identifier SMALL_ITEM_MODEL = id("block/small_pipe_item");
    private static final Identifier SMALL_ANIMATION = id("block/small_pipe");

    private static final Identifier MEDIUM_BLOCK_MODEL = id("block/medium_pipe");
    private static final Identifier MEDIUM_ITEM_MODEL = id("block/medium_pipe_item");
    private static final Identifier MEDIUM_ANIMATION = id("block/medium_pipe");

    private static final Identifier LARGE_BLOCK_MODEL = id("block/large_pipe");
    private static final Identifier LARGE_ITEM_MODEL = id("block/large_pipe_item");
    private static final Identifier LARGE_ANIMATION = id("block/large_pipe");

    public PipeGeometry {
        if (collisionWidth < 1 || collisionWidth > 16) {
            throw new IllegalArgumentException("Pipe width must be between 1 and 16 pixels");
        }
    }

    public static PipeGeometry small() {
        return new PipeGeometry(6, SMALL_BLOCK_MODEL, SMALL_ITEM_MODEL, SMALL_ANIMATION);
    }

    public static PipeGeometry medium() {
        return new PipeGeometry(8, MEDIUM_BLOCK_MODEL, MEDIUM_ITEM_MODEL, MEDIUM_ANIMATION);
    }

    public static PipeGeometry large() {
        return new PipeGeometry(10, LARGE_BLOCK_MODEL, LARGE_ITEM_MODEL, LARGE_ANIMATION);
    }

    public static PipeGeometry custom(int collisionWidth, String blockModel, String itemModel, String animation) {
        return new PipeGeometry(collisionWidth, id(blockModel), id(itemModel), id(animation));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, path);
    }
}
