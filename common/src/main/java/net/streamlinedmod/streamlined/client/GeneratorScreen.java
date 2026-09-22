package net.streamlinedmod.streamlined.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.streamlinedmod.streamlined.menu.generator.GeneratorMenu;
import org.jspecify.annotations.NonNull;

public class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {

    private static final int PANEL = 0xFFC6C6C6;
    private static final int SLOT = 0xFF8B8B8B;
    private static final int BAR_BACK = 0xFF373737;
    private static final int BAR_ENERGY = 0xFFD03030;
    private static final int BAR_FLAME = 0xFFF0A020;

    public GeneratorScreen(GeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, PANEL);
        for (Slot slot : menu.slots) {
            graphics.fill(leftPos + slot.x - 1, topPos + slot.y - 1, leftPos + slot.x + 17, topPos + slot.y + 17, SLOT);
        }

        int barX = leftPos + 30, barTop = topPos + 20, barW = 12, barH = 50;
        graphics.fill(barX, barTop, barX + barW, barTop + barH, BAR_BACK);
        int capacity = Math.max(menu.getCapacity(), 1);
        int filled = barH * menu.getEnergy() / capacity;
        graphics.fill(barX, barTop + barH - filled, barX + barW, barTop + barH, BAR_ENERGY);

        int flameX = leftPos + 80, flameY = topPos + 56, flameW = 16;
        graphics.fill(flameX, flameY, flameX + flameW, flameY + 4, BAR_BACK);
        if (menu.getBurnTotal() > 0) {
            graphics.fill(flameX, flameY, flameX + flameW * menu.getBurn() / menu.getBurnTotal(), flameY + 4, BAR_FLAME);
        }
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(font, menu.getEnergy() + " / " + menu.getCapacity() + " FE", 100, 20, 0xFF404040, false);
    }
}
