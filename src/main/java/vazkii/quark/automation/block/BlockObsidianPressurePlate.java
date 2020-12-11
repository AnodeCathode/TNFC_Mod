/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 21:06:08 (GMT)]
 */
package vazkii.quark.automation.block;

import java.util.List;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import vazkii.quark.base.block.BlockQuarkPressurePlate;

public class BlockObsidianPressurePlate extends BlockQuarkPressurePlate {

	public BlockObsidianPressurePlate() {
		super("obsidian_pressure_plate", Material.ROCK, Sensitivity.MOBS);
		setHardness(50.0F);
		setResistance(2000.0F);
		setSoundType(SoundType.STONE);
	}
	
	@Override
	protected List<Entity> getValidEntities(World world, AxisAlignedBB aabb) {
		return world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
	}

}
