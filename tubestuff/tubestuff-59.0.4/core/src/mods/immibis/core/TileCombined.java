package mods.immibis.core;


import java.util.ArrayList;
import java.util.List;

import mods.immibis.core.api.porting.PortableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;

public abstract class TileCombined extends PortableTileEntity {
	
	public int redstone_output = 0;
	
	public List<ItemStack> getInventoryDrops() {
		if(this instanceof IInventory) {
			IInventory inv = (IInventory)this;
			List<ItemStack> list = new ArrayList<ItemStack>();
			for(int k = 0; k < inv.getSizeInventory(); k++) {
				ItemStack is = inv.getStackInSlot(k);
				if(is != null && is.stackSize > 0) {
					inv.setInventorySlotContents(k, null);
					list.add(is);
				}
			}
			return list;
		}
		
		return new ArrayList<ItemStack>();
	}

	public void onBlockNeighbourChange() {}
	public boolean onBlockActivated(EntityPlayer player) {return false;}
	public void onBlockRemoval() {}
	
	public void notifyNeighbouringBlocks() {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
	}
	
	public void resendDescriptionPacket() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return null;
	}
	
	// look = The closest axis to the direction the player is looking towards
	public void onPlaced(EntityLivingBase player, int look) {}
	
	
	private void notifyComparator(int x, int y, int z) {
		if(worldObj.blockExists(x, y, z)) {
			Block block = worldObj.getBlock(x, y, z);
			if(block.equals(Blocks.unpowered_comparator)|| block.equals(Blocks.powered_comparator))
				worldObj.notifyBlockOfNeighborChange(x, y, z, getBlockType());
		}
	}
	public void notifyComparators() {
		notifyComparator(xCoord-1, yCoord, zCoord);
        notifyComparator(xCoord+1, yCoord, zCoord);
        notifyComparator(xCoord, yCoord, zCoord-1);
        notifyComparator(xCoord, yCoord, zCoord+1);
        notifyComparator(xCoord-2, yCoord, zCoord);
        notifyComparator(xCoord+2, yCoord, zCoord);
        notifyComparator(xCoord, yCoord, zCoord-2);
        notifyComparator(xCoord, yCoord, zCoord+2);
	}
}
