package net.streamlinedmod.streamlined.block;

import dev.architectury.registry.menu.ExtendedMenuDataProvider;
import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntity;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.streamlinedmod.streamlined.energy.EnergyBridge;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.streamlinedmod.streamlined.menu.GeneratorMenu;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class Generator extends CinderBlockEntity implements EnergyProvider, ExtendedMenuDataProvider<BlockPos> {

    public static final CinderBlockType<Generator> TYPE = CinderBlock.builder(ModBlocks.REGISTRAR, "generator")
            .blockEntity(Generator::new)
            .ticking()
            .register();

    static {
        EnergyBridge.register(TYPE::blockEntityType);
    }

    private static final long PER_TICK = 20;
    private static final int BURN_TICKS_PER_ITEM = 400;

    private final SimpleEnergy energy = new SimpleEnergy(1_000, 0, 100);

    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            Generator.this.setChanged();
        }

        @Override
        public boolean stillValid(@NonNull Player player) {
            return Container.stillValidBlockEntity(Generator.this, player);
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

    public Generator(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
        super(type, pos, state);
    }

    public int getBurnTime() {
        return burnTime;
    }

    @Override
    public @NonNull SimpleEnergy getEnergy(@Nullable Direction side) {
        return energy;
    }

    @Override
    public void serverTick() {
        if (burnTime <= 0 && energy.getAmount() < energy.getCapacity()) {
            var fuel = inventory.getItem(0);
            if (fuel.is(ItemTags.COALS)) {
                fuel.shrink(1);
                burnTime = burnTotal = BURN_TICKS_PER_ITEM;
            }
        }

        if (burnTime > 0) {
            burnTime--;
            energy.setAmount(energy.getAmount() + PER_TICK);
        }

        for (var dir : Direction.values()) {
            if (level == null) {
                continue;
            }

            if (!(level.getBlockEntity(worldPosition.relative(dir)) instanceof EnergyProvider provider)) {
                continue;
            }

            var other = provider.getEnergy(dir.getOpposite());
            if (other == null) {
                continue;
            }

            long moved = other.insert(energy.extract(Long.MAX_VALUE, true), true);
            if (moved > 0) {
                other.insert(energy.extract(moved, false), false);
            }
        }
        setChanged();
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
        return new GeneratorMenu(id, playerInventory, inventory, data);
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
