package net.streamlinedmod.streamlined.createmodetab;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.BatteryBlock;
import net.streamlinedmod.streamlined.block.GeneratorBlock;
import net.streamlinedmod.streamlined.block.RackShelfBlock;
import net.streamlinedmod.streamlined.cable.ModCables;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Streamlined.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> GENERAL_TAB = CREATIVE_MODE_TABS.register("general", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).icon(() -> new ItemStack(BatteryBlock.BATTERY.get().asItem())).title(Component.translatable("itemGroup.streamlined.general")).build());

    public static void init() {
        CREATIVE_MODE_TABS.register();

        for (var cable : ModCables.values()) {
            CreativeTabRegistry.append(GENERAL_TAB, cable.block());
        }

        CreativeTabRegistry.append(GENERAL_TAB, BatteryBlock.BATTERY);

        CreativeTabRegistry.append(GENERAL_TAB, GeneratorBlock.GENERATOR);

        CreativeTabRegistry.append(GENERAL_TAB, RackShelfBlock.RACK_SHELF);
    }
}