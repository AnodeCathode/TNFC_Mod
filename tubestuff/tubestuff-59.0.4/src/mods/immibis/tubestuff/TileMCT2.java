package mods.immibis.tubestuff;

import mods.immibis.core.TileCombined;
import mods.immibis.core.api.util.NBTType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileMCT2 extends TileCombined {
	public static final int numRecipes = 20;
	
	@Override public boolean canUpdate() {return false;}

	private EditingInventory editingInv = new EditingInventory();
	
	public EditingInventory getEditingInventory() {
		return editingInv;
	}

	public static class Recipe {
		public ItemStack[] input = new ItemStack[9];
		public ItemStack cachedOutput;
		
		public void writeToNBT(NBTTagCompound tag) {
			tag.setInteger("inl", input.length);
			for(int k = 0; k < input.length; k++) {
				if(input[k] != null) {
					NBTTagCompound in_t = new NBTTagCompound();
					input[k].writeToNBT(in_t);
					tag.setTag("in" + k, in_t);
				}
			}
		}
		
		public void readFromNBT(NBTTagCompound tag) {
			input = new ItemStack[tag.getInteger("inl")];
			for(int k = 0; k < input.length; k++)
				if(tag.hasKey("in" + k))
					input[k] = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("in" + k));
		}
	}
	
	public class EditingInventory implements IInventory {
		@Override public void closeInventory() {}
		@Override public void openInventory() {}
		@Override
		public ItemStack decrStackSize(int i, int j) {
			ItemStack s = getStackInSlot(i);
			if(s == null) return null;
			if(s.stackSize > j) {return s.splitStack(j);}
			setInventorySlotContents(i, null);
			return s;
		}
		@Override public int getInventoryStackLimit() {return 64;}
		@Override public String getInventoryName() {return "tubestuff.mct2edit";}
		@Override public int getSizeInventory() {return numRecipes * 9;}
		@Override public ItemStack getStackInSlot(int i) {return recipes[i/9].input[i%9];}
		@Override public void setInventorySlotContents(int i, ItemStack itemstack) {recipes[i/9].input[i%9] = itemstack;}
		@Override public ItemStack getStackInSlotOnClosing(int i) {return null;}
		@Override public boolean hasCustomInventoryName() {return false;}
		@Override public boolean isItemValidForSlot(int i, ItemStack itemstack) {return true;}
		@Override public boolean isUseableByPlayer(EntityPlayer entityplayer) {return entityplayer.getDistanceSq(xCoord+0.5, yCoord+0.5, zCoord+0.5) <= 64;}
		@Override public void markDirty() {}
	}
	
	private Recipe[] recipes = new Recipe[numRecipes];
	{for(int k = 0; k < recipes.length; k++) recipes[k] = new Recipe();}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		NBTTagList rlt = new NBTTagList();
		for(Recipe r : recipes) {
			NBTTagCompound rt = new NBTTagCompound();
			r.writeToNBT(rt);
			rlt.appendTag(rt);
		}
		tag.setTag("recipes", rlt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		NBTTagList rlt = tag.getTagList("recipes", NBTType.COMPOUND);
		for(int k = 0; k < rlt.tagCount(); k++) {
			Recipe r = new Recipe();
			r.readFromNBT(rlt.getCompoundTagAt(k));
			recipes[k] = r;
		}
		
		for(int k = rlt.tagCount(); k < recipes.length; k++)
			recipes[k] = new Recipe();
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(TubeStuff.instance, TubeStuff.GUI_MCT2_EDIT, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
}
