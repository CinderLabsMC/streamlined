package net.streamlinedmod.streamlined.menu.workstation;

import net.cinderlabsmc.cinderlib.menu.CinderMenu;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.item.DriveItem;
import net.streamlinedmod.streamlined.item.ServerItem;
import net.streamlinedmod.streamlined.menu.ModMenus;
import org.jspecify.annotations.NonNull;

public class WorkstationMenu extends CinderMenu {

    public static final int SERVER_SLOT = 0;

    private final Container server;
    private final ServerDrives drives;

    public WorkstationMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(1));
    }

    public WorkstationMenu(int id, Inventory inventory, Container server) {
        super(ModMenus.WORKSTATION.get(), id);

        this.server = server;
        this.drives = new ServerDrives(server);

        addMachineSlot(new Slot(server, SERVER_SLOT, 30, 35) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return stack.getItem() instanceof ServerItem;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        for (int drive = 0; drive < ServerItem.DRIVE_SLOTS; drive++) {
            addMachineSlot(new Slot(drives, drive, 76 + drive * 18, 35) {
                @Override
                public boolean mayPlace(@NonNull ItemStack stack) {
                    return drives.hasServer() && stack.getItem() instanceof DriveItem;
                }

                @Override
                public boolean mayPickup(@NonNull Player player) {
                    return drives.hasServer();
                }

                @Override
                public boolean isActive() {
                    return drives.hasServer();
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    public boolean hasServer() {
        return drives.hasServer();
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return server.stillValid(player);
    }
}
