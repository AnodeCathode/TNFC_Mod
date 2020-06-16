package tnfcmod.handlers.packets;



import io.netty.buffer.ByteBuf;
import tnfcmod.objects.entities.AbstractDrawnTFC;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketDrawnUpdate implements IMessage {
    private int pullingId;
    private int cartId;

    public SPacketDrawnUpdate() {
    }

    public SPacketDrawnUpdate(int horseIn, int cartIn) {
        this.pullingId = horseIn;
        this.cartId = cartIn;
    }

    public void fromBytes(ByteBuf buf) {
        this.pullingId = buf.readInt();
        this.cartId = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pullingId);
        buf.writeInt(this.cartId);
    }

    public static class DrawnUpdatePacketHandler implements IMessageHandler<SPacketDrawnUpdate, IMessage> {
        public DrawnUpdatePacketHandler() {
        }

        public IMessage onMessage(SPacketDrawnUpdate message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                AbstractDrawnTFC cart = (AbstractDrawnTFC) Minecraft.getMinecraft().world.getEntityByID(message.cartId);
                if (message.pullingId < 0) {
                    cart.setPulling((Entity)null);
                } else {
                    cart.setPullingId(message.pullingId);
                }
            });
            return null;
        }
    }
}
