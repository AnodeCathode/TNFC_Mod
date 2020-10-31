package mods.immibis.core.impl.crossmod;

import mods.immibis.core.api.crossmod.ICrossModIC2;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.IBlockAccess;

public class CrossModIC2_Default implements ICrossModIC2 {

	@Override
	public Item getWrenchItem() {
		return null;
	}

	@Override
	public boolean isShapedRecipe(IRecipe r) {
		return false;
	}

	@Override
	public boolean isShapelessRecipe(IRecipe r) {
		return false;
	}

	@Override
	public ItemStack[][] getShapedRecipeInputs(IRecipe r) throws ClassCastException {
		throw new ClassCastException();
	}

	@Override
	public ItemStack[][] getShapelessRecipeInputs(IRecipe r) throws ClassCastException {
		throw new ClassCastException();
	}

	@Override
	public void addEnetTile(Object tile) throws ClassCastException {
	}

	@Override
	public void removeEnetTile(Object tile) throws ClassCastException {
	}

	@Override
	public boolean isElectricItem(ItemStack item) {
		return false;
	}

	@Override
	public int dischargeElectricItem(ItemStack stack, int amount, int tier, boolean ignoreRateLimit, boolean simulate) {
		return 0;
	}

	@Override
	public boolean isReactorChamber(IBlockAccess world, int i, int j, int k) {
		return false;
	}

	@Override
	public void addExplosionWhitelist(Block block) {
	}

	@Override
	public ItemStack getItem(String name) {
		return null;
	}

}
