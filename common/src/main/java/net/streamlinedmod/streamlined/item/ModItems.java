package net.streamlinedmod.streamlined.item;

import dev.architectury.registry.registries.DeferredRegister;
import net.streamlinedmod.streamlined.Streamlined;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Streamlined.MOD_ID, Registries.ITEM);

    public static void init() {
        ITEMS.register();
    }
}
