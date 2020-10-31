package mods.immibis.core.impl.crossmod;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.reactor.IReactorChamber;
import ic2.api.tile.ExplosionWhitelist;
import ic2.core.AdvRecipe;
import ic2.core.AdvShapelessRecipe;

import java.util.List;

import mods.immibis.core.api.crossmod.ICrossModIC2;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

public class CrossModIC2_Impl implements ICrossModIC2 {
	@Override
	public Item getWrenchItem() {
		return IC2Items.getItem("wrench").getItem();
	}
	
	@Override
	public boolean isShapedRecipe(IRecipe r) {
		return r instanceof AdvRecipe;
	}
	
	@Override
	public boolean isShapelessRecipe(IRecipe r) {
		return r instanceof AdvShapelessRecipe;
	}
	
	@Override
	public ItemStack[][] getShapedRecipeInputs(IRecipe r) throws ClassCastException {
		// handles IC2 shaped recipes
		AdvRecipe ar = (AdvRecipe)r;
		
		ItemStack[][] recipeInputs = new ItemStack[9][];
		
		int width = ar.inputWidth;
		int height = ar.input.length / width;
		Object[] input = ar.input;
		
		for(int x = 0; x < 3; x++) {
			for(int y = 0; y < 3; y++) {
				int slot = x + y * 3;
				
				Object recipe_item = null;
				if(x < width && y < height)
					recipe_item = input[x + y*width];
				
				if(recipe_item == null)
					recipeInputs[slot] = new ItemStack[0];
				else {
					List<ItemStack> allowedInputs = AdvRecipe.expand(recipe_item);
					recipeInputs[slot] = zeroStackSizes((ItemStack[])allowedInputs.toArray(new ItemStack[0]));
				}
			}
		}
		
		return recipeInputs;
	}
	
	@Override
	public ItemStack[][] getShapelessRecipeInputs(IRecipe r) throws ClassCastException {
		AdvShapelessRecipe asr = (AdvShapelessRecipe)r;
		Object[] input = asr.input;
		
		int pos = 0;
		
		ItemStack[][] recipeInputs = new ItemStack[9][];
		
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++, pos++) {
				if(pos < input.length) {
					List<ItemStack> allowedItems = AdvRecipe.expand(input[pos]);
					recipeInputs[pos] = zeroStackSizes((ItemStack[])allowedItems.toArray(new ItemStack[0]));
				} else {
					recipeInputs[pos] = new ItemStack[0];
				}
			}
		}
		
		ItemStack[][] rv = new ItemStack[pos][];
		for(int k = 0; k < pos; k++)
			rv[k] = recipeInputs[k];
		return rv;
	}
	
	private static ItemStack[] zeroStackSizes(ItemStack[] ar) {
		ItemStack[] rv = new ItemStack[ar.length];
		for(int k = 0; k < ar.length; k++) {
			if(ar[k] == null)
				rv[k] = null;
			else {
				ItemStack is = ar[k].copy();
				is.stackSize = 0;
				rv[k] = is;
			}
		}
		return rv;
	}

	@Override
	public void addEnetTile(Object tile) throws ClassCastException {
		if(((TileEntity)tile).getWorldObj().isRemote)
			return;
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile)tile));
	}

	@Override
	public void removeEnetTile(Object tile) throws ClassCastException {
		if(((TileEntity)tile).getWorldObj().isRemote)
			return;
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)tile));
	}

	@Override
	public boolean isElectricItem(ItemStack item) {
		return item.getItem() instanceof IElectricItem;
	}

	@Override
	public int dischargeElectricItem(ItemStack stack, int amount, int tier, boolean ignoreRateLimit, boolean simulate) {
		return (int)(ElectricItem.manager.discharge(stack, amount, tier, ignoreRateLimit, true, simulate) + Math.random());
	}

	@Override
	public boolean isReactorChamber(IBlockAccess world, int i, int j, int k) {
		return world.getTileEntity(i, j, k) instanceof IReactorChamber;
	}

	@Override
	public void addExplosionWhitelist(Block block) {
		ExplosionWhitelist.addWhitelistedBlock(block);
	}

	@Override
	public ItemStack getItem(String name) {
		return IC2Items.getItem(name);
	}
}
