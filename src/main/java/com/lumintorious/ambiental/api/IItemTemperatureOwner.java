package com.lumintorious.ambiental.api;

import com.lumintorious.ambiental.modifiers.BaseModifier;
import com.lumintorious.ambiental.modifiers.ItemModifier;
import com.lumintorious.ambiental.modifiers.ModifierStorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

//Items you create should implement this if necessary
public interface IItemTemperatureOwner {
	public ItemModifier getModifier(ItemStack stack, EntityPlayer player);
}
