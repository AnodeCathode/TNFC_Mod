/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/04/2016, 18:01:17 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.management.feature.FavoriteItems;

public class MessageFavoriteItem extends NetworkMessage<MessageFavoriteItem> {

	public int index;

	public MessageFavoriteItem() { }

	public MessageFavoriteItem(int index) {
		this.index = index;
	}

	@Override
	public IMessage handleMessage(MessageContext context) {
		FavoriteItems.favoriteItem(context.getServerHandler().player, index);
		return null;
	}

}
