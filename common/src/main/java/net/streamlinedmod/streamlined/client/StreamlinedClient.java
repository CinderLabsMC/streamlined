package net.streamlinedmod.streamlined.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.streamlinedmod.streamlined.block.BasicEnergyCableBlock;
import net.streamlinedmod.streamlined.example.ExampleEntities;
import net.streamlinedmod.streamlined.geo.renderer.BasicEnergyCableBlockRenderer;

public class StreamlinedClient {

    private static final KeyMapping OPEN_SCREEN = new KeyMapping(
            "key.streamlined.open_screen", InputConstants.KEY_G, KeyMapping.Category.MISC);

    public static void init() {
        EntityRendererRegistry.register(ExampleEntities.EXAMPLE_ENTITY, ctx -> new ExampleEntityRenderer(ctx, ExampleEntities.EXAMPLE_ENTITY.get()));
        KeyMappingRegistry.register(OPEN_SCREEN);
        ClientTickEvent.CLIENT_POST.register(mc -> {
            while (OPEN_SCREEN.consumeClick()) {
                Minecraft.getInstance().setScreenAndShow(new ExampleScreen());
            }
        });

        // Registry-Objekte existieren beim Mod-Konstruktor (NeoForge) noch nicht -> erst im Client-Setup holen
        ClientLifecycleEvent.CLIENT_SETUP.register(client ->
                BlockEntityRendererRegistry.register(BasicEnergyCableBlock.CABLE_BE.get(), BasicEnergyCableBlockRenderer::create));
    }
}
