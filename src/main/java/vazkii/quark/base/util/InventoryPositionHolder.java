/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 20, 2019, 10:03 AM (EST)]
 */
package vazkii.quark.base.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class InventoryPositionHolder {

	public static final int NO_SLOT = -1;
	public static final int MAIN_HAND = -2;
	public static final int OFF_HAND = -3;

	public static ItemStack getStack(EntityPlayer player, int slot) {
		switch (slot) {
			case NO_SLOT:
				return ItemStack.EMPTY;
			case MAIN_HAND:
				return player.getHeldItemMainhand();
			case OFF_HAND:
				return player.getHeldItemOffhand();
			default:
				return player.inventory.getStackInSlot(slot);
		}
	}

	public static void setStack(EntityPlayer player, int slot, ItemStack stack) {
		switch (slot) {
			case NO_SLOT:
				break;
			case MAIN_HAND:
				player.setHeldItem(EnumHand.MAIN_HAND, stack);
				break;
			case OFF_HAND:
				player.setHeldItem(EnumHand.OFF_HAND, stack);
				break;
			default:
				player.inventory.setInventorySlotContents(slot, stack);
		}
	}

	public int slot = -1;
	public ItemStack stack = ItemStack.EMPTY;
}
