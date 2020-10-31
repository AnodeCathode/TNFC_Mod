package mods.immibis.tubestuff;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.net.PacketUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketBlockBreakerDescription implements IPacket {

	public ItemStack tool;
	public int x, y, z;
	public byte facing;
	public boolean isBreaking;
	
	public PacketBlockBreakerDescription() {}
	
	public PacketBlockBreakerDescription(int x, int y, int z, ItemStack tool, byte facing, boolean isBreaking) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.tool = tool;
		this.facing = facing;
		this.isBreaking = isBreaking;
	}
	
	@Override
	public byte getID() {
		return TubeStuff.PKT_BLOCK_BREAKER_DESC;
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		facing = in.readByte();
		isBreaking = in.readBoolean();
		
		tool = PacketUtils.readItemStack(in);
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);
		out.writeByte(facing);
		out.writeBoolean(isBreaking);
		PacketUtils.writeItemStack(tool, out);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onReceived(EntityPlayer source) {
		TileEntity te = net.minecraft.client.Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
		if(te instanceof TileBlockBreaker) {
			TileBlockBreaker tbb = (TileBlockBreaker)te;
			tbb.tool = tool;
			tbb.facing = facing;
			tbb.isBreaking = isBreaking;
		}
	}
	
	@Override
	public String getChannel() {
		return TubeStuff.CHANNEL;
	}

}
