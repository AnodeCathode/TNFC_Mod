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
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.vanity.feature.BoatSails;

public class MessageSyncBoatBanner extends NetworkMessage<MessageSyncBoatBanner> {

	public int boat;
	public ItemStack banner;

	public MessageSyncBoatBanner() {
		// NO-OP
	}

	public MessageSyncBoatBanner(int boat, ItemStack banner) {
		this.boat = boat;
		this.banner = banner;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage handleMessage(MessageContext context) {
		ClientTicker.addAction(() -> {
			World world = Minecraft.getMinecraft().world;
			if (world != null) {
				Entity boatEntity = world.getEntityByID(boat);
				if (boatEntity == null)
					BoatSails.queueBannerUpdate(boat, banner);
				else
					BoatSails.setBanner(boatEntity, banner, false);
			}
		});
		return null;
	}
}
