package net.streamlinedmod.streamlined.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.block.BatteryBlock;
import net.streamlinedmod.streamlined.block.GeneratorBlock;
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

        blocks.add(BatteryBlock.BATTERY.get());

        ModCables.values().stream().map(ModCables.Entry::block).map(RegistrySupplier::get).forEach(blocks::add);

        blocks.add(GeneratorBlock.GENERATOR.get());

        return List.copyOf(blocks);
    }
}