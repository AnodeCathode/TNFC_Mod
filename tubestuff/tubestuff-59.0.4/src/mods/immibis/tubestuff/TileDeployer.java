package mods.immibis.tubestuff;


import java.util.Arrays;

import mods.immibis.core.TileBasicInventory;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileDeployer extends TileBasicInventory {

	// used by renderer if it crashed
	public boolean disableCustomRender, disableAllItemRender;
	
	public byte facing;
	
	public EntityPlayerFakeTS player;
	
	public TileDeployer() {
		super(9, "tile.tubestuff.deployer.name");
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(TubeStuff.isValidWrench(player.inventory.getCurrentItem()) && !worldObj.isRemote) {
			facing = (byte)((facing + 1) % 6);
			resendDescriptionPacket();
			return true;
		}
		
		if(!worldObj.isRemote)
			player.openGui(TubeStuff.instance, TubeStuff.GUI_DEPLOYER, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
	
	private boolean wasPowered;
	
	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, facing, null);
	}
	
	@Override
	public void onDataPacket(S35PacketUpdateTileEntity packet) {
		facing = (byte)packet.func_148853_f();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void onBlockNeighbourChange() {
		if(worldObj.isRemote)
			return;
		if(wasPowered == worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
			return;
		wasPowered = !wasPowered;
		
		if(wasPowered)
			activate();
	}
	
	private void prepareFakePlayer() {
		if(player == null)
			player = new EntityPlayerFakeTS(worldObj);
		
		ForgeDirection facing = ForgeDirection.VALID_DIRECTIONS[this.facing];
		
		player.posX = xCoord + 0.5 + 0.6*facing.offsetX;
		player.posY = yCoord + 0.5 + 0.6*facing.offsetY;
		player.posZ = zCoord + 0.5 + 0.6*facing.offsetZ;
		
		for(int k = 0; k < 9; k++)
			player.inventory.mainInventory[k] = inv.contents[k];
	}
	
	private void auxOutput(ItemStack s) {
		if(s == null)
			return;
		s = inv.mergeStackIntoRange(s, 0, 9);
		if(s != null)
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, s));
	}
	
	private void unprepareFakePlayer() {
		for(int k = 0; k < 9; k++)
			inv.contents[k] = player.inventory.mainInventory[k];
		for(int k = 9; k < 36; k++)
			auxOutput(player.inventory.mainInventory[k]);
		for(ItemStack s : player.inventory.armorInventory)
			auxOutput(s);
		Arrays.fill(player.inventory.mainInventory, null);
		Arrays.fill(player.inventory.armorInventory, null);
	}
	
	private void activate() {
		prepareFakePlayer();
		for(int k = 0; k < 9; k++)
			if(activate(k)) {
				ItemStack[] inv = player.inventory.mainInventory;
				if(inv[k] != null && inv[k].stackSize == 0)
					inv[k] = null;
				break;
			}
		unprepareFakePlayer();
	}
	
	private boolean activate(int slot) {
		ItemStack s = player.inventory.mainInventory[slot];
		if(s == null)
			return false;
		
		player.inventory.currentItem = slot;
		
		ForgeDirection facing = ForgeDirection.VALID_DIRECTIONS[this.facing];
		int tx = xCoord + facing.offsetX, ty = yCoord + facing.offsetY, tz = zCoord + facing.offsetZ;
		int side;
		
		player.rotationYaw = 0;
		player.rotationPitch = 0;
		
		switch(this.facing) {
		case Dir.PX: player.rotationYaw = -90; break;
		case Dir.NX: player.rotationYaw = 90; break;
		case Dir.PZ: player.rotationYaw = 0; break;
		case Dir.NZ: player.rotationYaw = 180; break;
		case Dir.PY: player.rotationPitch = 90; break;
		case Dir.NY: player.rotationPitch = -90; break;
		}
		
		if(!worldObj.blockExists(tx, ty, tz))
			return false;
		
		if(worldObj.getBlock(tx, ty, tz).getMaterial() != Material.air) {
			// use on block directly in front of deployer
			side = this.facing ^ 1;
		} else if(!worldObj.blockExists(tx+facing.offsetX, ty+facing.offsetY, tz+facing.offsetZ)) {
			return false;
		} else if(worldObj.isSideSolid(tx+facing.offsetX, ty+facing.offsetY, tz+facing.offsetZ, facing.getOpposite()) && s.getItem() instanceof ItemBlock) {
			// place ItemBlock on block opposite deployer
			tx += facing.offsetX;
			ty += facing.offsetY;
			tz += facing.offsetZ;
			side = this.facing ^ 1;
		} else if(worldObj.getBlock(tx, ty-1, tz).getMaterial() != Material.air) {
			// place on block below
			ty--;
			side = Dir.PY;
		} else {
			tx = xCoord;
			ty = yCoord;
			tz = zCoord;
			side = this.facing;
		}
		
		if(!worldObj.blockExists(tx, ty, tz))
			return false;
		
		if(s.getItem().onItemUseFirst(s, player, worldObj, tx, ty, tz, side, 0.5f, 0.5f, 0.5f))
			return true;
		
		if(tx != xCoord || ty != yCoord || tz != zCoord) {
			Block block = worldObj.getBlock(tx, ty, tz);
			if(block.onBlockActivated(worldObj, tx, ty, tz, player, side, 0.5f, 0.5f, 0.5f))
				return true;
		}
		
		//if(s.getItem() instanceof ItemBlock && !((ItemBlock)s.getItem()).canPlaceItemBlockOnSide(worldObj, tx, ty, tz, side, player, s))
		//	return false;
		
		if(s.tryPlaceItemIntoWorld(player, worldObj, tx, ty, tz, side, 0.5f, 0.5f, 0.5f))
			return true;
		
		int oldC = s.stackSize;
		ItemStack oldS = s;
		s = s.useItemRightClick(worldObj, player);
		if(s != oldS || (s != null && s.stackSize != oldC)) {
			player.inventory.mainInventory[slot] = s;
			return true;
		}
		
		return false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("facing", facing);
		tag.setBoolean("wasPowered", wasPowered);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		facing = tag.getByte("facing");
		wasPowered = tag.getBoolean("wasPowered");
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public void onPlaced(EntityLivingBase player, int look2) {
		super.onPlaced(player, look2);
		
		facing = (byte)(1 ^ look2);
	}

	public int getComparatorOutput() {
		return Container.calcRedstoneFromInventory(this);
	}
}