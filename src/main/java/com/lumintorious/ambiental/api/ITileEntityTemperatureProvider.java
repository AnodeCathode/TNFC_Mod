package com.lumintorious.ambiental.api;

import com.lumintorious.ambiental.modifiers.BaseModifier;
import com.lumintorious.ambiental.modifiers.ModifierStorage;
import com.lumintorious.ambiental.modifiers.TileEntityModifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

//Add an example of this into TemperatureRegistry for tile entities you didn't create personally
@FunctionalInterface
public interface ITileEntityTemperatureProvider  extends ITemperatureProvider{
	TileEntityModifier getModifier(TileEntity tile, EntityPlayer player);
}
