package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.tweaks.feature.ImprovedSleeping;

public class MessageUpdateAfk extends NetworkMessage<MessageUpdateAfk> {

	public boolean afk;
	
	public MessageUpdateAfk() { }

	public MessageUpdateAfk(boolean afk) { 
		this.afk = afk;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		ImprovedSleeping.updateAfk(context.getServerHandler().player, afk);
		return null;
	}

}
