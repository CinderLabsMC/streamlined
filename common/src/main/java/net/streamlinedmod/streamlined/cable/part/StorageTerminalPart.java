package net.streamlinedmod.streamlined.cable.part;

import dev.architectury.registry.menu.ExtendedMenuDataProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.streamlinedmod.streamlined.block.Cable;
import net.streamlinedmod.streamlined.menu.storage.StorageTerminal;
import net.streamlinedmod.streamlined.storage.StorageNetwork;
import net.streamlinedmod.streamlined.storage.StorageNetworks;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class StorageTerminalPart extends CablePart implements ExtendedMenuDataProvider<BlockPos> {

    private static final long POWER_USAGE = 2;

    public StorageTerminalPart(@NonNull Cable host, @NonNull Direction side) {
        super(PartType.STORAGE_TERMINAL, host, side);
    }

    @Override
    public @NonNull InteractionResult onUse(@NonNull Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, this);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public long powerUsage() {
        return POWER_USAGE;
    }

    public @Nullable StorageNetwork network() {
        return host.getLevel() instanceof ServerLevel level ? StorageNetworks.find(level, host.getBlockPos()) : null;
    }

    public boolean stillValid(@NonNull Player player) {
        return host.part(side) == this && Container.stillValidBlockEntity(host, player);
    }

    @Override
    public @NonNull Component getDisplayName() {
        return toItem().getHoverName();
    }

    @Override
    public @NonNull AbstractContainerMenu createMenu(int id, @NonNull Inventory inventory, @NonNull Player player) {
        return new StorageTerminal(id, inventory, this);
    }

    @Override
    public BlockPos getExtraData(@NonNull ServerPlayer player) {
        return host.getBlockPos();
    }

    @Override
    public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, BlockPos> getExtraDataCodec() {
        return BlockPos.STREAM_CODEC;
    }
}
