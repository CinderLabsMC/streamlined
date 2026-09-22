package net.streamlinedmod.streamlined.geo;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.item.CableItem;
import org.jspecify.annotations.NonNull;

public final class CableItemGeoModel extends GeoModel<CableItem> {

    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "block/cable_item");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "textures/block/copper_energy_cable.png");

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return renderState.getOrDefaultGeckolibData(CableRenderData.MODEL, MODEL);
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return renderState.getOrDefaultGeckolibData(CableRenderData.TEXTURE, TEXTURE);
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull CableItem cable) {
        return cable.type().geometry().animation();
    }
}