/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 21:35:57 (GMT)]
 */
package vazkii.quark.decoration.block;

import net.minecraft.block.BlockColored;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.experimental.features.ColoredLights;
import vazkii.quark.experimental.lighting.IColoredLightSource;

public class BlockLitLamp extends BlockMod implements IQuarkBlock, IColoredLightSource {

	public BlockLitLamp() {
		super("lit_lamp", Material.GLASS);
		setHardness(0.3F);
		setLightLevel(1F);
		setSoundType(SoundType.GLASS);
		setCreativeTab(CreativeTabs.REDSTONE);
	}
	
	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
		ColoredLights.addLightSource(world, pos, state);
		return super.getLightOpacity(state, world, pos);
	}

	@Override
	public float[] getColoredLight(IBlockAccess world, BlockPos pos) {
		int index = 0;
		
		BlockPos down = pos.down();
		IBlockState state = world.getBlockState(down);
		if(state.getBlock() == Blocks.CONCRETE)
			index = state.getValue(BlockColored.COLOR).ordinal();
		
		return VANILLA_SPECTRUM_COLORS[index];
	}

}
