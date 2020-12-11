package vazkii.quark.world.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockElderSeaLantern extends BlockMod implements IQuarkBlock {

	public BlockElderSeaLantern() {
		super("elder_sea_lantern", Material.GLASS);
		setHardness(0.3F);
		setSoundType(SoundType.GLASS);
		setLightLevel(1.0F);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 2 + random.nextInt(2);
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, @Nonnull Random random) {
		return MathHelper.clamp(quantityDropped(random) + random.nextInt(fortune + 1), 1, 5);
	}

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.PRISMARINE_CRYSTALS;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return MapColor.QUARTZ;
	}

	@Override
	@SuppressWarnings("deprecation")
	protected boolean canSilkHarvest() {
		return true;
	}

}
