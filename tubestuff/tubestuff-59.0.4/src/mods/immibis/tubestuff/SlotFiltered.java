package mods.immibis.tubestuff;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotFiltered extends Slot {
	
	private Item item;
	private int meta;

	public SlotFiltered(IInventory inv, int index, int x, int y, ItemStack allowed) {
		super(inv, index, x, y);
		this.item = allowed.getItem();
		this.meta = allowed.getItemDamage();
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
        return stack.getItem() == item && (meta == -1 || stack.getItemDamage() == meta);
    }

}
