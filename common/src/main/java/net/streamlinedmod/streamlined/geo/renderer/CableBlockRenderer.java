package net.streamlinedmod.streamlined.geo.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.streamlinedmod.streamlined.block.CableBlock;
import net.streamlinedmod.streamlined.blockentity.CableBlockEntity;
import net.streamlinedmod.streamlined.geo.CableGeoModel;
import net.streamlinedmod.streamlined.geo.CableRenderData;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class CableBlockRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<CableBlockEntity, @NonNull R> {

    public CableBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new CableGeoModel());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockEntityRenderer<CableBlockEntity, ?> create(BlockEntityRendererProvider.Context context) {
        return (BlockEntityRenderer) new CableBlockRenderer(context);
    }

    @Override
    public void addRenderData(CableBlockEntity cable, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        var type = cable.type();
        var geometry = type.geometry();
        var level = cable.getLevel();

        int connections = level == null ? 0 : CableBlock.connectionMask(level, cable.getBlockPos(), type.type());

        renderState.addGeckolibData(CableRenderData.CONNECTIONS, connections);
        renderState.addGeckolibData(CableRenderData.MODEL, geometry.blockModel());
        renderState.addGeckolibData(CableRenderData.TEXTURE, type.texture());
        renderState.addGeckolibData(CableRenderData.SCALE, 1.0f);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<@NonNull R> renderPassInfo, @NonNull BoneSnapshots snapshots) {
        int connections = renderPassInfo.getOrDefaultGeckolibData(CableRenderData.CONNECTIONS, 0);
        float scale = renderPassInfo.getOrDefaultGeckolibData(CableRenderData.SCALE, 1f);

        for (var dir : Direction.values()) {
            boolean connected = (connections & (1 << dir.ordinal())) != 0;

            snapshots.ifPresent(dir.getSerializedName(), bone -> bone.skipRender(!connected));
        }

        scale(snapshots, "core", scale, scale, scale);

        scale(snapshots, "north", scale, scale, 1);
        scale(snapshots, "south", scale, scale, 1);

        scale(snapshots, "east", 1, scale, scale);
        scale(snapshots, "west", 1, scale, scale);

        scale(snapshots, "up", scale, 1, scale);
        scale(snapshots, "down", scale, 1, scale);
    }

    private static void scale(BoneSnapshots snapshots, String name, float x, float y, float z) {
        snapshots.ifPresent(name, bone -> bone.setScale(bone.getScaleX() * x, bone.getScaleY() * y, bone.getScaleZ() * z));
    }
}