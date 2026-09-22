package net.streamlinedmod.streamlined.block;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.item.DriveItem;
import net.streamlinedmod.streamlined.item.DriveState;
import net.streamlinedmod.streamlined.item.ServerItem;
import net.streamlinedmod.streamlined.storage.StorageNetworks;
import net.streamlinedmod.streamlined.storage.StorageProvider;
import net.streamlinedmod.streamlined.storage.StoredItem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

public final class RackShelf extends CinderGeoBlockEntity implements StorageProvider {

    public static final CinderBlockType<RackShelf> TYPE = CinderBlock.builder(ModBlocks.REGISTRAR, "rack_shelf")
            .horizontalFacing()
            .blockEntity(RackShelf::new)
            .geo(geo -> geo.itemModel(Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "block/rack_shelf_item")))
            .register();

    public static final int SLOTS = 5;

    private static final int SLOT_PITCH = 3;
    private static final long POWER_PER_SERVER = 2;
    private static final long POWER_PER_DRIVE = 1;
    private static final DriveState[] DRIVE_STATES = DriveState.values();

    private final SimpleContainer servers = new SimpleContainer(SLOTS) {
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

    private DriveState[] leds = new DriveState[0];

    public RackShelf(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NonNull InteractionResult onUseItem(@NonNull ItemStack stack, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
        if (!(stack.getItem() instanceof ServerItem)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        int slot = freeSlot(targetSlot(hit));

        if (slot < 0) {
            return InteractionResult.FAIL;
        }

        if (level != null && !level.isClientSide()) {
            servers.setItem(slot, stack.copyWithCount(1));
            stack.consume(1, player);
            serversChanged();
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull InteractionResult onUse(@NonNull Player player, @NonNull BlockHitResult hit) {
        int slot = targetSlot(hit);

        if (!player.getMainHandItem().isEmpty() || servers.getItem(slot).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level != null && !level.isClientSide()) {
            var server = servers.removeItemNoUpdate(slot);

            if (!player.getInventory().add(server)) {
                Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), server);
            }

            serversChanged();
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void adjustBones(@NonNull CinderBones bones) {
        for (int slot = 0; slot < SLOTS; slot++) {
            var server = servers.getItem(slot);
            var bone = "slot_" + (slot + 1);
            var drives = server.isEmpty() ? null : ServerItem.drives(server);

            bones.visible(bone, drives != null);

            for (int drive = 0; drive < ServerItem.DRIVE_SLOTS; drive++) {
                var driveBone = bone + "_drive_" + (drive + 1);
                var state = drives == null ? null : driveState(drives.get(drive));

                bones.visible(driveBone, state != null);

                for (var led : DRIVE_STATES) {
                    bones.visible(driveBone + "_led_" + led.id(), led == state);
                }
            }
        }
    }

    @Override
    public long insert(@NonNull ItemStack stack, long amount, boolean simulate) {
        long inserted = insert(stack, amount, simulate, true);

        return inserted + insert(stack, amount - inserted, simulate, false);
    }

    @Override
    public long extract(@NonNull ItemStack stack, long amount, boolean simulate) {
        long extracted = 0;

        for (int slot = 0; slot < SLOTS && extracted < amount; slot++) {
            var server = servers.getItem(slot);

            if (server.isEmpty()) {
                continue;
            }

            var drives = ServerItem.drives(server);
            long before = extracted;

            for (var drive : drives) {
                if (drive.getItem() instanceof DriveItem item && extracted < amount) {
                    extracted += item.extract(drive, stack, amount - extracted, simulate);
                }
            }

            if (!simulate && extracted > before) {
                ServerItem.setDrives(server, drives);
                refreshLeds();
            }
        }

        return extracted;
    }

    @Override
    public void collect(@NonNull Consumer<StoredItem> consumer) {
        for (int slot = 0; slot < SLOTS; slot++) {
            var server = servers.getItem(slot);

            if (server.isEmpty()) {
                continue;
            }

            for (var drive : ServerItem.drives(server)) {
                if (drive.getItem() instanceof DriveItem) {
                    DriveItem.contents(drive).items().forEach(consumer);
                }
            }
        }
    }

    @Override
    public long powerUsage() {
        long usage = 0;

        for (int slot = 0; slot < SLOTS; slot++) {
            var server = servers.getItem(slot);

            if (!server.isEmpty()) {
                usage += POWER_PER_SERVER + POWER_PER_DRIVE * ServerItem.driveCount(server);
            }
        }

        return usage;
    }

    @Override
    protected @NonNull Container getDroppedContents() {
        return servers;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (level != null) {
            StorageNetworks.markDirty(level);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level != null) {
            StorageNetworks.markDirty(level);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, servers.getItems());
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        servers.clearContent();
        ContainerHelper.loadAllItems(input, servers.getItems());
        leds = ledStates();
    }

    private long insert(ItemStack stack, long amount, boolean simulate, boolean existingOnly) {
        long inserted = 0;

        for (int slot = 0; slot < SLOTS && inserted < amount; slot++) {
            var server = servers.getItem(slot);

            if (server.isEmpty()) {
                continue;
            }

            var drives = ServerItem.drives(server);
            long before = inserted;

            for (var drive : drives) {
                if (!(drive.getItem() instanceof DriveItem item) || inserted >= amount) {
                    continue;
                }

                if (existingOnly != item.contains(drive, stack)) {
                    continue;
                }

                inserted += item.insert(drive, stack, amount - inserted, simulate);
            }

            if (!simulate && inserted > before) {
                ServerItem.setDrives(server, drives);
                refreshLeds();
            }
        }

        return inserted;
    }

    private void refreshLeds() {
        var current = ledStates();

        if (Arrays.equals(current, leds)) {
            setChanged();
            return;
        }

        leds = current;
        sync();
    }

    private DriveState[] ledStates() {
        var states = new DriveState[SLOTS * ServerItem.DRIVE_SLOTS];

        for (int slot = 0; slot < SLOTS; slot++) {
            var server = servers.getItem(slot);

            if (server.isEmpty()) {
                continue;
            }

            var drives = ServerItem.drives(server);

            for (int drive = 0; drive < ServerItem.DRIVE_SLOTS; drive++) {
                states[slot * ServerItem.DRIVE_SLOTS + drive] = driveState(drives.get(drive));
            }
        }

        return states;
    }

    private static @Nullable DriveState driveState(ItemStack drive) {
        return drive.getItem() instanceof DriveItem item ? item.state(drive) : null;
    }

    private void serversChanged() {
        leds = ledStates();
        sync();

        if (level != null) {
            StorageNetworks.markDirty(level);
        }
    }

    private int freeSlot(int preferred) {
        if (servers.getItem(preferred).isEmpty()) {
            return preferred;
        }

        for (int slot = 0; slot < SLOTS; slot++) {
            if (servers.getItem(slot).isEmpty()) {
                return slot;
            }
        }

        return -1;
    }

    private int targetSlot(BlockHitResult hit) {
        double pixel = (hit.getLocation().y - worldPosition.getY()) * 16;

        return Mth.clamp((int) ((16 - pixel) / SLOT_PITCH), 0, SLOTS - 1);
    }
}
