package tnfcmod.handlers.packets;


import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static tnfcmod.tnfcmod.instance;

public class CPacketOpenCartGui implements IMessage {
    private int invId;
    private int cartId;

    public CPacketOpenCartGui() {
    }

    public CPacketOpenCartGui(int invIdIn, int cartIdIn) {
        this.invId = invIdIn;
        this.cartId = cartIdIn;
    }

    public void fromBytes(ByteBuf buf) {
        this.invId = buf.readInt();
        this.cartId = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.invId);
        buf.writeInt(this.cartId);
    }

    public static class OpenCartGuiPacketHandler implements IMessageHandler<CPacketOpenCartGui, IMessage> {
        public OpenCartGuiPacketHandler() {
        }

        public IMessage onMessage(CPacketOpenCartGui message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.openGui(instance, message.invId, player.world, message.cartId, 0, 0);
            return null;
        }
    }
}
