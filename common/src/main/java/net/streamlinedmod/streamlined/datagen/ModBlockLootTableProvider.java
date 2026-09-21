package net.streamlinedmod.streamlined.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.streamlinedmod.streamlined.block.BatteryBlock;
import net.streamlinedmod.streamlined.block.BasicEnergyCableBlock;
import net.streamlinedmod.streamlined.block.GeneratorBlock;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(Set<Item> explosionResistant, FeatureFlagSet enabledFeatures, Context output) {
        super(explosionResistant, enabledFeatures, output);
    }

    @Override
    protected void generate() {
        dropSelf(BatteryBlock.BATTERY.get());

        dropSelf(BasicEnergyCableBlock.CABLE.get());

        dropSelf(GeneratorBlock.GENERATOR.get());
    }
}
