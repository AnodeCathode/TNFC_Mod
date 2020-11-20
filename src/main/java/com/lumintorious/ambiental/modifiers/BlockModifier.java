package com.lumintorious.ambiental.modifiers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import com.lumintorious.ambiental.api.*;
import net.dries007.tfc.objects.blocks.stone.BlockRockRaw;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;

public class BlockModifier extends BaseModifier{

	public boolean affectedByDistance = false;
	
	public BlockModifier(String name) {
		super(name);
	}
	
	public BlockModifier(String name, float change, float potency) {
		super(name, change, potency);
	}
	
	public BlockModifier(String name, float change, float potency, boolean affectedByDistance) {
		super(name, change, potency);
		this.affectedByDistance = affectedByDistance;
	}
	
	public static void computeModifiers(EntityPlayer player, ModifierStorage modifiers) {
		BlockPos p = player.getPosition();
		BlockPos pos1 = new BlockPos(p.getX() - 9, p.getY() - 3, p.getZ() - 9);
		BlockPos pos2 = new BlockPos(p.getX() + 9, p.getY() + 5, p.getZ() + 9);
		Iterable<BlockPos.MutableBlockPos> allPositions = BlockPos.getAllInBoxMutable(pos1, pos2);
		IBlockState skipState = Blocks.AIR.getDefaultState();
		for(BlockPos.MutableBlockPos pos : allPositions) {
			IBlockState state = player.world.getBlockState(pos);
			if(state == skipState) {
				continue;
			}
			if(state.getBlock() instanceof BlockRockVariant || state.getBlock() instanceof BlockRockRaw) {
				continue;
			}
			Block block = state.getBlock();
			double distance = Math.sqrt(player.getPosition().distanceSq(pos));
			float distanceMultiplier = (float)distance / 9f;
			distanceMultiplier = Math.min(1f, Math.max(0f, distanceMultiplier));
			distanceMultiplier = 1f - distanceMultiplier;
			boolean isInside = EnvironmentalModifier.getSkylight(player) < 14 && EnvironmentalModifier.getBlockLight(player) > 3;
			if(isInside) {
				distanceMultiplier *= 1.3f;
			}
			if(block instanceof IBlockTemperatureOwner) {
				IBlockTemperatureOwner owner = (IBlockTemperatureOwner)block;
				BlockModifier modifier = owner.getModifier(state, pos, player);
				if(modifier != null) {
					if(modifier.affectedByDistance){
						modifier.setChange(modifier.getChange() * distanceMultiplier);
						modifier.setPotency(modifier.getPotency() * distanceMultiplier);
					}
					modifiers.add(modifier);
				}
			}
			for(IBlockTemperatureProvider provider : TemperatureRegistry.BLOCKS) {
				BlockModifier modifier = provider.getModifier(state, pos, player);
				if(modifier != null) {
					if(modifier.affectedByDistance){
						modifier.setChange(modifier.getChange() * distanceMultiplier);
						modifier.setPotency(modifier.getPotency() * distanceMultiplier);
					}
					modifiers.add(modifier);
				}
			}
			if(block.hasTileEntity(state)) {
				TileEntity tile = player.world.getTileEntity(pos);
				if(tile instanceof ITileEntityTemperatureOwner) {
					ITileEntityTemperatureOwner owner = (ITileEntityTemperatureOwner)tile;
					BlockModifier modifier = owner.getModifier(player);
					if(modifier != null) {
						if(modifier.affectedByDistance){
							modifier.setChange(modifier.getChange() * distanceMultiplier);
							modifier.setPotency(modifier.getPotency() * distanceMultiplier);
						}
						modifiers.add(modifier);
					}
				}else
				for(ITileEntityTemperatureProvider provider : TemperatureRegistry.TILE_ENTITIES) {
					BlockModifier modifier = provider.getModifier(tile, player);
					if(modifier != null) {
						modifier.setChange(modifier.getChange() * distanceMultiplier);
						modifier.setPotency(modifier.getPotency() * distanceMultiplier);
						modifiers.add(modifier);
					}
				}
			}
		}
	}
}
