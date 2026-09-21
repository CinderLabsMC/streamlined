package net.streamlinedmod.streamlined.menu;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.streamlinedmod.streamlined.Streamlined;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Streamlined.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<GeneratorMenu>> GENERATOR =
            MENUS.register("generator", () -> MenuRegistry.ofExtended((id, inventory, pos) -> new GeneratorMenu(id, inventory), BlockPos.STREAM_CODEC));

    private ModMenus() {}

    public static void init() {
        MENUS.register();
    }
}
