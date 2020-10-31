package mods.immibis.tubestuff;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;

public class InventoryCraftingACT2 extends InventoryCrafting {

	public InventoryCraftingACT2() {
		super(new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer entityplayer) {
				return false;
			}

			@Override
			public void onCraftMatrixChanged(IInventory iinventory) {
		    }
		}, 3, 3);
	}

}
