package com.lumintorious.ambiental.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface ITemperatureSystem extends INBTSerializable<NBTTagCompound>{
	public float getTemperature();
	public void setTemperature(float newTemp);
	public EntityPlayer getPlayer();
	public float getChange();
	public float getChangeSpeed();
}
