package net.streamlinedmod.streamlined.blockentity;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import dev.architectury.registry.menu.ExtendedMenuDataProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.streamlinedmod.streamlined.block.RackShelfBlock;
import net.streamlinedmod.streamlined.storage.StorageProvider;
import org.jspecify.annotations.NonNull;

public class RackShelfBlockEntity extends BlockEntity implements StorageProvider, GeoBlockEntity, ExtendedMenuDataProvider<BlockPos> {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            RackShelfBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(@NonNull Player player) {
            return Container.stillValidBlockEntity(RackShelfBlockEntity.this, player);
        }
    };

    // TODO: Change to storage drives
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {

                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return 0;
        }
    };

    public RackShelfBlockEntity(BlockPos pos, BlockState state) {
        super(RackShelfBlock.RACK_SHELF_BE.get(), pos, state);
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("block.streamlined.rack_shelf");
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {

    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, RackShelfBlockEntity gen) {
        // TODO
    }
}
