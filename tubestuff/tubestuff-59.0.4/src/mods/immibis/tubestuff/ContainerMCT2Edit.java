package mods.immibis.tubestuff;

import java.util.ArrayList;
import java.util.List;

import mods.immibis.core.api.util.BaseContainer;
import mods.immibis.tubestuff.TileMCT2.EditingInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerMCT2Edit extends BaseContainer<TileMCT2.EditingInventory> {
	
	private List<Slot> recipeslots = new ArrayList<Slot>();
	
	EditingInventory getInv() {return inv;}
	
	public ContainerMCT2Edit(EntityPlayer player, EditingInventory inv) {
		super(player, inv);
		
		for(int r = 0; r < TileMCT2.numRecipes; r++)
			for(int i = 0; i < 9; i++) {
				Slot s = new Slot(inv, i + r*9, 1000000, 1000000);
				recipeslots.add(s);
				addSlotToContainer(s);
			}
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, 13 + 18*x, 192));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x + y*9 + 9, 13 + 18*x, 134 + 18*y));
	}
	
	public Slot getRecipeSlot(int r, int s) {
		return recipeslots.get(r*9 + s);
	}

}
