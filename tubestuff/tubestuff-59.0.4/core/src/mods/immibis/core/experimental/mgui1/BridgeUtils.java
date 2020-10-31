package mods.immibis.core.experimental.mgui1;

import immibis.mgui.MControl;
import immibis.mgui.MWindow;
import immibis.mgui.controls.MItemSlotControl;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

class BridgeUtils {
	static class BridgeInventory implements IInventory {
		private IInventory[] invs;
		private IInventory[] slotToSourceInventory;
		private int[] slotToSourceIndex;
		
		@Override
		public void closeInventory() {
			for(IInventory i : invs)
				i.closeInventory();
		}
		
		@Override
		public void openInventory() {
			for(IInventory i : invs)
				i.openInventory();
		}
		
		@Override
		public ItemStack decrStackSize(int slot, int amt) {
			return slotToSourceInventory[slot].decrStackSize(slotToSourceIndex[slot], amt);
		}
		
		@Override
		public String getInventoryName() {
			return "MGUI Combined Inventory";
		}
		
		@Override
		public int getInventoryStackLimit() {
			return 64; // XXX
		}
		
		@Override
		public int getSizeInventory() {
			return slotToSourceInventory.length;
		}
		
		@Override
		public ItemStack getStackInSlot(int slot) {
			return slotToSourceInventory[slot].getStackInSlot(slotToSourceIndex[slot]);
		}
		
		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			return slotToSourceInventory[slot].getStackInSlotOnClosing(slotToSourceIndex[slot]);
		}
		
		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}
		
		@Override
		public boolean isItemValidForSlot(int slot, ItemStack item) {
			return slotToSourceInventory[slot].isItemValidForSlot(slotToSourceIndex[slot], item);
		}
		
		@Override
		public boolean isUseableByPlayer(EntityPlayer ply) {
			for(IInventory inv : invs)
				if(!inv.isUseableByPlayer(ply))
					return false;
			return true;
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack item) {
			slotToSourceInventory[slot].setInventorySlotContents(slotToSourceIndex[slot], item);
		}

		@Override
		public void markDirty() {
			for(IInventory inv : invs)
				inv.markDirty();
		}
	}
	
	
	static final class BridgeGenResult {
		BridgeInventory bridgeInventory;
		int[] containerToBridgeSlotMap;
	}
	
	// The "bridge inventory" is a server-side inventory that encapsulates the link to multiple source inventories
	// The server treats the bridge inventory the same way the client treats the client backing inventory.
	// The bridge inventory has the same layout as the client backing inventory.
	// The client backing inventory gets synced with the bridge inventory using Minecraft's normal mechanisms.
	// Note: this method assumes each inventory has at most one name.
	static BridgeGenResult generateBridgeAndSlotMap(MWindow window, String[] networkIDList, int totalContainerSlots, Map<String, IInventory> inventorySources) {
		// First: find all the container slots, and get their inventory and invslot.
		String[] cslotToInv = new String[totalContainerSlots];
		int[] cslotToInvSlot = new int[totalContainerSlots];
		int currentCSlot = 0;
		
		for(String c_ident : networkIDList) {
			MControl c = window.getControlByID(c_ident);
			assert c != null;
			
			if(c instanceof MItemSlotControl) {
				MItemSlotControl asISC = (MItemSlotControl)c;
				
				int firstSlot = asISC.getFirstSlot();
				int numSlots = asISC.getNumSlots();
				String invName = asISC.getSourceInventoryName();
				
				assert currentCSlot + numSlots <= totalContainerSlots;
				
				for(int k = 0; k < numSlots; k++) {
					cslotToInv[currentCSlot] = invName;
					cslotToInvSlot[currentCSlot] = firstSlot + k;
					currentCSlot++;
				}
			}
		}
		assert currentCSlot == totalContainerSlots;
		
		// Then, assign a bslot for each cslot, except where two cslots have the same source slot, in which case they get the same bslot.
		Map<String, Integer> sourceSlotToBSlot = new HashMap<>();
		int[] cslotToBSlot = new int[totalContainerSlots];
		int nextBSlot = 0;
		List<IInventory> bslotToInv = new ArrayList<>();
		List<Integer> bslotToInvSlot = new ArrayList<>();
		for(int k = 0; k < totalContainerSlots; k++) {
			String sourceSlotIdentifier = cslotToInv[k] + " " + cslotToInvSlot[k];
			Integer existingBSlot = sourceSlotToBSlot.get(sourceSlotIdentifier);
			if(existingBSlot != null)
				cslotToBSlot[k] = existingBSlot.intValue();
			else {
				bslotToInv.add(inventorySources.get(cslotToInv[k]));
				bslotToInvSlot.add(cslotToInvSlot[k]);
				cslotToBSlot[k] = nextBSlot;
				nextBSlot++;
			}
		}
		
		BridgeInventory binv = new BridgeInventory();
		binv.slotToSourceInventory = bslotToInv.toArray(new IInventory[bslotToInv.size()]);
		binv.slotToSourceIndex = toIntArray(bslotToInvSlot);
		binv.invs = inventorySources.values().toArray(new IInventory[inventorySources.size()]);
		
		BridgeGenResult rv = new BridgeGenResult();
		rv.containerToBridgeSlotMap = cslotToBSlot;
		rv.bridgeInventory = binv;
		return rv;
	}

	private static int[] toIntArray(List<Integer> list) {
		int[] rv = new int[list.size()];
		for(int k = 0; k < rv.length; k++)
			rv[k] = list.get(k).intValue();
		return rv;
	}
}
