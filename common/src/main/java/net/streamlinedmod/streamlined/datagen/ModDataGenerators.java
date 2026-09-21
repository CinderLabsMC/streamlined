package net.streamlinedmod.streamlined.datagen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

/**
 * Baut die Datagen-Registries (Rezepte und Loot-Tables sind seit 26.x "reloadable registries").
 * Loader-unabhängig; der Aufruf passiert im Datagen-Event des jeweiligen Loaders.
 */
public final class ModDataGenerators {
    private ModDataGenerators() {}

    /**
     * @param blockLoot Factory für die Block-Loot-Tables; ist ein Parameter, weil NeoForge dafür eine Unterklasse mit
     *                  {@code getKnownBlocks()} braucht (sonst prüft Vanilla, ob JEDER Block der Welt eine Loot-Table hat).
     */
    public static RegistrySetBuilder reloadableRegistries(LootTableSubProvider.Factory blockLoot) {
        return new RegistrySetBuilder()
                .add(new MultiRegistryBootstrap() {
                    @Override
                    public Set<ResourceKey<? extends Registry<?>>> requestedRegistries() {
                        return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
                    }

                    @Override
                    public void run(BootstrapGetter getter) {
                        new ModRecipeProvider(getter.get(Registries.RECIPE), getter.get(Registries.ADVANCEMENT)).buildRecipes();
                    }
                })
                .add(Registries.LOOT_TABLE, new LootTableProvider(Set.of(), List.of(
                        new LootTableProvider.SubProviderEntry(blockLoot, LootContextParamSets.BLOCK))));
    }
}
