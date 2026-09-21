package net.streamlinedmod.streamlined.createmodetab;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.BatteryBlock;
import net.streamlinedmod.streamlined.block.BasicEnergyCableBlock;
import net.streamlinedmod.streamlined.block.GeneratorBlock;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Streamlined.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> GENERAL_TAB = CREATIVE_MODE_TABS.register("general", () -> {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .icon(() -> new ItemStack(BatteryBlock.BATTERY.get().asItem()))
                .title(Component.translatable("itemGroup.streamlined.general"))
                .build();
    });

    public static void init() {
        CREATIVE_MODE_TABS.register();

        CreativeTabRegistry.append(GENERAL_TAB, BatteryBlock.BATTERY, BasicEnergyCableBlock.CABLE, GeneratorBlock.GENERATOR);
    }
}
