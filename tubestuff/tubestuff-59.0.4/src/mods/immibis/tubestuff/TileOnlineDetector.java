package mods.immibis.tubestuff;

import java.util.List;
import java.util.UUID;

import mods.immibis.core.TileCombined;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;

public class TileOnlineDetector extends TileCombined {
	public UUID owner;
	
	@Override
	public void onPlaced(EntityLivingBase player, int look) {
		if(player instanceof EntityPlayer)
			owner = ((EntityPlayer)player).getGameProfile().getId(); // TODO check correctness
		else
			owner = new UUID(0, 0);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeToNBT(par1nbtTagCompound);
		par1nbtTagCompound.setString("owner", owner.toString());
		par1nbtTagCompound.setBoolean("out", redstone_output != 0);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readFromNBT(par1nbtTagCompound);
		try {
			owner = UUID.fromString(par1nbtTagCompound.getString("owner"));
		} catch(IllegalArgumentException e) {
			owner = new UUID(0, 0);
		}
		redstone_output = par1nbtTagCompound.getBoolean("out") ? 15 : 0;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, redstone_output, null);
	}
	
	@Override
	public void onDataPacket(S35PacketUpdateTileEntity packet) {
		redstone_output = packet.func_148853_f();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	private int updateTime = 0; 
	
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote && --updateTime < 0) {
			updateTime = 20;
			updateNow();
		}
	}
	
	public void updateNow() {
		int old = redstone_output;
		
		redstone_output = 0;
		
		for(EntityPlayer pl : (List<EntityPlayer>)MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if(pl.getGameProfile().getId().equals(owner)) {
				redstone_output = 15;
				break;
			}
		}
		
		if(redstone_output != old) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		}
	}
}
