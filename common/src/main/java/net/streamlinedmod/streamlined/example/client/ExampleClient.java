package net.streamlinedmod.streamlined.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.streamlinedmod.streamlined.example.ExampleEntities;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

/** Beispiel: Client-Setup (Entity-Renderer, Keybind, Screen öffnen). */
public final class ExampleClient {
    private static final KeyMapping OPEN_SCREEN = new KeyMapping(
            "key.streamlined.open_screen", InputConstants.KEY_G, KeyMapping.Category.MISC);

    private ExampleClient() {}

    public static void init() {
        EntityRendererRegistry.register(ExampleEntities.EXAMPLE_ENTITY, ctx -> new ExampleEntityRenderer(ctx, ExampleEntities.EXAMPLE_ENTITY.get()));
        KeyMappingRegistry.register(OPEN_SCREEN);
        ClientTickEvent.CLIENT_POST.register(mc -> {
            while (OPEN_SCREEN.consumeClick()) {
                Minecraft.getInstance().setScreenAndShow(new ExampleScreen());
            }
        });
    }
}
