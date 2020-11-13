package com.lumintorious.ambiental.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import com.lumintorious.ambiental.modifiers.BlockModifier;

// Blocks you create should implement this if necessary
public interface IBlockTemperatureOwner
{
    public BlockModifier getModifier(IBlockState state, BlockPos blockPos, EntityPlayer player);
}
