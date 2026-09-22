package net.streamlinedmod.streamlined.block;

import dev.architectury.registry.menu.ExtendedMenuDataProvider;
import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntity;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.streamlinedmod.streamlined.menu.workstation.WorkstationMenu;
import org.jspecify.annotations.NonNull;

public final class Workstation extends CinderBlockEntity implements ExtendedMenuDataProvider<BlockPos> {

    public static final CinderBlockType<Workstation> TYPE = CinderBlock.builder(ModBlocks.REGISTRAR, "workstation")
            .blockEntity(Workstation::new)
            .register();

    private final SimpleContainer server = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            Workstation.this.setChanged();
        }

        @Override
        public boolean stillValid(@NonNull Player player) {
            return Container.stillValidBlockEntity(Workstation.this, player);
        }
    };

    public Workstation(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected @NonNull Container getDroppedContents() {
        return server;
    }

    @Override
    public BlockPos getExtraData(@NonNull ServerPlayer player) {
        return worldPosition;
    }

    @Override
    public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, BlockPos> getExtraDataCodec() {
        return BlockPos.STREAM_CODEC;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NonNull Inventory playerInventory, @NonNull Player player) {
        return new WorkstationMenu(id, playerInventory, server);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, server.getItems());
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, server.getItems());
    }
}
