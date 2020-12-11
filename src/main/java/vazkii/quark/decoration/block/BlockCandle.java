package vazkii.quark.decoration.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.decoration.feature.TallowAndCandles;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

public class BlockCandle extends BlockMetaVariants<BlockCandle.Variants> implements IQuarkBlock {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(6F / 16F, 0F, 6F / 16F, 10F / 16F, 0.5F, 10F / 16F);
	
	public BlockCandle() {
		super("candle", Material.CLAY, Variants.class);
		setHardness(0.2F);
		setLightLevel(0.9375F);
		setLightOpacity(0);
		setSoundType(SoundType.CLOTH);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!worldIn.isRemote && TallowAndCandles.candlesFall)
			checkFallable(worldIn, pos);
	}
	
	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos) {
		return TallowAndCandles.enchantPower;
	}
	
	// Copypasta from BlockFalling
	private void checkFallable(World worldIn, BlockPos pos) {
		if((worldIn.isAirBlock(pos.down()) || BlockFalling.canFallThrough(worldIn.getBlockState(pos.down()))) && pos.getY() >= 0) {

			if(!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
				if(!worldIn.isRemote) {
					EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, worldIn.getBlockState(pos));
					worldIn.spawnEntity(entityfallingblock);
				}
			} else {
				IBlockState state = worldIn.getBlockState(pos);
				worldIn.setBlockToAir(pos);
				BlockPos blockpos = pos.down();

				while ((worldIn.isAirBlock(blockpos) || BlockFalling.canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.getY() > 0)
					blockpos = blockpos.down();

				if(blockpos.getY() > 0)
					worldIn.setBlockState(blockpos.up(), state);
			}
		}
	}
	

	@Override
	public int tickRate(World worldIn) {
		return 2;
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
		return AABB;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullBlock(IBlockState state) {
		return false;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos blockPos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		double d0 = pos.getX() + 0.5D;
		double d1 = pos.getY() + 0.7D;
		double d2 = pos.getZ() + 0.5D;

		worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}
	
	
	public enum Variants implements IStringSerializable {
		CANDLE_WHITE(EnumDyeColor.WHITE),
		CANDLE_ORANGE(EnumDyeColor.ORANGE),
		CANDLE_MAGENTA(EnumDyeColor.MAGENTA),
		CANDLE_LIGHT_BLUE(EnumDyeColor.LIGHT_BLUE),
		CANDLE_YELLOW(EnumDyeColor.YELLOW),
		CANDLE_LIME(EnumDyeColor.LIME),
		CANDLE_PINK(EnumDyeColor.PINK),
		CANDLE_GRAY(EnumDyeColor.GRAY),
		CANDLE_SILVER(EnumDyeColor.SILVER),
		CANDLE_CYAN(EnumDyeColor.CYAN),
		CANDLE_PURPLE(EnumDyeColor.PURPLE),
		CANDLE_BLUE(EnumDyeColor.BLUE),
		CANDLE_BROWN(EnumDyeColor.BROWN),
		CANDLE_GREEN(EnumDyeColor.GREEN),
		CANDLE_RED(EnumDyeColor.RED),
		CANDLE_BLACK(EnumDyeColor.BLACK);

		public final EnumDyeColor color;

		Variants(EnumDyeColor color) {

			this.color = color;
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
	
}
