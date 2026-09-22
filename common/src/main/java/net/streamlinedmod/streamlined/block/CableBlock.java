package net.streamlinedmod.streamlined.block;

import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.cinderlabsmc.cinderlib.block.CinderEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.streamlinedmod.streamlined.cable.part.Attachment;
import org.jspecify.annotations.NonNull;

public final class CableBlock extends CinderEntityBlock {

    public CableBlock(@NonNull CinderBlockType<?> type, BlockBehaviour.@NonNull Properties properties) {
        super(type, properties);
    }

    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack tool, boolean willHarvest, FluidState fluid) {
        if (level.getBlockEntity(pos) instanceof Cable cable && cable.targetAttachment(player) instanceof Attachment attachment) {
            if (!level.isClientSide()) {
                cable.breakAttachment(attachment, player);
            }

            return false;
        }

        return level.isClientSide() ? level.setBlock(pos, fluid.createLegacyBlock(), UPDATE_ALL_IMMEDIATE) : level.removeBlock(pos, false);
    }

    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        if (level.getBlockEntity(pos) instanceof Cable cable && cable.targetAttachment(player) instanceof Attachment attachment) {
            return cable.attachmentItem(attachment);
        }

        return state.getCloneItemStack(level, pos, includeData);
    }
}
