package mods.immibis.core.experimental.mgui1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import mods.immibis.core.ImmibisCore;
import mods.immibis.core.api.net.IPacket;

public class PacketOpenMGUI implements IPacket {
	
	public ResourceLocation guifile;
	public int windowID;
	
	// The identifiers of the controls on the server, indexed by network ID.
	// (This is used to determine the network IDs by applying the mapping in reverse)
	public String[] serverIDs;
	
	// The number of slots in each of the server's item slot controls, indexed by network ID.
	// 0 for controls that are not item slot controls.
	public int[] serverSlotControlNumSlots;
	
	// The bridge inventory index that each slot maps to.
	// This is indexed by container slots, not control network IDs.
	// Container slots are assigned in ascending order of network ID, with the number of slots per control
	// stored in serverSlotControlNumSlots.
	public int[] containerSlotToBridgeInvSlot;
	
	@Override
	public String getChannel() {
		return ImmibisCore.CHANNEL;
	}
	
	@Override
	public byte getID() {
		return ImmibisCore.PACKET_TYPE_S2C_OPEN_MGUI;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void onReceived(EntityPlayer source) {
		
		Minecraft m = Minecraft.getMinecraft();
		
		ContainerMGUIClient c = new ContainerMGUIClient();
		c.windowId = windowID;
		
		c.initChannel(serverIDs);
		c.initSlots(serverSlotControlNumSlots, containerSlotToBridgeInvSlot);
		
		c.initMainWindow(IC_MGUI.createClientWindow(guifile, c.msd));
		
		c.detectAndSendChanges(); // XXX why are we calling this on the client?
		
		m.thePlayer.openContainer = c;
		m.displayGuiScreen(new GuiMGUI(c));
	}
	
	@Override
	public void read(DataInputStream in) throws IOException {
		guifile = new ResourceLocation(in.readUTF(), in.readUTF());
		windowID = in.readInt();
		
		int numIDs = in.readInt();
		serverIDs = new String[numIDs];
		for(int k = 0; k < numIDs; k++)
			serverIDs[k] = in.readUTF();
		
		serverSlotControlNumSlots = new int[numIDs];
		int totalSlots = 0;
		for(int k = 0; k < numIDs; k++) {
			serverSlotControlNumSlots[k] = in.readInt();
			totalSlots += serverSlotControlNumSlots[k]; 
		}
		
		containerSlotToBridgeInvSlot = new int[totalSlots];
		for(int k = 0; k < totalSlots; k++)
			containerSlotToBridgeInvSlot[k] = in.readInt();
	}
	
	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(guifile.getResourceDomain());
		out.writeUTF(guifile.getResourcePath());
		out.writeInt(windowID);
		
		out.writeInt(serverIDs.length);
		for(String s : serverIDs)
			out.writeUTF(s);
		
		assert serverIDs.length == serverSlotControlNumSlots.length;
		int totalSlots = 0;
		for(int i : serverSlotControlNumSlots) {
			out.writeInt(i);
			totalSlots += i;
		}
		
		assert totalSlots == containerSlotToBridgeInvSlot.length;
		for(int i : containerSlotToBridgeInvSlot)
			out.writeInt(i);
	}
}
