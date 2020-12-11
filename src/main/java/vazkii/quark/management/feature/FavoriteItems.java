/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/04/2016, 17:54:27 (GMT)]
 */
package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageFavoriteItem;

public class FavoriteItems extends Feature {

	public static final String TAG_FAVORITE_ITEM = "Quark:FavoriteItem";

	public static boolean hovering;
	private static boolean mouseDown = false;

	@SubscribeEvent
	public void itemPickedUp(EntityItemPickupEvent event) {
		ItemStack stack = event.getItem().getItem();
		ItemStack copy = stack.copy();
		ItemNBTHelper.setBoolean(copy, TAG_FAVORITE_ITEM, true);
		
		if(stack.isStackable()) {
			for(ItemStack other : event.getEntityPlayer().inventory.mainInventory) {
				if(ItemStack.areItemsEqual(copy, other) && ItemStack.areItemStackTagsEqual(copy, other)) {
					if(!ItemStack.areItemStackTagsEqual(stack, copy)) {
						event.getItem().setItem(copy);
						event.setCanceled(true);
					}
					return;
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public void mouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
		boolean wasMouseDown = mouseDown;
		mouseDown = Mouse.isButtonDown(1);
		boolean click = mouseDown && !wasMouseDown;

		if(GuiScreen.isAltKeyDown() && click && event.getGui() instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) event.getGui();
			Slot slot = gui.getSlotUnderMouse();
			if(slot != null) {
				IInventory inv = slot.inventory;
				if(inv instanceof InventoryPlayer) {
					int index = slot.getSlotIndex();

					if(Minecraft.getMinecraft().player.capabilities.isCreativeMode && index >= 36)
						index -= 36; // Creative mode messes with the indexes for some reason

					if(index < ((InventoryPlayer) inv).mainInventory.size()) {
						NetworkHandler.INSTANCE.sendToServer(new MessageFavoriteItem(index));
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void drawEvent(GuiScreenEvent.DrawScreenEvent.Post event) {
		if(hovering && event.getGui() instanceof GuiContainer) {
			GuiContainer guiInv = (GuiContainer) event.getGui();
			Container container = guiInv.inventorySlots;

			int guiLeft = guiInv.getGuiLeft();
			int guiTop = guiInv.getGuiTop();

			GlStateManager.color(1F, 1F, 1F);
			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.disableLighting();
			for(Slot s : container.inventorySlots) {
				ItemStack stack = s.getStack();
				if(isItemFavorited(stack)) {
					Minecraft.getMinecraft().renderEngine.bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
					guiInv.drawTexturedModalRect(guiLeft + s.xPos, guiTop + s.yPos, 96, 0, 16, 16);
				}
			}
			GlStateManager.popMatrix();
		}

		hovering = false;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void makeTooltip(ItemTooltipEvent event) {
		if(isItemFavorited(event.getItemStack()))
			event.getToolTip().set(0, "   " + event.getToolTip().get(0));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderTooltip(RenderTooltipEvent.PostText event) {
		if(isItemFavorited(event.getStack())) {
			GlStateManager.pushMatrix();
			GlStateManager.color(1F, 1F, 1F);
			Minecraft mc = Minecraft.getMinecraft();

			int x = event.getX();
			int y = event.getY() - 1;
			
			mc.getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
			Gui.drawModalRectWithCustomSizedTexture(x, y, 166, 0, 9, 9, 256, 256);
			
			GlStateManager.popMatrix();
		}
	}
	
	public static void favoriteItem(EntityPlayer player, int slot) {
		if(!ModuleLoader.isFeatureEnabled(FavoriteItems.class) || slot >= player.inventory.getSizeInventory())
			return;

		ItemStack stack = player.inventory.getStackInSlot(slot);
		if(!stack.isEmpty()) {
			if(isItemFavorited(stack)) {
				NBTTagCompound cmp = stack.getTagCompound();
				if (cmp != null) {
					cmp.removeTag(TAG_FAVORITE_ITEM);
					if (cmp.isEmpty())
						stack.setTagCompound(null);
				}
			} else ItemNBTHelper.setBoolean(stack, TAG_FAVORITE_ITEM, true);
		}
	}

	public static boolean isItemFavorited(ItemStack stack) {
		if(stack == null || stack.isEmpty() || !stack.hasTagCompound() || !ModuleLoader.isFeatureEnabled(FavoriteItems.class))
			return false;

		return ItemNBTHelper.getBoolean(stack, TAG_FAVORITE_ITEM, false);
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Favorite Items";
	}

}
