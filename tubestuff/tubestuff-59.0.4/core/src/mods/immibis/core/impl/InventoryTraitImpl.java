package mods.immibis.core.impl;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.traits.IInventoryTrait;
import mods.immibis.core.api.traits.IInventoryTraitUser;
import mods.immibis.core.api.traits.TraitClass;
import mods.immibis.core.api.traits.TraitMethod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

@TraitClass(interfaces={ISidedInventory.class})
public class InventoryTraitImpl implements IInventoryTrait {
	
	private BasicInventory inv;
	private IInventoryTraitUser tile;
	private String invName;
	
	public InventoryTraitImpl(Object tile) {
		try {
			this.tile = (IInventoryTraitUser)tile;
		} catch(ClassCastException e) {
			throw new RuntimeException("Tile '"+tile+"' must implement IInventoryTraitUser.", e);
		}
		
		inv = new BasicInventory(this.tile.getInventorySize());
		
		invName = tile.getClass().getSimpleName();
		if(invName.length() > 15)
			invName = invName.substring(0, 15);
	}

	@TraitMethod @Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return tile.getAccessibleSlots(var1);
	}
	
	@TraitMethod @Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return tile.canInsert(i, j, itemstack);
	}

	@TraitMethod @Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return tile.canExtract(i, j, itemstack);
	}
	
	@TraitMethod @Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return tile.canInsert(i, itemstack);
	}
	
	@TraitMethod @Override public int getSizeInventory() {return inv.contents.length;}
	@TraitMethod @Override public ItemStack getStackInSlot(int i) {return inv.contents[i];}
	@TraitMethod @Override public ItemStack decrStackSize(int i, int j) {return inv.decrStackSize(i, j);}
	@TraitMethod @Override public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack is = getStackInSlot(i);
		setInventorySlotContents(i, null);
		return is;
	}
	@TraitMethod @Override public void setInventorySlotContents(int i, ItemStack itemstack) {inv.contents[i] = itemstack;}
	@TraitMethod @Override public String getInventoryName() {return invName;}
	@TraitMethod @Override public int getInventoryStackLimit() {return 64;}
	@TraitMethod @Override public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		TileEntity te = (TileEntity)tile;
		return !te.isInvalid() && te.getWorldObj() == entityplayer.worldObj && entityplayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5) <= 64;
	}
	@TraitMethod @Override public void openInventory() {}
	@TraitMethod @Override public void closeInventory() {}

	@TraitMethod @Override public boolean hasCustomInventoryName() { return false; }
	@TraitMethod @Override public void markDirty() { }
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		inv.readFromNBT(tag.getTagList("Items", 10));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setTag("Items", inv.writeToNBT());
	}
}
