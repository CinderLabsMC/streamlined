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
import net.streamlinedmod.streamlined.blockentity.BasicEnergyCableBlockEntity;
import net.streamlinedmod.streamlined.block.BasicEnergyCableBlock;
import net.streamlinedmod.streamlined.geo.BasicEnergyCableGeoModel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BasicEnergyCableBlockRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<BasicEnergyCableBlockEntity, @NonNull R> {

    private static final DataTicket<Integer> CONNECTIONS = DataTicket.create("streamlined_cable_connections", Integer.class);

    public BasicEnergyCableBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new BasicEnergyCableGeoModel());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockEntityRenderer<BasicEnergyCableBlockEntity, ?> create(BlockEntityRendererProvider.Context context) {
        return (BlockEntityRenderer) new BasicEnergyCableBlockRenderer(context);
    }

    @Override
    public void addRenderData(BasicEnergyCableBlockEntity cable, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        int mask = 0;
        Level level = cable.getLevel();

        if (level != null) {
            for (Direction dir : Direction.values()) {
                if (BasicEnergyCableBlock.isConnected(level, cable.getBlockPos(), dir)) {
                    mask |= 1 << dir.ordinal();
                }
            }
        }

        renderState.addGeckolibData(CONNECTIONS, mask);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        int mask = renderPassInfo.getOrDefaultGeckolibData(CONNECTIONS, 0);

        for (Direction dir : Direction.values()) {
            boolean connected = (mask & (1 << dir.ordinal())) != 0;
            snapshots.ifPresent(dir.getSerializedName(), bone -> bone.skipRender(!connected));
        }
    }
}
