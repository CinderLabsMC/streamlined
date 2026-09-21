package net.streamlinedmod.streamlined.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.streamlinedmod.streamlined.cable.CableType;
import org.jspecify.annotations.Nullable;

public final class CableBlock extends Block implements EntityBlock {

    private static final Direction[] DIRECTIONS = Direction.values();

    private final CableType type;

    private final VoxelShape[] shapes;

    public CableBlock(CableType type, Properties properties) {
        super(properties);
        this.type = type;
        this.shapes = createShapes(type.geometry().collisionWidth());
    }

    public CableType type() {
        return type;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        // TODO: Return Block Entity
        return null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes[]
    }

    public static int connectionMask(BlockGetter level, BlockPos pos) {
        int mask = 0;

        for (var dir : DIRECTIONS) {
            if (!)
        }
    }

    public static boolean isConnected(BlockGetter level, BlockPos pos, Direction dir) {
        var target = pos.relative(dir);
        var neighbor = level.getBlockEntity(target);

        // Check Instances
        return true;
    }

    private static VoxelShape[] createShapes(int width) {
        double min = (16 - width) / 2d;
        double max = min + width;

        var core = Block.box(min, min, min, max, max, max);

        var arms = new VoxelShape[DIRECTIONS.length];

        arms[Direction.NORTH.ordinal()] = Block.box(min, min, 0, max, max, min);
        arms[Direction.SOUTH.ordinal()] = Block.box(min, min, max, max, max, 16);
        arms[Direction.WEST.ordinal()] = Block.box(0, min, min, min, max, max);
        arms[Direction.EAST.ordinal()] = Block.box(max, min, min, 16, max, max);
        arms[Direction.UP.ordinal()] = Block.box(min, max, min, max, 16, max);
        arms[Direction.DOWN.ordinal()] = Block.box(min, 0, min, max, min, max);

        var shapes = new VoxelShape[1 << DIRECTIONS.length];

        for(int mask = 0; mask < shapes.length; mask++) {
            var shape = core;

            for (var dir : DIRECTIONS) {
                if((mask & (1 << dir.ordinal())) == 0) {
                    continue;
                }

                shape = Shapes.or(shape, arms[dir.ordinal()]);
            }

            shapes[mask] = shape;
        }

        return shapes;
    }
}
