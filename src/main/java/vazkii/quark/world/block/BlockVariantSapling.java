package vazkii.quark.world.block;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.event.terraingen.TerrainGen;
import vazkii.arl.item.ItemModBlock;
import vazkii.quark.base.block.BlockQuarkBush;
import vazkii.quark.world.world.tree.WorldGenSakuraTree;
import vazkii.quark.world.world.tree.WorldGenSwampTree;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

public class BlockVariantSapling extends BlockQuarkBush implements IGrowable {

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
	public static final PropertyInteger STAGE = BlockSapling.STAGE;

	protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.1, 0, 0.1, 0.9, 0.8, 0.9);

	private static final String[] VARIANTS = new String[] { "swamp_sapling", "sakura_sapling" };

	public BlockVariantSapling() {
		super("variant_sapling", VARIANTS);
		setHardness(0F);
		setSoundType(SoundType.PLANT);
		
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, Variant.SWAMP_SAPLING).withProperty(STAGE, 0));
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public ItemBlock createItemInstance(ResourceLocation regName) {
		return new ItemModBlock(this, regName) {
			@Override
			public int getItemBurnTime(ItemStack itemStack) {
				return 100;
			}
		};
	}

	@Override
	public void updateTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			super.updateTick(worldIn, pos, state, rand);

			if (!worldIn.isAreaLoaded(pos, 1))
				return; // Forge: prevent loading unloaded chunks when checking neighbor's light

			if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0)
				grow(worldIn, pos, state, rand);
		}
	}

	public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (state.getValue(STAGE) == 0)
			worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
		else
			generateTree(worldIn, pos, state, rand);
	}

	public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!TerrainGen.saplingGrowTree(worldIn, rand, pos))
			return;

		WorldGenAbstractTree generator = state.getValue(VARIANT) == Variant.SWAMP_SAPLING ? new WorldGenSwampTree(false)
				: new WorldGenSakuraTree(true);

		worldIn.setBlockToAir(pos);
		if (!generator.generate(worldIn, rand, pos))
			worldIn.setBlockState(pos, state, 4);

	}

	@Override
	public boolean canGrow(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		return worldIn.rand.nextFloat() < 0.45;
	}

	@Override
	public void grow(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		grow(worldIn, pos, state, rand);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return SAPLING_AABB;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, Variant.values()[meta & 1]).withProperty(STAGE, (meta & 8) >> 3);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = state.getValue(VARIANT).ordinal() & 1;
		i = i | (state.getValue(STAGE) << 3);

		return i;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Plains;
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT, STAGE);
	}

	@Override
	public IProperty getVariantProp() {
		return VARIANT;
	}

	@Override
	public Class getVariantEnum() {
		return Variant.class;
	}

	@Override
	public IProperty[] getIgnoredProperties() {
		return new IProperty[] { STAGE };
	}
	
	@Override
	public boolean useBlockstateForItem() {
		return false;
	}

	private enum Variant implements IStringSerializable {

		SWAMP_SAPLING, SAKURA_SAPLING;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}

	}

}
