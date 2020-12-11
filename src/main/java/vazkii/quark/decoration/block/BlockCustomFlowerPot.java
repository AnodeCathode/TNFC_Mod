package vazkii.quark.decoration.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IStateMapperProvider;
import vazkii.quark.decoration.client.state.FlowerPotStateMapper;
import vazkii.quark.decoration.feature.ColoredFlowerPots;

import javax.annotation.Nonnull;

public class BlockCustomFlowerPot extends BlockFlowerPot implements IBlockColorProvider, IStateMapperProvider {

	public static final PropertyBool CUSTOM = PropertyBool.create("custom");
	public static final String TAG_TEXTURE_PATH = "texture_path";

	public BlockCustomFlowerPot() {
		this.setHardness(0.0F);
		this.setSoundType(SoundType.STONE);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(CONTENTS, EnumFlowerType.EMPTY)
				.withProperty(LEGACY_DATA, 0)
				.withProperty(CUSTOM, false));
	}

	@Nonnull
	@Override
	protected ExtendedBlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {CONTENTS, LEGACY_DATA, CUSTOM}, new IUnlistedProperty[] {TEXTURE});
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, EntityPlayer player, @Nonnull EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// this is basically a copy of the original method since I needed to override the private method canBePotted
		ItemStack stack = player.getHeldItem(hand);
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityFlowerPot)) {
			return false;
		}
		TileEntityFlowerPot flowerPot = (TileEntityFlowerPot) te;
		ItemStack flower = flowerPot.getFlowerItemStack();

		if(flower.isEmpty()) {
			if(!ColoredFlowerPots.isFlower(stack)) {
				return false;
			}

			flowerPot.setItemStack(stack);
			flowerPot.getTileData().removeTag(TAG_TEXTURE_PATH);
			player.addStat(StatList.FLOWER_POTTED);

			if(!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
		} else {
			ItemHandlerHelper.giveItemToPlayer(player, flower, player.inventory.currentItem);
			flowerPot.setItemStack(ItemStack.EMPTY);
		}

		flowerPot.markDirty();
		world.notifyBlockUpdate(pos, state, state, 3);
		return true;
	}

	@Nonnull
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		state = super.getActualState(state, world, pos);
		// if the flower pot type is empty, but we have a flower, set the extra flag
		if(state.getValue(CONTENTS) == EnumFlowerType.EMPTY) {
			TileEntity te = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
			if(te instanceof TileEntityFlowerPot) {
				if(!((TileEntityFlowerPot)te).getFlowerItemStack().isEmpty()) {
					state = state.withProperty(CUSTOM, true);
				}
			}
		}

		return state;
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
		if(!state.getValue(CUSTOM)) {
			return state;
		}

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityFlowerPot) {
			TileEntityFlowerPot flowerPot = (TileEntityFlowerPot) te;

			// try loading the texture from the client TE
			String texture = flowerPot.getTileData().getString(TAG_TEXTURE_PATH);
			if(texture.isEmpty()) {
				// if missing load it from stored block
				ItemStack stack = flowerPot.getFlowerItemStack();
				if(!stack.isEmpty()) {
					Block block = Block.getBlockFromItem(stack.getItem());
					if(block != Blocks.AIR) {
						// logic to obtain texture string
						IBlockState blockState = block.getStateFromMeta(stack.getItem().getMetadata(stack));
						texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState).getIconName();
						flowerPot.getTileData().setString(TAG_TEXTURE_PATH, texture);
					}
				}
			}
			if(!texture.isEmpty()) {
				state = ((IExtendedBlockState)state).withProperty(TEXTURE, texture);
			}
		}

		return state;
	}


	/*
	 * Comparator
	 */

	@Override
	@SuppressWarnings("deprecation")
	public boolean hasComparatorInputOverride(IBlockState state) {
		return ColoredFlowerPots.enableComparatorLogic;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		if(!ColoredFlowerPots.enableComparatorLogic) {
			return 0;
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityFlowerPot) {
			return getComparatorSignal(((TileEntityFlowerPot) te).getFlowerItemStack());
		}

		return 0;
	}

	private int getComparatorSignal(ItemStack stack) {
		if(stack.isEmpty()) {
			return 0;
		}

		return ColoredFlowerPots.getFlowerComparatorPower(stack);
	}

	@Override
	public IItemColor getItemColor() {
		return (stack, i) -> -1;
	}

	@Override
	public IBlockColor getBlockColor() {
		return (state, world, pos, index) -> {
			if (world != null && pos != null) {
				TileEntity tileentity = world.getTileEntity(pos);
				if (tileentity instanceof TileEntityFlowerPot) {
					ItemStack stack = ((TileEntityFlowerPot)tileentity).getFlowerItemStack();
					return ColoredFlowerPots.getStackBlockColorsSafe(stack, world, pos, 0);
				}
			}
			return -1;
		};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IStateMapper getStateMapper() {
		return FlowerPotStateMapper.INSTANCE;
	}

}
