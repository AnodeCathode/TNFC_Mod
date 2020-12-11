/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jun 09, 2019, 09:04 AM (EST)]
 */
package vazkii.quark.oddities.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotCachingItemHandler extends SlotItemHandler {
	public SlotCachingItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Nonnull
	@Override
	public ItemStack getStack() {
		if (caching)
			return cached;
		return super.getStack();
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int amount) {
		if (caching) {
			ItemStack newStack = cached.copy();
			int trueAmount = Math.min(amount, cached.getCount());
			cached.shrink(trueAmount);
			newStack.setCount(trueAmount);
			return newStack;
		}
		return super.decrStackSize(amount);
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		super.putStack(stack);
		if (caching)
			cached = stack;
	}

	private ItemStack cached = ItemStack.EMPTY;

	private boolean caching = false;

	public static void cache(Container container) {
		for (Slot slot : container.inventorySlots) {
			if (slot instanceof SlotCachingItemHandler) {
				SlotCachingItemHandler thisSlot = (SlotCachingItemHandler) slot;
				thisSlot.cached = slot.getStack();
				thisSlot.caching = true;
			}
		}
	}

	public static void applyCache(Container container) {
		for (Slot slot : container.inventorySlots) {
			if (slot instanceof SlotCachingItemHandler) {
				SlotCachingItemHandler thisSlot = (SlotCachingItemHandler) slot;
				if (thisSlot.caching) {
					slot.putStack(thisSlot.cached);
					thisSlot.caching = false;
				}
			}
		}
	}
}
