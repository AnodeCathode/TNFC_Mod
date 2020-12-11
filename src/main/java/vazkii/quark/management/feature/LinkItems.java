/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [06/06/2016, 01:40:29 (GMT)]
 */
package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageLinkItem;

public class LinkItems extends Feature {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void keyboardEvent(GuiScreenEvent.KeyboardInputEvent.Post event) {
		if(GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindChat) && event.getGui() instanceof GuiContainer && GuiScreen.isShiftKeyDown()) {
			GuiContainer gui = (GuiContainer) event.getGui();

			Slot slot = gui.getSlotUnderMouse();
			if(slot != null && slot.inventory != null && !"tmp".equals(slot.inventory.getName())) { // "tmp" checks for a creative inventory
				ItemStack stack = slot.getStack();

				if(!stack.isEmpty() && !MinecraftForge.EVENT_BUS.post(new ClientChatEvent(stack.getTextComponent().getUnformattedText())))
					NetworkHandler.INSTANCE.sendToServer(new MessageLinkItem(stack));
			}
		}
	}

	public static void linkItem(EntityPlayer player, ItemStack item) {
		if(!ModuleLoader.isFeatureEnabled(LinkItems.class))
			return;

		if(!item.isEmpty() && player instanceof EntityPlayerMP) {
			ITextComponent comp = item.getTextComponent();
			ITextComponent fullComp = new TextComponentTranslation("chat.type.text", player.getDisplayName(), comp);

			PlayerList players = ((EntityPlayerMP) player).server.getPlayerList();

			ServerChatEvent event = new ServerChatEvent((EntityPlayerMP) player, comp.getUnformattedText(), fullComp);
			if (!MinecraftForge.EVENT_BUS.post(event)) {
				players.sendMessage(fullComp, false);

				NetHandlerPlayServer handler = ((EntityPlayerMP) player).connection;
				int threshold = ObfuscationReflectionHelper.getPrivateValue(NetHandlerPlayServer.class, handler, LibObfuscation.CHAT_SPAM_THRESHOLD_COUNT);
				threshold += 20;

				if (threshold > 200 && !players.canSendCommands(player.getGameProfile()))
					handler.onDisconnect(new TextComponentTranslation("disconnect.spam"));

				ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, handler, threshold, LibObfuscation.CHAT_SPAM_THRESHOLD_COUNT);
			}
		}

	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Link Items To Chat";
	}

}
