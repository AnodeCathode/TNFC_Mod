/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jun 03, 2019, 00:43 AM (EST)]
 */
package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.client.feature.ItemsFlashBeforeExpiring;

public class MessageItemUpdate extends NetworkMessage<MessageItemUpdate> {

	public int id;
	public int age;
	public int lifespan;

	public MessageItemUpdate() {
		// NO-OP
	}

	public MessageItemUpdate(int id, int age, int lifespan) {
		this.id = id;
		this.age = age;
		this.lifespan = lifespan;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage handleMessage(MessageContext context) {

		ClientTicker.addAction(() -> {
			World world = Minecraft.getMinecraft().world;
			if (world != null) {
				Entity entity = world.getEntityByID(id);
				if (entity instanceof EntityItem) {
					((EntityItem) entity).lifespan = lifespan;
					ItemsFlashBeforeExpiring.setItemAge((EntityItem) entity, age);
				}
			}
		});

		return null;
	}
}
