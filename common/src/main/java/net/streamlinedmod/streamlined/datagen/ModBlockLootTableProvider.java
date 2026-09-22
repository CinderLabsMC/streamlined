package net.streamlinedmod.streamlined.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.block.Battery;
import net.streamlinedmod.streamlined.block.Generator;
import net.streamlinedmod.streamlined.block.Workstation;
import net.streamlinedmod.streamlined.cable.ModCables;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(Set<Item> explosionResistant, FeatureFlagSet enabledFeatures, LootTableSubProvider.Context output) {
        super(explosionResistant, enabledFeatures, output);
    }

    @Override
    protected void generate() {
        ownBlocks().forEach(this::dropSelf);
    }

    public static List<Block> ownBlocks() {
        var blocks = new ArrayList<Block>();

        blocks.add(Battery.TYPE.block());

        ModCables.values().stream().map(ModCables.Entry::block).map(RegistrySupplier::get).forEach(blocks::add);

        blocks.add(Generator.TYPE.block());

        blocks.add(Workstation.TYPE.block());

        return List.copyOf(blocks);
    }
}