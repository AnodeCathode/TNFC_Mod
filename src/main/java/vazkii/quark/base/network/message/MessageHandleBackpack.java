package vazkii.quark.base.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibGuiIDs;
import vazkii.quark.oddities.inventory.ContainerBackpack;

public class MessageHandleBackpack extends NetworkMessage<MessageHandleBackpack> {

	public boolean open;
	
	public MessageHandleBackpack() { }
	
	public MessageHandleBackpack(boolean open) { 
		this.open = open;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		EntityPlayerMP player = context.getServerHandler().player;
		player.server.addScheduledTask(() -> {
			if(open)
				player.openGui(Quark.instance, LibGuiIDs.BACKPACK, player.world, 0, 0, 0);
			else {
				ContainerBackpack.saveCraftingInventory(player);
				player.openContainer = player.inventoryContainer;
			}
		});
		
		return null;
	}
	
}
