package com.lumintorious.ambiental.api;

import net.minecraft.entity.player.EntityPlayer;

import com.lumintorious.ambiental.modifiers.TileEntityModifier;

//Tile entities you create should implement this if necessary
public interface ITileEntityTemperatureOwner {
	public TileEntityModifier getModifier(EntityPlayer player);
}
