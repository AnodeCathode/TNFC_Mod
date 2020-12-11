/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [31/03/2016, 02:55:00 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;

public class MessageDismountSeat extends NetworkMessage<MessageDismountSeat> {

	public MessageDismountSeat() { }

	@Override
	public IMessage handleMessage(MessageContext context) {
		context.getServerHandler().player.getServerWorld().addScheduledTask(() ->
				context.getServerHandler().player.dismountRidingEntity());
		return null;
	}

}
