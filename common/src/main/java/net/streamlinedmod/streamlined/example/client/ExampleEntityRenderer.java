package net.streamlinedmod.streamlined.example.client;

import com.geckolib.renderer.GeoEntityRenderer;
import net.streamlinedmod.streamlined.example.ExampleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EntityType;

/** Beispiel: GeckoLib-Renderer (Modell/Textur/Animation aus assets, Pfad via Entity-ID). */
public class ExampleEntityRenderer extends GeoEntityRenderer<ExampleEntity, EntityRenderState> {
    public ExampleEntityRenderer(EntityRendererProvider.Context context, EntityType<? extends ExampleEntity> type) {
        super(context, type);
    }
}
