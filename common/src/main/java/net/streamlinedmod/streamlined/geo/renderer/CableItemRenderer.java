package net.streamlinedmod.streamlined.geo.renderer;

import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.GeoItemRenderer.RenderData;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.streamlinedmod.streamlined.geo.CableItemGeoModel;
import net.streamlinedmod.streamlined.geo.CableRenderData;
import net.streamlinedmod.streamlined.item.CableItem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class CableItemRenderer extends GeoItemRenderer<CableItem> {

    public CableItemRenderer() {
        super(new CableItemGeoModel());
    }

    @Override
    public void addRenderData(CableItem cable, @Nullable RenderData data, @NonNull GeoRenderState renderState, float partialTick) {
        var type = cable.type();
        var geometry = type.geometry();

        renderState.addGeckolibData(CableRenderData.MODEL, geometry.itemModel());
        renderState.addGeckolibData(CableRenderData.TEXTURE, type.texture());
        renderState.addGeckolibData(CableRenderData.SCALE, geometry.renderScale());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<GeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        float scale = renderPassInfo.getOrDefaultGeckolibData(CableRenderData.SCALE, 1f);

        scale(snapshots, "core", scale, scale, scale);
        scale(snapshots, "north", scale, scale, 1);
        scale(snapshots, "south", scale, scale, 1);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> renderPassInfo) {
        renderPassInfo.poseStack().translate(0.5f, 0, 0.5f);
    }

    private static void scale(BoneSnapshots snapshots, String name, float x, float y, float z) {
        snapshots.ifPresent(name, bone -> bone.setScale(bone.getScaleX() * x, bone.getScaleY() * y, bone.getScaleZ() * z));
    }
}