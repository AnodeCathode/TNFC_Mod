package vazkii.quark.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.quark.automation.entity.EntityGravisand;
import vazkii.quark.base.block.IQuarkBlock;

import java.util.Random;

public class BlockGravisand extends BlockMod implements IQuarkBlock {

	public BlockGravisand() {
		super("gravisand", Material.SAND);
		setHardness(0.5F);
		setSoundType(SoundType.SAND);
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@Override
	public int tickRate(World worldIn) {
		return 2;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		checkRedstone(worldIn, pos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		checkRedstone(worldIn, pos);
	}

	private void checkRedstone(World worldIn, BlockPos pos) {
        boolean powered = worldIn.isBlockPowered(pos);

        if(powered)
        	worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!worldIn.isRemote) {
			if(checkFallable(worldIn, pos))
				for(EnumFacing face : EnumFacing.VALUES) {
					BlockPos offPos = pos.offset(face);
					IBlockState offState = worldIn.getBlockState(offPos);
					
					if(offState.getBlock() == this)
			        	worldIn.scheduleUpdate(offPos, this, tickRate(worldIn));
				}
		}
	}

	private boolean checkFallable(World worldIn, BlockPos pos) {
		if(!worldIn.isRemote) {
			if(tryFall(worldIn, pos, EnumFacing.DOWN))
				return true;
			else return tryFall(worldIn, pos, EnumFacing.UP);
		}
		
		return false;
	}
	
	private boolean tryFall(World worldIn, BlockPos pos, EnumFacing facing) {
		BlockPos target = pos.offset(facing);
		if((worldIn.isAirBlock(target) || canFallThrough(worldIn.getBlockState(target))) && pos.getY() >= 0) {
			EntityGravisand entity = new EntityGravisand(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, worldIn.getBlockState(pos), facing.getYOffset());
			worldIn.spawnEntity(entity);
			return true;
		}
		
		return false;
	}
	
    public static boolean canFallThrough(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.WEB || block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }

}
