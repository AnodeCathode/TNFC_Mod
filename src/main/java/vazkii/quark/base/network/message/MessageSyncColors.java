/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 12, 2019, 16:37 AM (EST)]
 */
package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.world.effects.PotionColorizer;

public class MessageSyncColors extends NetworkMessage<MessageSyncColors> {

	public int entityID;
	public double r, g, b;

	public MessageSyncColors() {
		// NO-OP
	}

	public MessageSyncColors(int entity, double r, double g, double b) {
		this.entityID = entity;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage handleMessage(MessageContext context) {
		ClientTicker.addAction(() -> {
			World world = Minecraft.getMinecraft().world;
			if (world != null) {
				Entity byId = world.getEntityByID(entityID);
				if (byId instanceof EntityLivingBase)
					PotionColorizer.receiveColors((EntityLivingBase) byId, r, g, b);
			}
		});

		return null;
	}
}
