package vazkii.quark.base.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.oddities.inventory.ContainerMatrixEnchanting;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;

public class MessageMatrixEnchanterOperation extends NetworkMessage<MessageMatrixEnchanterOperation> {

	public int operation;
	public int arg0, arg1, arg2;
	
	public MessageMatrixEnchanterOperation() { }
	
	public MessageMatrixEnchanterOperation(int operation, int arg0, int arg1, int arg2) {
		this.operation = operation;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		EntityPlayerMP player = context.getServerHandler().player;
		player.server.addScheduledTask(() -> {
			Container container = player.openContainer;
			if(container instanceof ContainerMatrixEnchanting) {
				TileMatrixEnchanter enchanter = ((ContainerMatrixEnchanting) container).enchanter;
				enchanter.onOperation(player, operation, arg0, arg1, arg2);
			}
		});
		
		return null;
	}
	
}
