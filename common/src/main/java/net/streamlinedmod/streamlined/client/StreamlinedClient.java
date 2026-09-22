package net.streamlinedmod.streamlined.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.streamlinedmod.streamlined.blockentity.ModBlockEntities;
import net.streamlinedmod.streamlined.example.ExampleEntities;
import net.streamlinedmod.streamlined.geo.renderer.CableBlockRenderer;

public class StreamlinedClient {

    private static final KeyMapping OPEN_SCREEN = new KeyMapping("key.streamlined.open_screen", InputConstants.KEY_G, KeyMapping.Category.MISC);

    public static void init() {
        EntityRendererRegistry.register(ExampleEntities.EXAMPLE_ENTITY, ctx -> new ExampleEntityRenderer(ctx, ExampleEntities.EXAMPLE_ENTITY.get()));

        KeyMappingRegistry.register(OPEN_SCREEN);

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (OPEN_SCREEN.consumeClick()) {
                Minecraft.getInstance().setScreenAndShow(new ExampleScreen());
            }
        });

        ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> BlockEntityRendererRegistry.register(ModBlockEntities.CABLE.get(), CableBlockRenderer::create));
    }
}