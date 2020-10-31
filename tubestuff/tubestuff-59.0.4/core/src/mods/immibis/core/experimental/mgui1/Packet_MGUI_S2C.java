package mods.immibis.core.experimental.mgui1;

import immibis.mgui.MControl;
import immibis.mgui.MGUI;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import mods.immibis.core.ImmibisCore;
import mods.immibis.core.api.net.IPacket;

public class Packet_MGUI_S2C implements IPacket {
	
	int windowID;
	byte[] data;
	
	@Override
	public String getChannel() {
		return ImmibisCore.CHANNEL;
	}
	@Override
	public byte getID() {
		return ImmibisCore.PACKET_TYPE_S2C_MGUI;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void onReceived(EntityPlayer source) {
		Container c = Minecraft.getMinecraft().thePlayer.openContainer;
		if(c instanceof ContainerMGUIClient && c.windowId == windowID) {
			try {
				DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));
				MControl control = ((ContainerMGUIClient)c).getControlByNetID(din.readInt());
				if(control != null)
					MGUI.receiveChannelCommand(control, din);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	@Override
	public void read(DataInputStream in) throws IOException {
		windowID = in.readInt();
		data = new byte[in.readInt()];
		in.readFully(data);
	}
	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(windowID);
		out.writeInt(data.length);
		out.write(data);
	}
}
