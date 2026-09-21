package net.streamlinedmod.streamlined.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.block.BatteryBlock;
import net.streamlinedmod.streamlined.block.CopperEnergyCableBlock;
import net.streamlinedmod.streamlined.block.GeneratorBlock;

import java.util.List;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(Set<Item> explosionResistant, FeatureFlagSet enabledFeatures, Context output) {
        super(explosionResistant, enabledFeatures, output);
    }

    @Override
    protected void generate() {
        ownBlocks().forEach(this::dropSelf);
    }

    /** Alle Blöcke dieser Mod. Loader, die die Validierung auf eigene Blöcke beschränken müssen (NeoForge: getKnownBlocks), nutzen diese Liste. */
    public static List<Block> ownBlocks() {
        return List.of(
                BatteryBlock.BATTERY.get(),
                CopperEnergyCableBlock.CABLE.get(),
                GeneratorBlock.GENERATOR.get());
    }
}
