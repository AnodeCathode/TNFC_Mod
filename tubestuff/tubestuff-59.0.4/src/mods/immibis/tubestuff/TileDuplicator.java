package mods.immibis.tubestuff;

import mods.immibis.core.TileCombined;
import mods.immibis.core.api.porting.SidedProxy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;

public class TileDuplicator extends TileCombined implements IInventory, IDuplicator {
	
	@Override public boolean canUpdate() {return false;}

	public ItemStack item = null;
	
	private class ItemEditingInventory implements IInventory {

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int var1) {
			return item;
		}

		@Override
		public ItemStack decrStackSize(int var1, int var2) {
			if(var1 != 0 || item == null)
				return null;
			if(var2 >= item.stackSize) {
				ItemStack i = item;
				item = null;
				return i;
			}
			ItemStack i = item.copy();
			i.stackSize = var2;
			item.stackSize -= var2;
			return i;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int var1) {
			return null;
		}

		@Override
		public void setInventorySlotContents(int var1, ItemStack var2) {
			if(var1 == 0)
				item = var2;
		}

		@Override
		public String getInventoryName() {
			return "Duplicator";
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer var1) {
			if (!SidedProxy.instance.isOp(var1))
				return false;
			if(worldObj.getTileEntity(xCoord, yCoord, zCoord) != TileDuplicator.this)
	            return false;
	        double distance = var1.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D);
			return distance <= 64;
		}

		@Override
		public void openInventory() {
		}

		@Override
		public void closeInventory() {
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return true;
		}
	}
	
	public TileDuplicator() {
	}
	
	@Override
	public void onPlaced(EntityLivingBase player, int look) {
		if(!(player instanceof EntityPlayer) || !SidedProxy.instance.isOp((EntityPlayer)player)) {
			if(player instanceof EntityPlayer)
				((EntityPlayer)player).addChatMessage(new ChatComponentTranslation("tubestuff.duplicator.placeFail"));
			worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air, 0, 2);
		}
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(worldObj.isRemote)
			return true;
		
		if(!SidedProxy.instance.isOp(player)) {
			player.addChatMessage(new ChatComponentTranslation("tubestuff.duplicator.openFail"));
			return true;
		}
		
		player.openGui(TubeStuff.instance, TubeStuff.GUI_DUPLICATOR, worldObj, xCoord, yCoord, zCoord);
		
		return true;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(item != null)
		{
			NBTTagCompound itemTag = new NBTTagCompound();
			item.writeToNBT(itemTag);
			tag.setTag("item", itemTag);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound itemTag = tag.getCompoundTag("item");
		if(itemTag != null)
			item = ItemStack.loadItemStackFromNBT(itemTag);
	}
	
	@Override
	public int getSizeInventory() {
		return (item == null ? 0 : 1);
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return ItemStack.copyItemStack(item);
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		if(item == null)
			return null;
		if(var2 >= item.stackSize)
			return item.copy();
		else
		{
			ItemStack _new = item.copy();
			_new.stackSize = var2;
			return _new;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		// There are no empty "slots"...
	}

	@Override
	public String getInventoryName() {
		return "Duplicator";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return false;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public IInventory getGuiInventory() {
		return new ItemEditingInventory();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}
}
