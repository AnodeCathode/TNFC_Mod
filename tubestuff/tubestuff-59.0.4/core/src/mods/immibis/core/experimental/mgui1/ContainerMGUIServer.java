package mods.immibis.core.experimental.mgui1;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import immibis.mgui.MControl;
import immibis.mgui.MEventHandler;
import immibis.mgui.MServerWindow;
import immibis.mgui.hosting.MGUIChannel;
import mods.immibis.core.api.APILocator;
import mods.immibis.core.experimental.mgui1.BridgeUtils.BridgeInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

class ContainerMGUIServer extends Container {

	MServerWindow mainWindow;
	MEventHandler eventHandler;
	MGUIChannel channel;
	
	private BridgeUtils.BridgeInventory bridgeInventory;
	
	@Override
	public boolean canInteractWith(EntityPlayer ply) {
		return bridgeInventory.isUseableByPlayer(ply);
	}

	void initMainWindow(MServerWindow w, Map<String, IInventory> inventories, MEventHandler eh) {
		if(w == null || inventories == null || eh == null) throw new NullPointerException();
		if(mainWindow != null) throw new IllegalStateException();
		mainWindow = w;
		eventHandler = eh;
		eventHandler.onAttach(w);
	}
	
	@Override
	public void detectAndSendChanges() {
		if(eventHandler != null) {
			try {
				eventHandler.update();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		super.detectAndSendChanges();
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
		/* TODO */
		return null;
	}

	void initSlots(BridgeInventory bridgeInventory, int[] containerToBridgeSlotMap) {
		this.bridgeInventory = bridgeInventory;
		
		inventorySlots.clear();
		inventoryItemStacks.clear();
		for(int bridgeSlot : containerToBridgeSlotMap) {
			addSlotToContainer(new Slot(bridgeInventory, bridgeSlot, 0, 0));
		}
	}
	
	private String[] networkIDToIdent;
	private Map<String, Integer> identToNetworkID;
	void initChannel(String[] networkIDs) {
		this.networkIDToIdent = networkIDs;
		
		this.identToNetworkID = new HashMap<>();
		for(int k = 0; k < networkIDs.length; k++)
			identToNetworkID.put(networkIDs[k], Integer.valueOf(k));
		
		this.channel = new MGUIChannel() {
			@Override
			public void send(String controlID, DataWriter data) {
				Integer netID = identToNetworkID.get(controlID);
				if(netID == null)
					throw new IllegalArgumentException("Unknown control identifier '"+controlID+"'; not in mapping. (Mapping is "+identToNetworkID+")");
				
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
				p.windowID = ContainerMGUIServer.this.windowId;
				
				for(Object c : ContainerMGUIServer.this.crafters)
					if(c instanceof EntityPlayerMP)
						APILocator.getNetManager().sendToClient(p, (EntityPlayerMP)c);
			}
		};
	}

	public MControl getControlByNetID(int netID) {
		if(netID < 0 || netID >= this.networkIDToIdent.length)
			return null;
		return mainWindow.getControlByID(networkIDToIdent[netID]);
	}

}
