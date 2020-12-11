package vazkii.quark.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class BlockCrystal extends BlockMetaVariants<BlockCrystal.Variants> implements IQuarkBlock {

	public BlockCrystal() {
		super("crystal", Material.GLASS, Variants.class);
		setHardness(0.3F);
		setSoundType(SoundType.GLASS);
		setLightLevel(1.0F * 11F / 15F);
		setLightOpacity(0);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setHarvestLevel("pickaxe", 0);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
		Block block = iblockstate.getBlock();

		return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos) {
		return state.getValue(variantProp).color;
	}

	public enum Variants implements IStringSerializable {
		CRYSTAL_WHITE(1f, 1f, 1f),
		CRYSTAL_RED(1f, 0f, 0f),
		CRYSTAL_ORANGE(1f, 0.5f, 0f),
		CRYSTAL_YELLOW(1f, 1f, 0f),
		CRYSTAL_GREEN(0f, 1f, 0f),
		CRYSTAL_BLUE(0f, 1f, 1f),
		CRYSTAL_INDIGO(0f, 0f, 1f),
		CRYSTAL_VIOLET(1f, 0f, 1f),
		CRYSTAL_BLACK(0f, 0f, 0f);

		public final float[] color;

		Variants(float r, float g, float b) {
			this.color = new float[] {r, g, b};
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
