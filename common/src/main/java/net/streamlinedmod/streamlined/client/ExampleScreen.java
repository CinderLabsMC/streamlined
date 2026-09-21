package net.streamlinedmod.streamlined.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Beispiel: einfacher GUI-Screen mit Text und Button. */
public class ExampleScreen extends Screen {
    private int clicks;

    public ExampleScreen() {
        super(Component.literal("Example Screen"));
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(Component.literal("Klick mich"), button -> {
            clicks++;
            button.setMessage(Component.literal("Klicks: " + clicks));
        }).bounds(width / 2 - 50, height / 2 - 10, 100, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, height / 2 - 40, 0xFFFFFFFF);
    }
}
