package mods.immibis.core.experimental.mgui1;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import immibis.mgui.MControl;
import immibis.mgui.MHostingEnvironment;
import immibis.mgui.MClientWindow;
import immibis.mgui.hosting.MGUIChannel;
import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.APILocator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMGUIClient extends Container {

	MHostingEnvironment msd = new MHostingEnvironment();
	
	MClientWindow mainWindow;
	
	IInventory clientBackingInventory;
	
	@Override
	public boolean canInteractWith(EntityPlayer ply) {
		return true;
	}

	void initMainWindow(MClientWindow w) {
		if(w == null) throw new NullPointerException();
		if(mainWindow != null) throw new IllegalStateException();
		mainWindow = w;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
		return null;
	}

	private Map<String, Integer> identToNetworkID;
	private String[] networkIDToIdent;
	
	// Initializes the networkID <-> identifier mapping, based on the information initially received from the server.
	void initChannel(String[] serverIDs) {
		networkIDToIdent = serverIDs;
		
		identToNetworkID = new HashMap<>();
		for(int k = 0; k < serverIDs.length; k++)
			identToNetworkID.put(serverIDs[k], Integer.valueOf(k));
		
		msd.sendChannel = new MGUIChannel() {
			@Override
			public void send(String controlID, DataWriter data) {
				Integer netID = identToNetworkID.get(controlID);
				if(netID != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try {
						DataOutputStream ds = new DataOutputStream(baos);
						ds.writeInt(netID.intValue());
						data.write(ds);
					} catch(IOException e) {
						throw new RuntimeException(e);
					}
					
					Packet_MGUI_S2C p = new Packet_MGUI_S2C();
					p.data = baos.toByteArray();
					p.windowID = ContainerMGUIClient.this.windowId;
					
					APILocator.getNetManager().sendToServer(p);
				}
				// else don't send packet, as this control doesn't exist on the server
			}
		};
	}
	
	// Indexed by control network ID
	int[] controlFirstContainerSlot;
	int[] controlNumContainerSlots;
	
	// Initializes the container slot <-> inventory slot mapping (like any other Minecraft container has)
	void initSlots(int[] numControlSlots, int[] containerToBridgeSlotMapping) {
		this.controlNumContainerSlots = numControlSlots;
		
		controlFirstContainerSlot = new int[networkIDToIdent.length];
		int total = 0;
		for(int k = 0; k < controlFirstContainerSlot.length; k++) {
			controlFirstContainerSlot[k] = total;
			total += numControlSlots[k];
		}
		
		int highestBridgeSlot = -1;
		for(int i : containerToBridgeSlotMapping)
			if(i > highestBridgeSlot)
				highestBridgeSlot = i;
		clientBackingInventory = new BasicInventory(highestBridgeSlot + 1);
		
		inventorySlots.clear();
		inventoryItemStacks.clear();
		for(int k = 0; k < containerToBridgeSlotMapping.length; k++) {
			addSlotToContainer(new Slot(clientBackingInventory, containerToBridgeSlotMapping[k], 0, 0));
		}
	}

	int getContainerSlotIndexForControl(String controlID, int slotInControl) {
		Integer netID = this.identToNetworkID.get(controlID);
		if(netID == null)
			return -1;
		
		if(slotInControl >= controlNumContainerSlots[netID.intValue()])
			return -1;
		
		return controlFirstContainerSlot[netID.intValue()] + slotInControl;
	}

	public MControl getControlByNetID(int netID) {
		if(netID < 0 || netID >= networkIDToIdent.length)
			return null;
		return mainWindow.getControl(networkIDToIdent[netID]);
	}
}
