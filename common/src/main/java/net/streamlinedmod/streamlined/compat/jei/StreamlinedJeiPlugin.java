package net.streamlinedmod.streamlined.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.CopperEnergyCableBlock;
import net.streamlinedmod.streamlined.block.BatteryBlock;
import net.streamlinedmod.streamlined.block.GeneratorBlock;
import org.jspecify.annotations.NonNull;

@JeiPlugin
public class StreamlinedJeiPlugin implements IModPlugin {

    @Override
    public @NonNull Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "jei");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addItemStackInfo(new ItemStack(GeneratorBlock.GENERATOR.get()), Component.translatable("jei.streamlined.generator"));
        registration.addItemStackInfo(new ItemStack(BatteryBlock.BATTERY.get()), Component.translatable("jei.streamlined.battery"));
        registration.addItemStackInfo(new ItemStack(CopperEnergyCableBlock.CABLE.get()), Component.translatable("jei.streamlined.cable"));
    }
}
