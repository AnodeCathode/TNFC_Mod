package com.lumintorious.ambiental.api;

import com.lumintorious.ambiental.modifiers.TileEntityModifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

//Tile entities you create should implement this if necessary
public interface ITileEntityTemperatureOwner {
	public TileEntityModifier getModifier(EntityPlayer player);
}
