package com.lumintorious.ambiental.api;

import net.minecraft.entity.player.EntityPlayer;

import com.lumintorious.ambiental.modifiers.EnvironmentalModifier;

//Add an example of this into TemperatureRegistry for general modifiers
@FunctionalInterface
public interface IEnvironmentalTemperatureProvider extends ITemperatureProvider
{
    public EnvironmentalModifier getModifier(EntityPlayer player);
}
