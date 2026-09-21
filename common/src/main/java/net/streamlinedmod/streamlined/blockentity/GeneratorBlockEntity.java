package net.streamlinedmod.streamlined.blockentity;

import net.streamlinedmod.streamlined.block.GeneratorBlock;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.streamlinedmod.streamlined.menu.GeneratorMenu;
import dev.architectury.registry.menu.ExtendedMenuDataProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class GeneratorBlockEntity extends BlockEntity implements EnergyProvider, ExtendedMenuDataProvider<BlockPos> {

    private static final long PER_TICK = 20;
    private static final int BURN_TICKS_PER_ITEM = 400;

    private final SimpleEnergy energy = new SimpleEnergy(1_000, 0, 100);

    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            GeneratorBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(@NonNull Player player) {
            return Container.stillValidBlockEntity(GeneratorBlockEntity.this, player);
        }
    };

    private int burnTime;
    private int burnTotal;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case GeneratorMenu.DATA_ENERGY -> (int) energy.getAmount();
                case GeneratorMenu.DATA_CAPACITY -> (int) energy.getCapacity();
                case GeneratorMenu.DATA_BURN -> burnTime;
                case GeneratorMenu.DATA_BURN_TOTAL -> burnTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return GeneratorMenu.DATA_COUNT;
        }
    };

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(GeneratorBlock.GENERATOR_BE.get(), pos, state);
    }

    public int getBurnTime() {
        return burnTime;
    }

    @Override
    public @Nullable SimpleEnergy getEnergy(@Nullable Direction side) {
        return energy;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("block.streamlined.generator");
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
        return new GeneratorMenu(id, playerInventory, inventory, data);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity gen) {
        if (gen.burnTime <= 0 && gen.energy.getAmount() < gen.energy.getCapacity()) {
            ItemStack fuel = gen.inventory.getItem(0);
            if (fuel.is(ItemTags.COALS)) {
                fuel.shrink(1);
                gen.burnTime = gen.burnTotal = BURN_TICKS_PER_ITEM;
            }
        }

        if (gen.burnTime > 0) {
            gen.burnTime--;
            gen.energy.setAmount(gen.energy.getAmount() + PER_TICK);
        }

        for (Direction dir : Direction.values()) {
            if (!(level.getBlockEntity(pos.relative(dir)) instanceof EnergyProvider provider)) continue;
            SimpleEnergy other = provider.getEnergy(dir.getOpposite());
            if (other == null) continue;
            long moved = other.insert(gen.energy.extract(Long.MAX_VALUE, true), true);
            if (moved > 0) other.insert(gen.energy.extract(moved, false), false);
        }
        gen.setChanged();
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("energy", energy.getAmount());
        output.putInt("burn_time", burnTime);
        output.putInt("burn_total", burnTotal);
        ContainerHelper.saveAllItems(output, inventory.getItems());
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        energy.setAmount(input.getLongOr("energy", 0));
        burnTime = input.getIntOr("burn_time", 0);
        burnTotal = input.getIntOr("burn_total", 0);
        ContainerHelper.loadAllItems(input, inventory.getItems());
    }
}
