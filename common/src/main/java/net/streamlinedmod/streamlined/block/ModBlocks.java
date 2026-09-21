package net.streamlinedmod.streamlined.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Streamlined.MOD_ID, Registries.BLOCK);

    public static void init() {
        BatteryBlock.init();
        BasicEnergyCableBlock.init();
        GeneratorBlock.init();

        BLOCKS.register();
    }

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, name);
    }


    public static RegistrySupplier<Block> block(String name, java.util.function.Function<BlockBehaviour.Properties, Block> factory) {
        RegistrySupplier<Block> block = BLOCKS.register(name, () -> factory.apply(BlockBehaviour.Properties.of()
                .strength(1.5f).setId(ResourceKey.create(Registries.BLOCK, id(name)))));
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id(name))).useBlockDescriptionPrefix()));
        return block;
    }
}
