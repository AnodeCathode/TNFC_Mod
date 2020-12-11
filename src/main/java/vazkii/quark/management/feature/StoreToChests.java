/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [28/03/2016, 15:57:43 (GMT)]
 */
package vazkii.quark.management.feature;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageDropoff;
import vazkii.quark.management.client.gui.GuiButtonChest;
import vazkii.quark.management.client.gui.GuiButtonChest.Action;
import vazkii.quark.management.gamerule.DropoffGamerule;

import java.util.ArrayList;
import java.util.List;

public class StoreToChests extends Feature {

	public static final String GAME_RULE = "quark_allowDropoff";

	public static boolean clientDisabled;
	public static boolean invert;

	public static int xPos, yPos;
	public static int xPosC, yPosC;

	public static List<String> classnames;

	@Override
	public void setupConfig() {
		invert = loadPropBool("Invert button", "If true the default will be to merge your items into nearby chests, otherwise hold shift for this functionality.", false);
		xPos = loadPropInt("Position X", "", 0);
		yPos = loadPropInt("Position Y ", "", 30);
		xPosC = loadPropInt("Position X (Creative)", "", 28);
		yPosC = loadPropInt("Position Y (Creative)", "", -20);

		String[] classnamesArr = loadPropStringList("Forced GUIs", "GUIs in which the drop off button should be forced to show up. Use the \"Debug Classnames\" option in chest buttons to find the names.", new String[0]);
		classnames = Lists.newArrayList(classnamesArr);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new DropoffGamerule());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		ModKeybinds.initDropoffKey();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if(Minecraft.getMinecraft().world == null)
			clientDisabled = false;

		if(event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative
				|| classnames.contains(event.getGui().getClass().getName())) {
			if(clientDisabled)
				return;

			GuiContainer guiInv = (GuiContainer) event.getGui();
			GuiContainerCreative creativeInv = null;
			if(guiInv instanceof GuiContainerCreative)
				creativeInv = (GuiContainerCreative) guiInv;

			if(creativeInv == null && Minecraft.getMinecraft().player.capabilities.isCreativeMode)
				return;

			if (ChestButtons.chestButtons == null)
				ChestButtons.chestButtons = new ArrayList<>();
			ChestButtons.chestButtons.clear();

			Container container = guiInv.inventorySlots;
			for(Slot s : container.inventorySlots)
				if(creativeInv != null || s instanceof SlotCrafting) {
					if(creativeInv == null)
						ChestButtons.addButtonAndKeybind(event, Action.DROPOFF, guiInv, 13211, s.xPos + xPos, s.yPos + yPos, ModKeybinds.dropoffKey);
					else {
						if(s.getSlotIndex() != 15)
							continue;
						
						ChestButtons.addButtonAndKeybind(event, Action.DROPOFF, guiInv, 13211, s.xPos + xPosC, s.yPos + yPosC, ModKeybinds.dropoffKey,
								(gui) -> ((GuiContainerCreative) gui).getSelectedTabIndex() == CreativeTabs.INVENTORY.getIndex());
					}

					break;
				}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void performAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if(event.getButton() instanceof GuiButtonChest && ((GuiButtonChest) event.getButton()).action == Action.DROPOFF) {
			boolean smart = GuiScreen.isShiftKeyDown() != StoreToChests.invert;
			NetworkHandler.INSTANCE.sendToServer(new MessageDropoff(smart, false));
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void update(ClientTickEvent event) {
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if(gui instanceof GuiInventory) {
			GuiInventory inv = (GuiInventory) gui;
			if (ChestButtons.chestButtons == null)
				ChestButtons.chestButtons = new ArrayList<>();
			for(GuiButtonChest b : ChestButtons.chestButtons) {
				b.x = inv.getGuiLeft() + b.shiftX;
				b.y = inv.getGuiTop() + b.shiftY;
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
