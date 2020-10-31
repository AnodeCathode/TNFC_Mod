package mods.immibis.core.net;

import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.porting.SidedProxy;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractContainerSyncPacket implements IPacket {
	@Override
	public void onReceived(EntityPlayer player) {
		if(player == null)
			player = SidedProxy.instance.getThePlayer();
		if(player.openContainer instanceof ISyncedContainer)
			((ISyncedContainer)player.openContainer).onReceivePacket(this);
	}
}
