package net.streamlinedmod.streamlined.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.cable.part.PartType;

import java.util.function.Function;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Streamlined.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> SERVER = register("server", properties -> new ServerItem(properties.stacksTo(1)));

    public static final RegistrySupplier<Item> HARD_DISK_DRIVE = register("hard_disk_drive", properties -> new DriveItem(properties.stacksTo(1), 4_096, 32));
    public static final RegistrySupplier<Item> SOLID_STATE_DRIVE = register("solid_state_drive", properties -> new DriveItem(properties.stacksTo(1), 16_384, 64));

    public static final RegistrySupplier<Item> STORAGE_TERMINAL = register("storage_terminal", properties -> new CablePartItem(properties, PartType.STORAGE_TERMINAL));
    public static final RegistrySupplier<Item> POWER_SUPPLY = register("power_supply", properties -> new CablePartItem(properties, PartType.POWER_SUPPLY));
    public static final RegistrySupplier<Item> CABLE_SEPARATOR = register("cable_separator", properties -> new CablePartItem(properties, PartType.CABLE_SEPARATOR));
    public static final RegistrySupplier<Item> FACADE = register("facade", FacadeItem::new);

    private ModItems() {}

    public static void init() {
        ITEMS.register();
    }

    private static RegistrySupplier<Item> register(String name, Function<Item.Properties, Item> factory) {
        var id = Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, name);

        return ITEMS.register(id, () -> factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
    }
}
