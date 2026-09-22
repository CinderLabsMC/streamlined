package net.streamlinedmod.streamlined.client;

import net.cinderlabsmc.cinderlib.client.gui.CinderLayout;
import net.cinderlabsmc.cinderlib.client.gui.CinderScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.menu.workstation.WorkstationMenu;
import org.jspecify.annotations.NonNull;

public class WorkstationScreen extends CinderScreen<WorkstationMenu> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public WorkstationScreen(@NonNull WorkstationMenu menu, @NonNull Inventory inventory, @NonNull Component title) {
        super(menu, inventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void build(@NonNull CinderLayout layout) {
        layout.label(30, 24, Component.translatable("gui." + Streamlined.MOD_ID + ".workstation.server"));
        layout.label(76, 24, () -> menu.hasServer()
                ? Component.translatable("gui." + Streamlined.MOD_ID + ".workstation.drives") : Component.empty());
    }
}
