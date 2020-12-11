/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 03:23:49 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vazkii.arl.recipe.MultiRecipe;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.quark.base.module.Feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static vazkii.quark.tweaks.feature.StairsMakeMore.findResult;

public class SlabsToBlocks extends Feature {

	public static final Map<IBlockState, ItemStack> slabs = new HashMap<>();

	private static final List<ItemStack> alreadyFound = new ArrayList<>();

	public static int originalSize;
	private MultiRecipe multiRecipe;
	
	@Override
	public void setupConfig() {
		originalSize = loadPropInt("Vanilla stack size", "The stack size for the vanilla slab recipe, used for automatically detecting slab recipes", 6);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void registerRecipe(RegistryEvent.Register<IRecipe> event) {
		ResourceLocation key = new ResourceLocation("quark", "slabs_to_blocks");
		event.getRegistry().register(multiRecipe = new MultiRecipe(key));
	}

	
	@Override
	@SuppressWarnings("deprecation")
	public void postInit() {
		List<ResourceLocation> recipeList = new ArrayList<>(CraftingManager.REGISTRY.getKeys());
		recipeLoop: for(ResourceLocation res : recipeList) {
			IRecipe recipe = CraftingManager.REGISTRY.getObject(res);
			if(recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe) {
				NonNullList<Ingredient> recipeItems;
				if(recipe instanceof ShapedRecipes)
					recipeItems = ((ShapedRecipes) recipe).recipeItems;
				else recipeItems = recipe.getIngredients();

				ItemStack output = recipe.getRecipeOutput();
				if(!output.isEmpty() && output.getCount() == originalSize) {
					ItemStack singleOut = output.copy();
					singleOut.setCount(1);

					for (ItemStack stack : alreadyFound) {
						if (ItemStack.areItemStacksEqual(singleOut, stack))
							continue recipeLoop;
					}

					alreadyFound.add(singleOut);

					Item outputItem = output.getItem();
					Block outputBlock = Block.getBlockFromItem(outputItem);
					if(outputBlock instanceof BlockSlab) {
						ItemStack outStack = findResult(recipeItems, 3);

						if(!outStack.isEmpty()) {
							ItemStack outCopy = outStack.copy();
							if(outCopy.getItemDamage() == OreDictionary.WILDCARD_VALUE)
								outCopy.setItemDamage(0);

							ItemStack in = output.copy();
							in.setCount(1);
							if(in.getItem() instanceof ItemBlock && outCopy.getItem() instanceof ItemBlock) {
								Block block = Block.getBlockFromItem(outCopy.getItem());
								slabs.put(block.getStateFromMeta(outCopy.getItemDamage()), in);
							}
							
							RecipeHandler.addShapelessRecipe(multiRecipe, outCopy, in, in);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
}
