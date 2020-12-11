/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 05, 2019, 17:10 AM (EST)]
 */
package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.client.ClientReflectiveAccessor;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageRotateArrows;
import vazkii.quark.base.util.CommonReflectiveAccessor;
import vazkii.quark.base.util.InventoryPositionHolder;

public class RotateArrowTypes extends Feature {

	private static boolean isBow(ItemStack bow) {
		return !bow.isEmpty() && bow.getItem() instanceof ItemBow;
	}

	private static void rotateSlot(InventoryPositionHolder holder, ItemBow bowItem, EntityPlayer player, int direction, int slot) {
		ItemStack stack = InventoryPositionHolder.getStack(player, slot);
		if (CommonReflectiveAccessor.isArrow(bowItem, stack)) {
			if (direction < 1) {
				if (holder.slot == InventoryPositionHolder.NO_SLOT)
					holder.slot = slot;

				InventoryPositionHolder.setStack(player, slot, holder.stack);
				holder.stack = stack;
			} else {
				InventoryPositionHolder.setStack(player, holder.slot, stack);

				if (holder.slot == InventoryPositionHolder.NO_SLOT)
					holder.stack = stack;
				holder.slot = slot;
			}
		}
	}

	public static ItemBow getHeldBow(EntityPlayer player) {
		ItemStack bow = player.getHeldItemMainhand();
		if (!isBow(bow))
			bow = player.getHeldItemOffhand();

		if (!isBow(bow))
			return null;

		return (ItemBow) bow.getItem();
	}

	public static void rotateArrows(ItemBow bowItem, EntityPlayer player, int direction) {
		InventoryPositionHolder holder = new InventoryPositionHolder();

		boolean foundSecondArrow = false;
		boolean foundArrow = false;
		ItemStack checkAgainst = ItemStack.EMPTY;
		for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
			ItemStack stack = player.inventory.getStackInSlot(slot);
			if (CommonReflectiveAccessor.isArrow(bowItem, stack)) {
				if (foundArrow && (!ItemStack.areItemsEqual(checkAgainst, stack) || !ItemStack.areItemStackTagsEqual(checkAgainst, stack))) {
					foundSecondArrow = true;
					break;
				} else if (!foundArrow) {
					foundArrow = true;
					checkAgainst = stack;
				}
			}
		}

		if (!foundSecondArrow)
			return;

		rotateSlot(holder, bowItem, player, direction, InventoryPositionHolder.MAIN_HAND);
		rotateSlot(holder, bowItem, player, direction, InventoryPositionHolder.OFF_HAND);

		for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
			if (slot != player.inventory.currentItem && slot != player.inventory.mainInventory.size() +
					EntityEquipmentSlot.OFFHAND.getSlotIndex() - 1)
				rotateSlot(holder, bowItem, player, direction, slot);
		}

		InventoryPositionHolder.setStack(player, holder.slot, holder.stack);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMouseInput(MouseEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		if (player != null && player.isSneaking() && !player.isSpectator() && mc.currentScreen == null) {
			ItemBow bowItem = getHeldBow(player);
			if (bowItem == null)
				return;

			long timeSinceUpdate = Minecraft.getSystemTime() - ClientReflectiveAccessor.lastSystemTime(mc);

			if (timeSinceUpdate <= 200) {
				int wheelChange = event.getDwheel();

				if (wheelChange != 0) {
					event.setCanceled(true);
					rotateArrows(bowItem, player, wheelChange);
					NetworkHandler.INSTANCE.sendToServer(new MessageRotateArrows(wheelChange));
				}
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
}
