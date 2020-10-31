package mods.immibis.tubestuff;

import static mods.immibis.tubestuff.TileAutoCraftingMk2.*;

import java.util.List;
import java.util.Random;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.SlotFake;
import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ContainerAutoCraftingMk2 extends BaseContainer<TileAutoCraftingMk2> {
	private int invStart;
	
	public TileAutoCraftingMk2 tile;
	
	public ContainerAutoCraftingMk2(EntityPlayer player, TileAutoCraftingMk2 table) {
		super(player, table);
		
		this.tile = table;
		
		addSlotToContainer(new Slot(table, SLOT_OUTPUT, 134, 124));
		for(int x = 0; x < 3; x++)
			for(int y = 0; y < 3; y++)
				addSlotToContainer(new SlotFake(table, x + y * 3 + START_RECIPE, x * 18 + 31, y * 18 + 107));
		for(int y = 0; y < 5; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(table, x + y * 9 + START_INPUT, x * 18 + 13, y * 19 + 6));

		invStart = inventorySlots.size();
		
		for(int x = 0; x < 9; x++)
		{
			for(int y = 0; y < 3; y++)
				addSlotToContainer(new Slot(player.inventory, x + y*9 + 9, x * 18 + 13, y * 18 + 168));
			addSlotToContainer(new Slot(player.inventory, x, x * 18 + 13, 226));
		}
		
		forceUpdateCount = table.forceContainerUpdateCount;
	}
	
	private boolean prevCraftMany = false;
	private int forceUpdateCount = 0;
	private int forceUpdateTicks = 0;
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		if(tile.craftMany != prevCraftMany) {
			prevCraftMany = tile.craftMany;
			sendProgressBarUpdate(0, prevCraftMany ? 1 : 0);
		}
		
		
		
		// ??????
		if(forceUpdateCount != inv.forceContainerUpdateCount) {
			forceUpdateCount = inv.forceContainerUpdateCount;
			
			if(forceUpdateTicks == 0)
				forceUpdateTicks = 2;
		}
		
		if(forceUpdateTicks > 0 && --forceUpdateTicks == 0) {
			resendRecipeSlots();
		}
	}
	
	@Override
	public void updateProgressBar(int par1, int par2) {
		if(par1 == 0)
			tile.craftMany = par2 != 0;
	}
	
	@Override
	public ItemStack transferStackInSlot(int slot) {
		Slot src = (Slot)inventorySlots.get(slot);
		try
		{
			int slotIndex = (Integer)ReflectionHelper.getPrivateValue(Slot.class, src, 0);
			if(slot < invStart)
			{
				BasicInventory.mergeStackIntoRange(inv, player.inventory, slotIndex, 0, 36);
			}
			else
				BasicInventory.mergeStackIntoRange(player.inventory, inv, slotIndex, START_INPUT, START_INPUT + SIZE_INPUT);
		}
		catch(Exception e)
		{
			return null;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void resendRecipeSlots() {
		for(ICrafting ic : (List<ICrafting>)crafters)
			for(Slot s : (List<Slot>)inventorySlots)
				if(s instanceof SlotFake)
					ic.sendSlotContents(this, s.slotNumber, s.getStack());
	}
	
	private Random random = new Random();
	
	@Override
	public void onActionPacket(IPacket p) {
		if(p instanceof PacketACT2RecipeUpdate) {
			
			PacketACT2RecipeUpdate pru = (PacketACT2RecipeUpdate)p;
			
			if(!pru.isPacketValid)
				return;
			
			// this uses a random item from each slot, then finds and sets the recipe from that
			for(int k = 0; k < 9; k++) {
				if(pru.stacks[k].length == 0)
					inv.setInventorySlotContents(TileAutoCraftingMk2.START_RECIPE + k, null);
				else {
					ItemStack is = pru.stacks[k][random.nextInt(pru.stacks[k].length)];
					is.stackSize = 0;
					inv.setInventorySlotContents(TileAutoCraftingMk2.START_RECIPE + k, is);
				}
			}
			
			inv.cacheOutput();
			inv.setMultiInputsFromRecipe();
			
		}
	}
	
	@Override
	public void onButtonPressed(int id) {
		switch(id) {
		case 0:
			tile.craftMany = !tile.craftMany;
			break;
			
		case 1:
			tile.setMultiInputsFromRecipe();
			break;
			
		case 2:
			for(int k = 0; k < 9; k++)
				tile.setInventorySlotContents(TileAutoCraftingMk2.START_RECIPE + k, null);
			break;
		}
	}
}
