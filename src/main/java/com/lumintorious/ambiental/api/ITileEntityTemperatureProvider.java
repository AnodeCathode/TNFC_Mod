package com.lumintorious.ambiental.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.lumintorious.ambiental.modifiers.TileEntityModifier;

//Add an example of this into TemperatureRegistry for tile entities you didn't create personally
@FunctionalInterface
public interface ITileEntityTemperatureProvider extends ITemperatureProvider
{
    TileEntityModifier getModifier(TileEntity tile, EntityPlayer player);
}
