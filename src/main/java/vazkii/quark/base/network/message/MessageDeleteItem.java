package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.management.feature.DeleteItems;

public class MessageDeleteItem extends NetworkMessage<MessageDeleteItem> {

	public int slot;
	
	public MessageDeleteItem() { }
	
	public MessageDeleteItem(int slot) {
		this.slot = slot;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		DeleteItems.deleteItem(context.getServerHandler().player, slot);
		return null;
	}
	
	
}
