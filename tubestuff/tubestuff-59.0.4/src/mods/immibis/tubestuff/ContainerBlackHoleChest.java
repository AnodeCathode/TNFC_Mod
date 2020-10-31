package mods.immibis.tubestuff;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ContainerBlackHoleChest extends BaseContainer<TileBlackHoleChest> {
	public TileBlackHoleChest chest;
	public int page, maxPages;
	
	public static final int WIDTH = 13;
	public static final int HEIGHT = 6;
	
	private IInventory clientInventory;
	
	public static class CustomSlot extends Slot {

		public CustomSlot(IInventory inv, int index, int x, int y) {
			super(inv, index, x, y);
		}
		
		public void setIndex(int index) {
			ReflectionHelper.setPrivateValue(Slot.class, this, index, 0);
		}
		
	}
	
	public void setPage(int page) {
		this.page = page;
		
		if(!chest.getWorldObj().isRemote) {
			int startIndex = page * WIDTH * HEIGHT;
			for(int k = 0; k < WIDTH * HEIGHT; k++) {
				((CustomSlot)inventorySlots.get(k)).setIndex(startIndex + k);
			}
			detectAndSendChanges();
		}
	}
	
	public ContainerBlackHoleChest(EntityPlayer player, TileBlackHoleChest chest) {
		super(player, chest);
		this.chest = chest;
		
		this.page = 0;
		
		
		inventorySlots.clear();
		
		if(chest.getWorldObj().isRemote) {
			
			clientInventory = new InventoryBasic("", true, WIDTH*HEIGHT);
			
			for(int y = 0; y < HEIGHT; y++)
				for(int x = 0; x < WIDTH; x++) {
					addSlotToContainer(new Slot(clientInventory, x + y*WIDTH, x*18 + 5, y*18 + 4));
				}
			
		} else {
			int startIndex = page * WIDTH * HEIGHT;
			
			for(int y = 0; y < HEIGHT; y++)
				for(int x = 0; x < WIDTH; x++) {
					addSlotToContainer(new CustomSlot(chest, startIndex + x + y * WIDTH, x*18 + 5, y*18 + 4));
				}
		}
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x + y*9 + 9, x * 18 + 5, y * 18 + 123));
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, x * 18 + 5, 181));
	}
	
	@Override
	public void onButtonPressed(int j) {
		if(j == 3)
		{
			// previous page
			if(page > 0)
				setPage(page - 1);
		}
		else if(j == 4)
		{
			// next page
			if(page < chest.maxPages - 1)
				setPage(page + 1);
		}
	}
	
	@Override
	public void updateProgressBar(int bar, int value) {
		if(bar == 0)
			setPage(value);
		else if(bar == 1)
			chest.maxPages = value;
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting) {
		super.addCraftingToCrafters(par1ICrafting);
		
	    par1ICrafting.sendProgressBarUpdate(this, 0, this.page);
        par1ICrafting.sendProgressBarUpdate(this, 1, this.chest.maxPages);
    }
	
	private int lastPage = -1, lastMaxPages = -1;

	//private boolean breakpoint;
	
    @Override
    public void detectAndSendChanges()
    {
    	if(chest.maxPages == -1)
        	chest.updateMaxPages();
        
        if(lastPage != page)
        	sendProgressBarUpdate(0, page);
        if(lastMaxPages != chest.maxPages)
        	sendProgressBarUpdate(1, chest.maxPages);
        
        this.lastPage = page;
        this.lastMaxPages = chest.maxPages;
        
        super.detectAndSendChanges();
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public ItemStack slotClick(int slot, int button, int shift, EntityPlayer player) {
    	
    	if(slot >= 0 && slot < inventorySlots.size()) {
    		Slot s = (Slot)inventorySlots.get(slot);
    		
    		// fixes desync when placing stacks after the end of the inventory
    		if(player.inventory.getItemStack() != null && s.getStack() == null)
    			inventoryItemStacks.set(slot, player.inventory.getItemStack());
    	}
    	
    	ItemStack rv = super.slotClick(slot, button, shift, player);
    	
    	if(player instanceof EntityPlayerMP) {
    		EntityPlayerMP mp = (EntityPlayerMP)player;
    		mp.isChangingQuantityOnly = false;
    		detectAndSendChanges();
    		mp.isChangingQuantityOnly = true;
    	}
    	
    	return rv;
    }
	
	@Override
	public ItemStack transferStackInSlot(int slotIndex) {
		Slot slot = (Slot)inventorySlots.get(slotIndex);
		if(slot == null)
			return null;
		
		try
		{
			int invSlot = (Integer)ReflectionHelper.getPrivateValue(Slot.class, slot, 0);
			if(slot.inventory == player.inventory)
				if(chest.getWorldObj().isRemote)
					BasicInventory.mergeStackIntoRange(player.inventory, clientInventory, invSlot, 0, WIDTH*HEIGHT);
				else
					// using a separate client inventory means they can only shift-click into the current page
					BasicInventory.mergeStackIntoRange(player.inventory, chest, invSlot, page * WIDTH * HEIGHT, (page + 1) * WIDTH * HEIGHT);
			else if(slot.inventory == chest)
				BasicInventory.mergeStackIntoRange(chest, player.inventory, invSlot, 0, 36);
			return null;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
}
