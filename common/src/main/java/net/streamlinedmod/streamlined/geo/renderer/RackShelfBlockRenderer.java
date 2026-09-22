package net.streamlinedmod.streamlined.geo.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.streamlinedmod.streamlined.blockentity.RackShelfBlockEntity;
import net.streamlinedmod.streamlined.geo.RackShelfGeoModel;
import org.jspecify.annotations.NonNull;

public final class RackShelfBlockRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<RackShelfBlockEntity, @NonNull R> {

    public RackShelfBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RackShelfGeoModel());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockEntityRenderer<RackShelfBlockEntity, ?> create(BlockEntityRendererProvider.Context context) {
        return (BlockEntityRenderer) new RackShelfBlockRenderer(context);
    }
}