package com.lumintorious.ambiental.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;

public class TemperaturePacket implements IMessage{
	
	public NBTTagCompound tag = new NBTTagCompound();
	
	public TemperaturePacket()
    {
    }

	public TemperaturePacket(NBTTagCompound tag)
    {
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, tag);
    }
    
    public static final class Handler implements IMessageHandler<TemperaturePacket, IMessage>
    {
        @Override
        public IMessage onMessage(TemperaturePacket message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    ITemperatureCapability sys = player.getCapability(TemperatureCapability.CAPABILITY, null);
                    if (sys != null)
                    {
                    	sys.deserializeNBT(message.tag);
                    }
                }
            });
            return null;
        }
    }
}
