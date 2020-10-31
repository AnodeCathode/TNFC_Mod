package mods.immibis.core.net;

import mods.immibis.core.api.net.IPacket;

/**
 * Implement this on your container class to receive GUI packets.
 * (send them from OneTwoFiveNetworking.sendContainerUpdate)
 */
public interface ISyncedContainer {
	/** You will want to check the class of the packet before using it */
	public void onReceivePacket(IPacket packet);
}
