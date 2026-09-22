package net.streamlinedmod.streamlined.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.Battery;
import net.streamlinedmod.streamlined.block.Generator;
import net.streamlinedmod.streamlined.cable.ModCables;
import org.jspecify.annotations.NonNull;

@JeiPlugin
public class StreamlinedJeiPlugin implements IModPlugin {

    @Override
    public @NonNull Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "jei");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addItemStackInfo(new ItemStack(Generator.TYPE.block()), Component.translatable("jei.streamlined.generator"));
        registration.addItemStackInfo(new ItemStack(Battery.TYPE.block()), Component.translatable("jei.streamlined.battery"));

        for (var cable : ModCables.values()) {
            registration.addItemStackInfo(new ItemStack(cable.block().get()), Component.translatable("jei.streamlined.cable"));
        }
    }
}