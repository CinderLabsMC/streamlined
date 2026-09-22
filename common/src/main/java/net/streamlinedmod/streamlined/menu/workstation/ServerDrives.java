package net.streamlinedmod.streamlined.menu.workstation;

import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.item.ServerItem;
import org.jspecify.annotations.NonNull;

import java.util.List;

final class ServerDrives implements Container {

    private final Container servers;

    ServerDrives(@NonNull Container servers) {
        this.servers = servers;
    }

    boolean hasServer() {
        return servers.getItem(WorkstationMenu.SERVER_SLOT).getItem() instanceof ServerItem;
    }

    @Override
    public int getContainerSize() {
        return ServerItem.DRIVE_SLOTS;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return !hasServer() || ServerItem.drives(server()).stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NonNull ItemStack getItem(int slot) {
        return hasServer() ? ServerItem.drives(server()).get(slot) : ItemStack.EMPTY;
    }

    @Override
    public @NonNull ItemStack removeItem(int slot, int amount) {
        if (!hasServer()) {
            return ItemStack.EMPTY;
        }

        var drives = ServerItem.drives(server());
        var removed = ContainerHelper.removeItem(drives, slot, amount);

        ServerItem.setDrives(server(), drives);
        setChanged();

        return removed;
    }

    @Override
    public @NonNull ItemStack removeItemNoUpdate(int slot) {
        if (!hasServer()) {
            return ItemStack.EMPTY;
        }

        var drives = ServerItem.drives(server());
        var removed = ContainerHelper.takeItem(drives, slot);

        ServerItem.setDrives(server(), drives);

        return removed;
    }

    @Override
    public void setItem(int slot, @NonNull ItemStack stack) {
        if (!hasServer()) {
            return;
        }

        var drives = ServerItem.drives(server());

        drives.set(slot, stack);
        ServerItem.setDrives(server(), drives);
        setChanged();
    }

    @Override
    public void setChanged() {
        servers.setChanged();
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return servers.stillValid(player);
    }

    @Override
    public void clearContent() {
        if (hasServer()) {
            ServerItem.setDrives(server(), List.of());
        }
    }

    private ItemStack server() {
        return servers.getItem(WorkstationMenu.SERVER_SLOT);
    }
}
