package net.streamlinedmod.streamlined.item;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.streamlinedmod.streamlined.Streamlined;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FacadeItem extends GeoModelItem {

    public FacadeItem(@NonNull Properties properties) {
        super(properties);
    }

    public static @Nullable BlockState block(@NonNull ItemStack stack) {
        return stack.get(ModComponents.FACADE_BLOCK.get());
    }

    public static @NonNull ItemStack of(@NonNull BlockState block) {
        var stack = new ItemStack(ModItems.FACADE.get());
        stack.set(ModComponents.FACADE_BLOCK.get(), block);
        return stack;
    }

    public static boolean isValid(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos) {
        return state.getRenderShape() == RenderShape.MODEL
                && !state.hasBlockEntity()
                && !(state.getBlock() instanceof CinderBlock)
                && state.isCollisionShapeFullBlock(level, pos);
    }

    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext context) {
        if (!context.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }

        var level = context.getLevel();
        var pos = context.getClickedPos();
        var state = level.getBlockState(pos);

        if (!isValid(state, level, pos)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            context.getItemInHand().set(ModComponents.FACADE_BLOCK.get(), state);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        var block = block(stack);

        if (block == null) {
            return super.getName(stack);
        }

        return Component.translatable("item." + Streamlined.MOD_ID + ".facade.block", block.getBlock().getName());
    }
}
