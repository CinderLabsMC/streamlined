package net.streamlinedmod.streamlined.menu.storage;

import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.cable.part.StorageTerminalPart;
import net.streamlinedmod.streamlined.item.ModComponents;
import net.streamlinedmod.streamlined.menu.ModMenus;
import net.streamlinedmod.streamlined.storage.NetworkStatus;
import net.streamlinedmod.streamlined.storage.StorageNetwork;
import net.streamlinedmod.streamlined.storage.StoredItem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class StorageTerminal extends AbstractContainerMenu {

    public static final int DATA_STATUS = 0;
    public static final int DATA_ENTRIES = 1;
    public static final int DATA_COUNT = 2;

    public static final int VISIBLE_ROWS = 8;
    public static final int COLUMNS = 9;
    public static final int VISIBLE_SIZE = VISIBLE_ROWS * COLUMNS;

    private static final int REFRESH_TICKS = 10;

    private final SimpleContainer display = new SimpleContainer(VISIBLE_SIZE);
    private final ContainerData data;
    private final @Nullable StorageTerminalPart terminal;

    private List<StoredItem> entries = List.of();
    private int scrollRow;
    private int ticks;
    private boolean dirty = true;

    public StorageTerminal(int id, Inventory inventory) {
        this(id, inventory, null);
    }

    public StorageTerminal(int id, Inventory inventory, @Nullable StorageTerminalPart terminal) {
        super(ModMenus.STORAGE_TERMINAL.get(), id);

        this.terminal = terminal;
        this.data = new SimpleContainerData(DATA_COUNT);

        addDisplaySlots();
        addPlayerSlots(inventory);
        addDataSlots(data);
    }

    private void addDisplaySlots() {
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                addSlot(new DisplaySlot(display, column + row * COLUMNS, 8 + column * 18, 18 + row * 18));
            }
        }
    }

    private void addPlayerSlots(Inventory inventory) {
        int offset = (VISIBLE_ROWS - 4) * 18;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + offset));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 161 + offset));
        }
    }

    public @NonNull NetworkStatus getStatus() {
        return NetworkStatus.byId(data.get(DATA_STATUS));
    }

    public int getScrollRow() {
        return scrollRow;
    }

    public int getMaxScrollRow() {
        int rows = Mth.positiveCeilDiv(data.get(DATA_ENTRIES), COLUMNS);

        return Math.max(rows - VISIBLE_ROWS, 0);
    }

    public static long displayCount(@NonNull ItemStack stack) {
        return stack.getOrDefault(ModComponents.DISPLAY_COUNT.get(), 0L);
    }

    @Override
    public boolean clickMenuButton(@NonNull Player player, int id) {
        scrollRow = Mth.clamp(id, 0, getMaxScrollRow());
        dirty = true;
        return true;
    }

    @Override
    public void clicked(int slotId, int button, @NonNull ContainerInput input, @NonNull Player player) {
        if (slotId < 0 || slotId >= VISIBLE_SIZE) {
            super.clicked(slotId, button, input, player);
            return;
        }

        var network = network();

        if (network == null || !network.isOnline()) {
            return;
        }

        if (input == ContainerInput.PICKUP) {
            if (getCarried().isEmpty()) {
                extractToCarried(network, slotId, button == CONTAINER_CLICK_SECONDARY);
            } else {
                insertCarried(network, button == CONTAINER_CLICK_SECONDARY);
            }
        } else if (input == ContainerInput.QUICK_MOVE) {
            extractToInventory(network, slotId, player);
        }

        dirty = true;
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        var network = network();

        if (index < VISIBLE_SIZE || network == null) {
            return ItemStack.EMPTY;
        }

        var slot = slots.get(index);
        var stack = slot.getItem();
        long inserted = network.insert(stack, stack.getCount(), false);

        if (inserted > 0) {
            stack.shrink((int) inserted);
            slot.setChanged();
            dirty = true;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canTakeItemForPickAll(@NonNull ItemStack stack, @NonNull Slot slot) {
        return !(slot instanceof DisplaySlot) && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean canDragTo(@NonNull Slot slot) {
        return !(slot instanceof DisplaySlot);
    }

    @Override
    public void broadcastChanges() {
        if (terminal != null && (dirty || ++ticks >= REFRESH_TICKS)) {
            refresh();
        }

        super.broadcastChanges();
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return terminal == null || terminal.stillValid(player);
    }

    private @Nullable StorageNetwork network() {
        return terminal == null ? null : terminal.network();
    }

    private void refresh() {
        dirty = false;
        ticks = 0;

        var network = network();
        var status = network == null ? NetworkStatus.OFFLINE : network.status();

        entries = network == null ? List.of() : network.items();

        data.set(DATA_STATUS, status.ordinal());
        data.set(DATA_ENTRIES, entries.size());

        scrollRow = Mth.clamp(scrollRow, 0, getMaxScrollRow());

        for (int slot = 0; slot < VISIBLE_SIZE; slot++) {
            var entry = entry(slot);
            display.setItem(slot, entry == null ? ItemStack.EMPTY : displayStack(entry));
        }
    }

    private @Nullable StoredItem entry(int slot) {
        int index = scrollRow * COLUMNS + slot;

        return index < entries.size() ? entries.get(index) : null;
    }

    private void extractToCarried(StorageNetwork network, int slot, boolean half) {
        var entry = entry(slot);

        if (entry == null) {
            return;
        }

        var template = entry.create(1);
        long amount = Math.min(template.getMaxStackSize(), entry.count());

        if (half) {
            amount = (amount + 1) / 2;
        }

        long extracted = network.extract(template, amount, false);

        if (extracted > 0) {
            setCarried(entry.create((int) extracted));
        }
    }

    private void insertCarried(StorageNetwork network, boolean single) {
        var carried = getCarried();
        long inserted = network.insert(carried, single ? 1 : carried.getCount(), false);

        carried.shrink((int) inserted);
    }

    private void extractToInventory(StorageNetwork network, int slot, Player player) {
        var entry = entry(slot);

        if (entry == null) {
            return;
        }

        var template = entry.create(1);
        long extracted = network.extract(template, Math.min(template.getMaxStackSize(), entry.count()), false);

        if (extracted <= 0) {
            return;
        }

        var stack = entry.create((int) extracted);

        player.getInventory().add(stack);

        if (!stack.isEmpty()) {
            network.insert(stack, stack.getCount(), false);
        }
    }

    private static ItemStack displayStack(StoredItem entry) {
        var stack = entry.create(1);

        stack.set(ModComponents.DISPLAY_COUNT.get(), entry.count());

        return stack;
    }

    private static final class DisplaySlot extends Slot {

        DisplaySlot(SimpleContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(@NonNull ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(@NonNull Player player) {
            return false;
        }
    }
}
