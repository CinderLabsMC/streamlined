package net.streamlinedmod.streamlined.example;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.streamlinedmod.streamlined.Streamlined;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ExampleItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Streamlined.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () ->
            new Item(new Item.Properties().setId(
                    ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "example_item")))));

    private ExampleItems() {}

    public static void init() {
        ITEMS.register();
    }
}
