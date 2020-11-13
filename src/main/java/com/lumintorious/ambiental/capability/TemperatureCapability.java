package com.lumintorious.ambiental.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class TemperatureCapability<C> implements ICapabilitySerializable<NBTTagCompound>
{
    @CapabilityInject(ITemperatureSystem.class)
    public static final Capability<ITemperatureSystem> CAPABILITY = null;


    /**
     * The capability this is for
     */
    private final EntityPlayer player;

    public TemperatureCapability(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability != null && capability == CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == CAPABILITY ? (T) (TemperatureSystem.getTemperatureFor(player)) : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();
        TemperatureSystem temp = TemperatureSystem.getTemperatureFor(player);
        return temp.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (!(nbt instanceof NBTTagCompound)) throw new IllegalArgumentException("Temperature must be read from an NBTTagCompound!");

        TemperatureSystem temp = TemperatureSystem.getTemperatureFor(player);
        temp.deserializeNBT(nbt);
    }
}
