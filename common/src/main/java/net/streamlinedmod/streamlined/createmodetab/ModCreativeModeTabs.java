package net.streamlinedmod.streamlined.createmodetab;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.Battery;
import net.streamlinedmod.streamlined.block.Generator;
import net.streamlinedmod.streamlined.block.RackShelf;
import net.streamlinedmod.streamlined.block.Workstation;
import net.streamlinedmod.streamlined.cable.ModCables;
import net.streamlinedmod.streamlined.item.ModItems;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Streamlined.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> GENERAL_TAB = CREATIVE_MODE_TABS.register("general", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).icon(() -> new ItemStack(Battery.TYPE.block())).title(Component.translatable("itemGroup.streamlined.general")).build());

    public static void init() {
        CREATIVE_MODE_TABS.register();

        for (var cable : ModCables.values()) {
            CreativeTabRegistry.append(GENERAL_TAB, cable.block());
        }

        CreativeTabRegistry.append(GENERAL_TAB, Battery.TYPE.blockEntry());

        CreativeTabRegistry.append(GENERAL_TAB, Generator.TYPE.blockEntry());

        CreativeTabRegistry.append(GENERAL_TAB, RackShelf.TYPE.blockEntry());

        CreativeTabRegistry.append(GENERAL_TAB, Workstation.TYPE.blockEntry());

        CreativeTabRegistry.append(GENERAL_TAB, ModItems.SERVER);
        CreativeTabRegistry.append(GENERAL_TAB, ModItems.HARD_DISK_DRIVE);
        CreativeTabRegistry.append(GENERAL_TAB, ModItems.SOLID_STATE_DRIVE);
        CreativeTabRegistry.append(GENERAL_TAB, ModItems.STORAGE_TERMINAL);
        CreativeTabRegistry.append(GENERAL_TAB, ModItems.POWER_SUPPLY);
        CreativeTabRegistry.append(GENERAL_TAB, ModItems.CABLE_SEPARATOR);
        CreativeTabRegistry.append(GENERAL_TAB, ModItems.FACADE);
    }
}