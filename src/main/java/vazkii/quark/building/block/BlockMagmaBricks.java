package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

import javax.annotation.Nonnull;

public class BlockMagmaBricks extends BlockMod implements IQuarkBlock {

	public BlockMagmaBricks() {
		super("magma_bricks", Material.ROCK);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setLightLevel(0.2F);
	}
	
	@Override
	public boolean isFireSource(@Nonnull World world, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, @Nonnull BlockPos pos) {
		return 0xf000f0;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
		return entityIn.isImmuneToFire();
	}
	
}
