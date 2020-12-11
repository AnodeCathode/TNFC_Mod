/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [21/03/2016, 00:05:19 (GMT)]
 */
package vazkii.quark.vanity.recipe;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.recipe.ModRecipe;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.vanity.feature.DyableElytra;

import javax.annotation.Nonnull;

public class ElytraDyeingRecipe extends ModRecipe {
	
	public ElytraDyeingRecipe() {
		super(new ResourceLocation("quark", "elytra_dyeing"));
	}
	
	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
		boolean foundSource = false;
		boolean foundTarget = false;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof ItemElytra) {
					if(foundTarget)
						return false;
					foundTarget = true;
				} else if(getDye(stack) != -1) {
					if(foundSource)
						return false;
					foundSource = true;
				} else return false;
			}
		}

		return foundSource && foundTarget;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		int source = -1;
		ItemStack target = ItemStack.EMPTY;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty()) {
				int dye = getDye(stack);
				if(dye != -1)
					source = dye;
				else target = stack;
			}
		}

		if(!target.isEmpty()) {
			ItemStack copy = target.copy();
			ItemNBTHelper.setInt(copy, DyableElytra.TAG_ELYTRA_DYE, source);
			return copy;
		}

		return ItemStack.EMPTY;
	}
	
	private int getDye(ItemStack stack) {
		if(stack.getItem() instanceof ItemDye)
			return stack.getItemDamage();
		
		int[] ids = OreDictionary.getOreIDs(stack);
		for (int id : ids) {
			String tag = OreDictionary.getOreName(id);
			int dye = LibMisc.OREDICT_DYES.indexOf(tag);
			if (dye > -1)
				return dye;
		}
		
		return -1;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(Items.ELYTRA);
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean canFit(int x, int y) {
		return true;
	}
	
	@Override
	@Nonnull
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.withSize(2, Ingredient.EMPTY);
		list.set(0, Ingredient.fromStacks(new ItemStack(Items.ELYTRA)));
		
		ItemStack[] stacks = new ItemStack[16];
		for(int i = 0; i < 16; i++)
			stacks[i] = new ItemStack(Items.DYE, 1, i);
		list.set(1, Ingredient.fromStacks(stacks));
		return list;
	}

}
