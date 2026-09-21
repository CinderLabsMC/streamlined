package net.streamlinedmod.streamlined.neoforge;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.datagen.ModBlockLootTableProvider;

import java.util.Set;

/** NeoForge patcht {@code getKnownBlocks()} in Vanillas BlockLootSubProvider; damit wird nur noch für unsere Blöcke geprüft. */
final class NeoForgeBlockLootTables extends ModBlockLootTableProvider {
    NeoForgeBlockLootTables(LootTableSubProvider.Context context) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, context);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ownBlocks();
    }
}
