/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 22:10:42 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.Quark;

public class MessageDoEmote extends NetworkMessage<MessageDoEmote> {

	public String emoteName;
	public String playerName;
	public int playerTier;

	public MessageDoEmote() { }

	public MessageDoEmote(String emoteName, String playerName, int tier) {
		this.emoteName = emoteName;
		this.playerName = playerName;
		this.playerTier = tier;
	}

	@Override
	public IMessage handleMessage(MessageContext context) {
		ClientTicker.addAction(() -> Quark.proxy.doEmote(playerName, emoteName, playerTier));
		
		return null;
	}

}
