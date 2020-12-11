/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 14, 2019, 12:53 AM (EST)]
 */
package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.automation.feature.ChainLinkage;
import vazkii.quark.world.base.ChainHandler;

import java.util.UUID;

public class MessageSyncChain extends NetworkMessage<MessageSyncChain> {

	public int vehicle;
	public UUID other;

	public MessageSyncChain() {
		// NO-OP
	}

	public MessageSyncChain(int vehicle, UUID other) {
		this.vehicle = vehicle;
		this.other = other;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage handleMessage(MessageContext context) {
		ClientTicker.addAction(() -> {
			World world = Minecraft.getMinecraft().world;
			if (world != null) {
				Entity boatEntity = world.getEntityByID(vehicle);
				if (boatEntity == null)
					ChainLinkage.queueChainUpdate(vehicle, other);
				else
					ChainHandler.setLink(boatEntity, other, false);
			}
		});
		return null;
	}
}
