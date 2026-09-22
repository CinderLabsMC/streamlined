package net.streamlinedmod.streamlined.geo;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.blockentity.RackShelfBlockEntity;
import org.jspecify.annotations.NonNull;

public final class RackShelfGeoModel extends GeoModel<RackShelfBlockEntity> {

    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "block/rack_shelf");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "textures/block/rack_shelf.png");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "block/rack_shelf");

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull RackShelfBlockEntity animatable) {
        return ANIMATION;
    }
}
