/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/04/2016, 17:44:30 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;

public class MessageSpamlessChat extends NetworkMessage<MessageSpamlessChat> {

	public ITextComponent message;
	public int id;

	public MessageSpamlessChat() { }

	public MessageSpamlessChat(ITextComponent message, int id) {
		this.message = message;
		this.id = id;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage handleMessage(MessageContext context) {
		ClientTicker.addAction(() -> Minecraft.getMinecraft().ingameGUI.getChatGUI()
				.printChatMessageWithOptionalDeletion(message, id));
		return null;
	}

	public static void sendToPlayer(EntityPlayer player, int id, ITextComponent component) {
		if (player instanceof EntityPlayerMP)
			NetworkHandler.INSTANCE.sendTo(new MessageSpamlessChat(component, id), (EntityPlayerMP) player);
	}

}
