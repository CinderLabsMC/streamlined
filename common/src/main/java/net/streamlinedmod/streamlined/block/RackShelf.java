package net.streamlinedmod.streamlined.block;

import dev.architectury.registry.menu.ExtendedMenuDataProvider;
import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
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
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.storage.StorageProvider;
import org.jspecify.annotations.NonNull;

public final class RackShelf extends CinderGeoBlockEntity implements StorageProvider, ExtendedMenuDataProvider<BlockPos> {

    public static final CinderBlockType<RackShelf> TYPE = CinderBlock.builder(ModBlocks.REGISTRAR, "rack_shelf")
            .horizontalFacing()
            .blockEntity(RackShelf::new)
            .ticking()
            .geo()
            .register();

    static {
        EnergyBridge.register(TYPE::blockEntityType);
    }

    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            RackShelf.this.setChanged();
        }

        @Override
        public boolean stillValid(@NonNull Player player) {
            return Container.stillValidBlockEntity(RackShelf.this, player);
        }
    };

    public RackShelf(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void serverTick() {
        // TODO
    }

    @Override
    protected @NonNull Container getDroppedContents() {
        return inventory;
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
        // TODO: Create rack menu
        return null;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, inventory.getItems());
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, inventory.getItems());
    }
}
