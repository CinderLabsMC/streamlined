package net.streamlinedmod.streamlined.geo;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.blockentity.CopperEnergyCableBlockEntity;
import org.jspecify.annotations.NonNull;

public class CopperEnergyCableGeoModel extends GeoModel<CopperEnergyCableBlockEntity> {

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "block/copper_energy_cable");
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "textures/block/copper_energy_cable.png");
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull CopperEnergyCableBlockEntity animatable) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "block/copper_energy_cable");
    }
}
