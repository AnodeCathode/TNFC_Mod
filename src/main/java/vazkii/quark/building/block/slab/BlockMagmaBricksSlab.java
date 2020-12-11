package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkSlab;

import javax.annotation.Nonnull;

public class BlockMagmaBricksSlab extends BlockQuarkSlab {

	public BlockMagmaBricksSlab(boolean doubleSlab) {
		super("magma_bricks_slab", Material.ROCK, doubleSlab);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setLightLevel(0.2F);
	}
	
	@Override
	public boolean isFireSource(@Nonnull World world, BlockPos pos, EnumFacing side) {
		return isSideSolid(world.getBlockState(pos), world, pos, side);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, @Nonnull BlockPos pos) {
		return 0xf000f0;
	}

}
