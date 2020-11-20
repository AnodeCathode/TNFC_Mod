package com.lumintorious.ambiental.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.lumintorious.ambiental.modifiers.ItemModifier;

//Items you create should implement this if necessary
public interface IItemTemperatureOwner {
	public ItemModifier getModifier(ItemStack stack, EntityPlayer player);
}
