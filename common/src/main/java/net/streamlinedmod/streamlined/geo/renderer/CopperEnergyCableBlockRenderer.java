package net.streamlinedmod.streamlined.geo.renderer;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.streamlinedmod.streamlined.blockentity.CopperEnergyCableBlockEntity;
import net.streamlinedmod.streamlined.block.CopperEnergyCableBlock;
import net.streamlinedmod.streamlined.geo.CopperEnergyCableGeoModel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CopperEnergyCableBlockRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<CopperEnergyCableBlockEntity, @NonNull R> {

    private static final DataTicket<Integer> CONNECTIONS = DataTicket.create("streamlined_cable_connections", Integer.class);

    public CopperEnergyCableBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new CopperEnergyCableGeoModel());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockEntityRenderer<CopperEnergyCableBlockEntity, ?> create(BlockEntityRendererProvider.Context context) {
        return (BlockEntityRenderer) new CopperEnergyCableBlockRenderer(context);
    }

    @Override
    public void addRenderData(CopperEnergyCableBlockEntity cable, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        int mask = 0;
        Level level = cable.getLevel();

        if (level != null) {
            for (Direction dir : Direction.values()) {
                if (CopperEnergyCableBlock.isConnected(level, cable.getBlockPos(), dir)) {
                    mask |= 1 << dir.ordinal();
                }
            }
        }

        renderState.addGeckolibData(CONNECTIONS, mask);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<@NonNull R> renderPassInfo, @NonNull BoneSnapshots snapshots) {
        int mask = renderPassInfo.getOrDefaultGeckolibData(CONNECTIONS, 0);

        for (Direction dir : Direction.values()) {
            boolean connected = (mask & (1 << dir.ordinal())) != 0;
            snapshots.ifPresent(dir.getSerializedName(), bone -> bone.skipRender(!connected));
        }
    }
}
