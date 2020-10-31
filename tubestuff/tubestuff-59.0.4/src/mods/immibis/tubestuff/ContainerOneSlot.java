package mods.immibis.tubestuff;

import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerOneSlot extends BaseContainer<IInventory> {

	public ContainerOneSlot(EntityPlayer player, IInventory inv) {
		super(player, inv);
		
		addSlotToContainer(new Slot(inv, 0, 86, 24));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x + y*9 + 9, x * 18 + 14, y * 18 + 67));
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, x * 18 + 14, 125));
	}
	
	@Override
	public ItemStack transferStackInSlot(int i) {
		return null;
	}

}
