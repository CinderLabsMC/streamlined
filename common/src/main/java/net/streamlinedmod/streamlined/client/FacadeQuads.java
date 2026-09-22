package net.streamlinedmod.streamlined.client;

import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

final class FacadeQuads {

    private static final float EPSILON = 1.0e-4f;
    private static final Direction[] DIRECTIONS = Direction.values();

    private FacadeQuads() {}

    static @NonNull BlockStateModelPart crop(@NonNull BlockStateModelPart source, float @NonNull [] box) {
        var culled = new EnumMap<Direction, List<BakedQuad>>(Direction.class);

        for (var direction : DIRECTIONS) {
            culled.put(direction, crop(source.getQuads(direction), box));
        }

        return new CroppedPart(source, culled, crop(source.getQuads(null), box));
    }

    private static List<BakedQuad> crop(List<BakedQuad> quads, float[] box) {
        var cropped = new ArrayList<BakedQuad>(quads.size());

        for (var quad : quads) {
            var result = crop(quad, box);

            if (result != null) {
                cropped.add(result);
            }
        }

        return cropped;
    }

    private static @Nullable BakedQuad crop(BakedQuad quad, float[] box) {
        var positions = new Vector3f[BakedQuad.VERTEX_COUNT];

        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Vector3f(quad.position(i));
        }

        int axis = planeAxis(positions);

        if (axis < 0) {
            return inside(positions, box) ? quad : null;
        }

        float plane = positions[0].get(axis);
        float min = box[axis];
        float max = box[axis + 3];
        boolean positive = quad.direction().getAxisDirection() == Direction.AxisDirection.POSITIVE;

        if (plane < min - EPSILON) {
            if (positive) {
                return null;
            }
            plane = min;
        } else if (plane > max + EPSILON) {
            if (!positive) {
                return null;
            }
            plane = max;
        }

        var origin = positions[0];
        var edgeU = new Vector3f(positions[1]).sub(origin);
        var edgeV = new Vector3f(positions[3]).sub(origin);
        float lengthU = edgeU.lengthSquared();
        float lengthV = edgeV.lengthSquared();

        if (lengthU < EPSILON || lengthV < EPSILON) {
            return null;
        }

        var cropped = new Vector3f[positions.length];
        var uvs = new long[positions.length];

        for (int i = 0; i < positions.length; i++) {
            var point = new Vector3f(positions[i]);

            for (int other = 0; other < 3; other++) {
                point.setComponent(other, other == axis ? plane : Math.clamp(point.get(other), box[other], box[other + 3]));
            }

            var offset = new Vector3f(point).sub(origin);
            offset.setComponent(axis, 0);

            float s = offset.dot(edgeU) / lengthU;
            float t = offset.dot(edgeV) / lengthV;

            cropped[i] = point;
            uvs[i] = interpolate(quad, s, t);
        }

        if (cropped[1].distanceSquared(cropped[0]) < EPSILON || cropped[3].distanceSquared(cropped[0]) < EPSILON) {
            return null;
        }

        return new BakedQuad(cropped[0], cropped[1], cropped[2], cropped[3], uvs[0], uvs[1], uvs[2], uvs[3], quad.direction(), quad.materialInfo());
    }

    private static long interpolate(BakedQuad quad, float s, float t) {
        long uv0 = quad.packedUV(0);
        long uv1 = quad.packedUV(1);
        long uv3 = quad.packedUV(3);

        float u = UVPair.unpackU(uv0) + s * (UVPair.unpackU(uv1) - UVPair.unpackU(uv0)) + t * (UVPair.unpackU(uv3) - UVPair.unpackU(uv0));
        float v = UVPair.unpackV(uv0) + s * (UVPair.unpackV(uv1) - UVPair.unpackV(uv0)) + t * (UVPair.unpackV(uv3) - UVPair.unpackV(uv0));

        return UVPair.pack(u, v);
    }

    private static int planeAxis(Vector3f[] positions) {
        for (int axis = 0; axis < 3; axis++) {
            float value = positions[0].get(axis);
            boolean flat = true;

            for (var position : positions) {
                if (Math.abs(position.get(axis) - value) > EPSILON) {
                    flat = false;
                    break;
                }
            }

            if (flat) {
                return axis;
            }
        }

        return -1;
    }

    private static boolean inside(Vector3f[] positions, float[] box) {
        for (var position : positions) {
            for (int axis = 0; axis < 3; axis++) {
                if (position.get(axis) < box[axis] - EPSILON || position.get(axis) > box[axis + 3] + EPSILON) {
                    return false;
                }
            }
        }

        return true;
    }

    private record CroppedPart(BlockStateModelPart source, Map<Direction, List<BakedQuad>> culled, List<BakedQuad> unculled) implements BlockStateModelPart {

        @Override
        public @NonNull List<BakedQuad> getQuads(@Nullable Direction direction) {
            return direction == null ? unculled : culled.get(direction);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return source.useAmbientOcclusion();
        }

        @Override
        public Material.@NonNull Baked particleMaterial() {
            return source.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return source.materialFlags();
        }
    }
}
