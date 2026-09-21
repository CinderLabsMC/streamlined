package net.streamlinedmod.streamlined.geo.renderer;

import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.streamlinedmod.streamlined.geo.CopperEnergyCableItemGeoModel;
import net.streamlinedmod.streamlined.item.CopperEnergyCableItem;

public class CopperEnergyCableItemRenderer extends GeoItemRenderer<CopperEnergyCableItem> {

    public CopperEnergyCableItemRenderer() {
        super(new CopperEnergyCableItemGeoModel());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> renderPassInfo) {
        renderPassInfo.poseStack().translate(0.5f, 0f, 0.5f);
    }
}
