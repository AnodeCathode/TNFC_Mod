package vazkii.quark.misc.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.item.ItemModBlock;
import vazkii.quark.base.item.IQuarkItem;

import javax.annotation.Nonnull;

public class ItemBlackAshBlock extends ItemModBlock implements IQuarkItem {

	public ItemBlackAshBlock(Block block, ResourceLocation res) {
		super(block, res);
	}
	
	// Complete copy of vanilla ItemBlock#onItemUse, but doesn't play the sound
	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();

		if(!block.isReplaceable(worldIn, pos))
			pos = pos.offset(facing);

		ItemStack itemstack = player.getHeldItem(hand);

		if(!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, null)) {
			int i = this.getMetadata(itemstack.getMetadata());
			IBlockState placeState = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

			if(placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, placeState))
				itemstack.shrink(1);

			return EnumActionResult.SUCCESS;
		}
		else return EnumActionResult.FAIL;
	}

}
