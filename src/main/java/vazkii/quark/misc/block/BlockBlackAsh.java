package vazkii.quark.misc.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkDust;
import vazkii.quark.misc.item.ItemBlackAshBlock;

import java.util.Random;

public class BlockBlackAsh extends BlockQuarkDust {

	public BlockBlackAsh() {
		super("black_ash");
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public ItemBlock createItemBlock(ResourceLocation res) {
		return new ItemBlackAshBlock(this, res);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(rand.nextFloat() < 0.1) {
			float x = pos.getX() + 0.3F + rand.nextFloat() * 0.4F;
			float y = pos.getY() + 0.2F;
			float z = pos.getZ() + 0.3F + rand.nextFloat() * 0.4F;
			worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 0, 0, 0);
		}
	}

	@Override
	public int getColor(IBlockAccess world, IBlockState state, BlockPos pos, int tint) {
		return 0x222222;
	}

}
