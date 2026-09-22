package net.streamlinedmod.streamlined.client;

import net.cinderlabsmc.cinderlib.client.gui.CinderLayout;
import net.cinderlabsmc.cinderlib.client.gui.CinderScreen;
import net.cinderlabsmc.cinderlib.client.gui.format.AmountFormat;
import net.cinderlabsmc.cinderlib.client.gui.render.GuiSizes;
import net.cinderlabsmc.cinderlib.client.gui.widget.Scrollbar;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.menu.storage.StorageTerminal;
import net.streamlinedmod.streamlined.storage.NetworkStatus;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class StorageTerminalScreen extends CinderScreen<StorageTerminal> {

    private static final int GRID_LEFT = 8;
    private static final int GRID_TOP = 18 - GuiSizes.SLOT_BORDER;
    private static final int GRID_WIDTH = StorageTerminal.COLUMNS * GuiSizes.SLOT_SIZE;
    private static final int GRID_HEIGHT = StorageTerminal.VISIBLE_ROWS * GuiSizes.SLOT_SIZE;
    private static final int SCROLLBAR_LEFT = 174;

    private static final int OVERLAY = 0xA0000000;
    private static final int STATUS_COLOR = 0xFFFF5555;
    private static final int COUNT_COLOR = 0xFFFFFFFF;
    private static final float COUNT_SCALE = 0.5f;

    private Scrollbar scrollbar;

    public StorageTerminalScreen(@NonNull StorageTerminal menu, @NonNull Inventory inventory, @NonNull Component title) {
        super(menu, inventory, title, SCROLLBAR_LEFT + Scrollbar.WIDTH + 7, 114 + GRID_HEIGHT);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void build(@NonNull CinderLayout layout) {
        scrollbar = layout.scrollbar(SCROLLBAR_LEFT, GRID_TOP, GRID_HEIGHT);
        scrollbar.setMaxOffset(menu.getMaxScrollRow());
        scrollbar.scroll(menu.getScrollRow());
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        scrollbar.setMaxOffset(menu.getMaxScrollRow());
        syncScroll();
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        var status = menu.getStatus();

        if (status != NetworkStatus.ONLINE) {
            graphics.fill(GRID_LEFT - 1, GRID_TOP, GRID_LEFT - 1 + GRID_WIDTH, GRID_TOP + GRID_HEIGHT, OVERLAY);
            graphics.centeredText(font, status.message(), GRID_LEFT - 1 + GRID_WIDTH / 2, GRID_TOP + (GRID_HEIGHT - font.lineHeight) / 2, STATUS_COLOR);
        }
    }

    @Override
    protected void extractSlot(@NonNull GuiGraphicsExtractor graphics, @NonNull Slot slot, int mouseX, int mouseY) {
        super.extractSlot(graphics, slot, mouseX, mouseY);

        long count = StorageTerminal.displayCount(slot.getItem());

        if (count <= 1) {
            return;
        }

        var text = AmountFormat.compact(count);
        int right = (int) ((slot.x + GuiSizes.ITEM_SIZE) / COUNT_SCALE);
        int bottom = (int) ((slot.y + GuiSizes.ITEM_SIZE) / COUNT_SCALE);

        graphics.pose().pushMatrix();
        graphics.pose().scale(COUNT_SCALE, COUNT_SCALE);
        graphics.text(font, text, right - font.width(text), bottom - font.lineHeight + 1, COUNT_COLOR, true);
        graphics.pose().popMatrix();
    }

    @Override
    protected @NonNull List<Component> getTooltipFromContainerItem(@NonNull ItemStack stack) {
        var tooltip = super.getTooltipFromContainerItem(stack);
        long count = StorageTerminal.displayCount(stack);

        if (count <= 0) {
            return tooltip;
        }

        var lines = new ArrayList<>(tooltip);
        lines.add(Component.translatable("gui." + Streamlined.MOD_ID + ".storage_terminal.stored", AmountFormat.full(count)).withStyle(ChatFormatting.GRAY));

        return lines;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        boolean handled = super.mouseClicked(event, doubleClick);
        syncScroll();
        return handled;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dragX, double dragY) {
        boolean handled = super.mouseDragged(event, dragX, dragY);
        syncScroll();
        return handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            if (scrollY == 0) {
                return false;
            }

            scrollbar.scroll((int) -Math.signum(scrollY));
        }

        syncScroll();
        return true;
    }

    private void syncScroll() {
        int row = scrollbar.offset();
        if (row == menu.getScrollRow() || minecraft.player == null || minecraft.gameMode == null) {
            return;
        }

        menu.clickMenuButton(minecraft.player, row);
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, row);
    }
}
