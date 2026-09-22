package net.streamlinedmod.streamlined.menu.generator;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.menu.ModMenus;
import org.jspecify.annotations.NonNull;

public class GeneratorMenu extends AbstractContainerMenu {

    public static final int DATA_ENERGY = 0;
    public static final int DATA_CAPACITY = 1;
    public static final int DATA_BURN = 2;
    public static final int DATA_BURN_TOTAL = 3;
    public static final int DATA_COUNT = 4;

    private static final int FUEL_SLOT = 0;
    private static final int PLAYER_START = 1;
    private static final int PLAYER_END = PLAYER_START + 36;

    private final Container container;
    private final ContainerData data;

    public GeneratorMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(1), new SimpleContainerData(DATA_COUNT));
    }

    public GeneratorMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.GENERATOR.get(), id);
        this.container = container;
        this.data = data;

        addSlot(new Slot(container, FUEL_SLOT, 80, 35) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return stack.is(ItemTags.COALS);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    public int getEnergy() {
        return data.get(DATA_ENERGY);
    }

    public int getCapacity() {
        return data.get(DATA_CAPACITY);
    }

    public int getBurn() {
        return data.get(DATA_BURN);
    }

    public int getBurnTotal() {
        return data.get(DATA_BURN_TOTAL);
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        boolean moved = index == FUEL_SLOT
                ? moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)
                : moveItemStackTo(stack, FUEL_SLOT, FUEL_SLOT + 1, false);

        if (!moved) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
            return copy;
        }

        slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return container.stillValid(player);
    }
}
