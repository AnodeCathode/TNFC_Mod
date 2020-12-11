/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [27/03/2016, 06:02:47 (GMT)]
 */
package vazkii.quark.misc.feature;

import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class NoteBlocksMobSounds extends Feature {

	public static final EnumFacing[] SKULL_SEARCH_FACINGS = new EnumFacing[] {
			EnumFacing.NORTH,
			EnumFacing.SOUTH,
			EnumFacing.EAST,
			EnumFacing.WEST
	};

	@SubscribeEvent
	public void noteBlockPlayed(NoteBlockEvent.Play event) {
		BlockPos pos = event.getPos();
		if(event.getWorld().getBlockState(pos).getBlock() != Blocks.NOTEBLOCK)
			return;

		int type = getSkullType(event.getWorld(), pos);
		if(type != -1 && type != 3) {
			event.setCanceled(true);

			SoundEvent sound = null;
			switch(type) {
			case 0:
				sound = SoundEvents.ENTITY_SKELETON_AMBIENT;
				break;
			case 1:
				sound = SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
				break;
			case 2:
				sound = SoundEvents.ENTITY_ZOMBIE_AMBIENT;
				break;
			case 4:
				sound = SoundEvents.ENTITY_CREEPER_PRIMED;
				break;
			case 5:
				sound = SoundEvents.ENTITY_ENDERDRAGON_AMBIENT;
				break;
			}

			if(sound != null) {
				float pitch = (float) Math.pow(2.0, (event.getVanillaNoteId() - 12) / 12.0);
				event.getWorld().playSound(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, sound, SoundCategory.BLOCKS, 1F, pitch);
			}
		}
	}

	public static int getSkullType(World world, BlockPos pos) {
		TileEntity tile = null;
		boolean can = false;
		for(EnumFacing face : SKULL_SEARCH_FACINGS) {
			BlockPos offsetPos = pos.offset(face);
			tile = world.getTileEntity(offsetPos);
			if(tile instanceof TileEntitySkull) {
				IBlockState state = world.getBlockState(offsetPos);
				if(state.getValue(BlockSkull.FACING) == face) {
					can = true;
					break;
				}
			}
		}

		if(can) return ((TileEntitySkull) tile).getSkullType();

		return -1;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Note Block Mob Sounds";
	}

}
