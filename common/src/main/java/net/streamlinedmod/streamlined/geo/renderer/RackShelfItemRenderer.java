package net.streamlinedmod.streamlined.geo.renderer;

import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.streamlinedmod.streamlined.block.RackShelfBlock;
import net.streamlinedmod.streamlined.geo.CableItemGeoModel;
import net.streamlinedmod.streamlined.geo.CableRenderData;
import net.streamlinedmod.streamlined.geo.RackShelfItemGeoModel;
import net.streamlinedmod.streamlined.item.CableItem;
import net.streamlinedmod.streamlined.item.RackShelfItem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class RackShelfItemRenderer extends GeoItemRenderer<RackShelfItem> {

    public RackShelfItemRenderer() {
        super(new RackShelfItemGeoModel());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> renderPassInfo) {
        renderPassInfo.poseStack().translate(0.5f, 0, 0.5f);
    }
}