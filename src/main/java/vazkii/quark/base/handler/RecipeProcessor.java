package vazkii.quark.base.handler;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.util.ProxyRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class RecipeProcessor {

	private static Function<ItemStack, Integer> compositeFunction = stack -> 0;
	private static final List<Consumer<IRecipe>> recipeConsumers = new ArrayList<>();
	
	static { 
		addConsumer(RecipeProcessor::executeWoodReplacementsOverRecipe);
	}
	
	public static void addReplacementFunction(Function<ItemStack, Integer> f) {
		Function<ItemStack, Integer> curr = compositeFunction;
		compositeFunction = stack -> {
				int res = f.apply(stack);
				return res == 0 ? curr.apply(stack) : res;
		};
	}
	
	public static void addWoodReplacements(int size, Block... blocks) {
		for(Block b : blocks)
			addReplacementFunction(stack -> stack.getItem() == Item.getItemFromBlock(b) ? size : 0);
	}
	
	public static void addWoodReplacements(Block... blocks) {
		addWoodReplacements(1, blocks);
	}
	
	public static void addConsumer(Consumer<IRecipe> consumer) {
		recipeConsumers.add(consumer);
	}
	
	public static void runConsumers() {
		List<ResourceLocation> recipeList = new ArrayList<>(CraftingManager.REGISTRY.getKeys());
		for(ResourceLocation res : recipeList) {
			IRecipe recipe = CraftingManager.REGISTRY.getObject(res);
			for(Consumer<IRecipe> consumer : recipeConsumers)
				consumer.accept(recipe);
		}
	}
	
	private static void executeWoodReplacementsOverRecipe(IRecipe recipe) {
		ItemStack out = recipe.getRecipeOutput();
		if(recipe instanceof ShapedRecipes && !out.isEmpty()) {
			int finalSize = compositeFunction.apply(out);
			if(finalSize > 0) {
				ShapedRecipes shaped = (ShapedRecipes) recipe;
				NonNullList<Ingredient> ingredients = shaped.recipeItems;
				for(int i = 0; i < ingredients.size(); i++) {
					Ingredient ingredient = ingredients.get(i);
					if(ingredient.apply(ProxyRegistry.newStack(Blocks.PLANKS)))
						ingredients.set(i, Ingredient.fromStacks(ProxyRegistry.newStack(Blocks.PLANKS, 1, 0)));
				}
				out.setCount(finalSize);
			}
		}
	}
	
}

