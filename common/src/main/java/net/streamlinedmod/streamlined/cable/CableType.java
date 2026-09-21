package net.streamlinedmod.streamlined.cable;

import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;

public record CableType(String name, long flowRate, Identifier texture, CableGeometry geometry) {

    public CableType {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Cable name cannot be blank");
        }

        if(flowRate <= 0) {
            throw new IllegalArgumentException("Cable flow rate must be greater than 0");
        }

        if(texture == null) {
            throw new IllegalArgumentException("Cable texture cannot be null");
        }

        if(geometry == null) {
            throw new IllegalArgumentException("Cable geometry cannot be null");
        }
    }


    public static CableType standard(String name, long flowRate, int width) {
        return new CableType(name, flowRate, texture(name), CableGeometry.standard(width));
    }

    public static CableType custom(String name, long flowRate, int collisionWidth, String blockModel, String itemModel) {
        return new CableType(name, flowRate, texture(name), CableGeometry.custom(collisionWidth, blockModel, itemModel)
        );
    }

    public static CableType custom(String name, long flowRate, int collisionWidth, String blockModel, String itemModel, String animation) {
        return new CableType(name, flowRate, texture(name), CableGeometry.custom(collisionWidth, blockModel, itemModel, animation));
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "textures/block/" + name + ".png");
    }


}
