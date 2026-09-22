package net.streamlinedmod.streamlined.client;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.cinderlabsmc.cinderlib.block.geo.client.CinderGeoBlockRenderer;
import net.cinderlabsmc.cinderlib.block.geo.client.CinderGeoModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import net.streamlinedmod.streamlined.block.Cable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class CableRenderer<R extends BlockEntityRenderState & GeoRenderState> extends CinderGeoBlockRenderer<Cable, R> {

    private static final float THICKNESS = 2 / 16f;
    private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final DataTicket<Facades> FACADES = DataTicket.create("streamlined_facades", (Class) Facades.class);

    private static final Matrix4fc IDENTITY = new Matrix4f();

    private final BlockModelResolver blockModels;
    private final Map<Cable, Facades> cache = new WeakHashMap<>();

    public CableRenderer(BlockEntityRendererProvider.@NonNull Context context, @NonNull CinderGeoModel<Cable> model) {
        super(context, model);
        this.blockModels = context.blockModelResolver();
    }

    public static void register() {
        Cable.BLOCK_ENTITY.entry().listen(type -> {
            var geo = Cable.BLOCK_ENTITY.geo();

            if (geo == null) {
                return;
            }

            var model = new CinderGeoModel<Cable>(geo.model(), geo.texture(), geo.animation());
            BlockEntityRendererRegistry.register(type, context -> new CableRenderer<>(context, model));
        });
    }

    @Override
    public void addRenderData(@NonNull Cable cable, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        super.addRenderData(cable, relatedObject, renderState, partialTick);

        var facades = cable.facades();

        if (facades.isEmpty()) {
            cache.remove(cable);
            return;
        }

        var models = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
        var cached = cache.get(cable);

        if (cached == null || cached.models() != models || !cached.facades().equals(facades)) {
            cached = build(cable, Map.copyOf(facades), models);
            cache.put(cable, cached);
        }

        renderState.addGeckolibData(FACADES, cached);
    }

    @Override
    public void submit(@NonNull R renderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        super.submit(renderState, poseStack, collector, camera);

        var facades = renderState.getOrDefaultGeckolibData(FACADES, (Facades) null);

        if (facades == null) {
            return;
        }

        for (var facade : facades.renders()) {
            var model = new BlockModelRenderState();

            model.setupModel(IDENTITY, facade.translucent()).addAll(facade.parts());
            model.tintLayers().addAll(facade.tints());
            model.submit(poseStack, collector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
    }

    private Facades build(Cable cable, Map<Direction, BlockState> facades, BlockStateModelSet models) {
        var renders = new ArrayList<FacadeRender>(facades.size());
        var sides = facades.isEmpty() ? EnumSet.noneOf(Direction.class) : EnumSet.copyOf(facades.keySet());

        facades.forEach((side, state) -> {
            var model = models.get(state);
            var parts = new ArrayList<BlockStateModelPart>();

            model.collectParts(RandomSource.create(state.getSeed(cable.getBlockPos())), parts);

            var box = bounds(side, sides);
            var cropped = new ArrayList<BlockStateModelPart>(parts.size());

            for (var part : parts) {
                cropped.add(FacadeQuads.crop(part, box));
            }

            var resolved = new BlockModelRenderState();
            blockModels.update(resolved, state, DISPLAY_CONTEXT);

            renders.add(new FacadeRender(cropped, model.hasMaterialFlag(BakedQuad.FLAG_TRANSLUCENT), new IntArrayList(resolved.tintLayers())));
        });

        return new Facades(facades, models, renders);
    }

    private static float[] bounds(Direction side, Set<Direction> sides) {
        float minX = 0, minY = 0, minZ = 0, maxX = 1, maxY = 1, maxZ = 1;

        switch (side.getAxis()) {
            case X -> {
                minY = sides.contains(Direction.DOWN) ? THICKNESS : 0;
                maxY = sides.contains(Direction.UP) ? 1 - THICKNESS : 1;
                minZ = sides.contains(Direction.NORTH) ? THICKNESS : 0;
                maxZ = sides.contains(Direction.SOUTH) ? 1 - THICKNESS : 1;
            }
            case Z -> {
                minY = sides.contains(Direction.DOWN) ? THICKNESS : 0;
                maxY = sides.contains(Direction.UP) ? 1 - THICKNESS : 1;
            }
            case Y -> {
            }
        }

        switch (side) {
            case NORTH -> maxZ = THICKNESS;
            case SOUTH -> minZ = 1 - THICKNESS;
            case WEST -> maxX = THICKNESS;
            case EAST -> minX = 1 - THICKNESS;
            case DOWN -> maxY = THICKNESS;
            case UP -> minY = 1 - THICKNESS;
        }

        return new float[]{minX, minY, minZ, maxX, maxY, maxZ};
    }

    private record FacadeRender(List<BlockStateModelPart> parts, boolean translucent, IntList tints) {}

    private record Facades(Map<Direction, BlockState> facades, BlockStateModelSet models, List<FacadeRender> renders) {}
}
