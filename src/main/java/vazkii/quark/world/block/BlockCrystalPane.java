package vazkii.quark.world.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkPane;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockCrystalPane extends BlockQuarkPane {

	public static final PropertyEnum<BlockCrystal.Variants> VARIANT = PropertyEnum.create("variant", BlockCrystal.Variants.class);

	private static String[] buildVariants() {
		BlockCrystal.Variants[] variantTypes = BlockCrystal.Variants.values();
		String[] variants = new String[variantTypes.length];

		for (int i = 0; i < variantTypes.length; i++) {
			variants[i] = variantTypes[i].getName() + "_pane";
		}

		return variants;
	}

	public BlockCrystalPane() {
		super("crystal_pane", Material.GLASS, buildVariants());
		setHardness(3.0F);
		setSoundType(SoundType.GLASS);
		setLightLevel(1.0F * 11F / 15F);
		setLightOpacity(0);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setHarvestLevel("pickaxe", 0);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, NORTH, EAST, WEST, SOUTH, VARIANT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IStateMapper getStateMapper() {
		return (new StateMap.Builder()).withName(VARIANT).withSuffix("_pane").ignore(getIgnoredProperties()).build();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		BlockCrystal.Variants[] variantTypes = BlockCrystal.Variants.values();
		return getDefaultState().withProperty(VARIANT, variantTypes[meta % variantTypes.length]);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos) {
		return state.getValue(VARIANT).color;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		return super.canPaneConnectTo(world, pos, dir) || world.getBlockState(pos.offset(dir)).getBlock() instanceof BlockCrystal;
	}

	
}
