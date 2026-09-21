package net.streamlinedmod.streamlined.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.streamlinedmod.streamlined.block.CopperEnergyCableBlock;

public class ModRecipeProvider extends RecipeProvider {

    protected ModRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, CopperEnergyCableBlock.CABLE.get(), 8)
                .pattern("CCC")
                .pattern("RBR")
                .pattern("CCC")
                .define('C', Items.COPPER_INGOT)
                .define('R', Items.REDSTONE)
                .define('B', Items.REDSTONE_BLOCK)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(output);
    }
}
