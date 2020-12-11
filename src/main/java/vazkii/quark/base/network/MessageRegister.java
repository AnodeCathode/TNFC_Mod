/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [28/08/2016, 00:37:40 (GMT)]
 */
package vazkii.quark.base.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.network.message.*;
import vazkii.quark.misc.feature.LockDirectionHotkey.LockProfile;

import java.io.IOException;
import java.util.UUID;

public class MessageRegister {

	@SuppressWarnings("unchecked")
	public static void init() {
		NetworkHandler.register(MessageDoEmote.class, Side.CLIENT);
		NetworkHandler.register(MessageDropoff.class, Side.SERVER);
		NetworkHandler.register(MessageSwapItems.class, Side.SERVER);
		NetworkHandler.register(MessageRestock.class, Side.SERVER);
		NetworkHandler.register(MessageFavoriteItem.class, Side.SERVER);
		NetworkHandler.register(MessageDisableDropoffClient.class, Side.CLIENT);	
		NetworkHandler.register(MessageLinkItem.class, Side.SERVER);
		NetworkHandler.register(MessageChangeConfig.class, Side.CLIENT);
		NetworkHandler.register(MessageDeleteItem.class, Side.SERVER);
		NetworkHandler.register(MessageSortInventory.class, Side.SERVER);
		NetworkHandler.register(MessageTuneNoteBlock.class, Side.SERVER);
		NetworkHandler.register(MessageSetLockProfile.class, Side.SERVER);
		NetworkHandler.register(MessageChangeHotbar.class, Side.SERVER);
		NetworkHandler.register(MessageUpdateAfk.class, Side.SERVER);
		NetworkHandler.register(MessageRequestPassengerChest.class, Side.SERVER);
		NetworkHandler.register(MessageHandleBackpack.class, Side.SERVER);
		NetworkHandler.register(MessageRequestEmote.class, Side.SERVER);
		NetworkHandler.register(MessageMatrixEnchanterOperation.class, Side.SERVER);
		NetworkHandler.register(MessageSyncBoatBanner.class, Side.CLIENT);
		NetworkHandler.register(MessageItemUpdate.class, Side.CLIENT);
		NetworkHandler.register(MessageSpamlessChat.class, Side.CLIENT);
		NetworkHandler.register(MessageRotateArrows.class, Side.SERVER);
		NetworkHandler.register(MessageDismountSeat.class, Side.SERVER);
		NetworkHandler.register(MessageSyncColors.class, Side.CLIENT);
		NetworkHandler.register(MessageSyncChain.class, Side.CLIENT);

		NetworkMessage.mapHandler(LockProfile.class, LockProfile::readProfile, LockProfile::writeProfile);
		NetworkMessage.mapHandler(ITextComponent.class, MessageRegister::readComponent, MessageRegister::writeComponent);
		NetworkMessage.mapHandler(UUID.class, MessageRegister::readUUID, MessageRegister::writeUUID);
	}

	private static ITextComponent readComponent(ByteBuf buf) {
		try {
			return new PacketBuffer(buf).readTextComponent();
		} catch (IOException e) {
			return new TextComponentString("");
		}
	}

	private static void writeComponent(ITextComponent comp, ByteBuf buf) {
		new PacketBuffer(buf).writeTextComponent(comp);
	}

	private static UUID readUUID(ByteBuf buf) {
		if (buf.readBoolean())
			return null;
		return new PacketBuffer(buf).readUniqueId();
	}

	private static void writeUUID(UUID uuid, ByteBuf buf) {
		if (uuid == null)
			buf.writeBoolean(true);
		else {
			buf.writeBoolean(false);
			new PacketBuffer(buf).writeUniqueId(uuid);
		}
	}
	
}
