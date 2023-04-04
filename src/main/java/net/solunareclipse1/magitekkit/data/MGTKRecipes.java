package net.solunareclipse1.magitekkit.data;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

public class MGTKRecipes extends RecipeProvider {
	public MGTKRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    	//SimpleCookingRecipeBuilder.smelting(Ingredient.of(ObjectInit.VOID_ARMOR), ObjectInit.GANTIUM_BLOCK_ITEM.get(), 9001.0f, 1337)
        //	.unlockedBy("has_gantium", has(ObjectInit.GANTIUM_BLOCK_ITEM.get()))
        //	.save(consumer, "gantium")
        //;
    }
}
