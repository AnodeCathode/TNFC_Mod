package vazkii.quark.world.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.world.base.EnumStonelingVariant;
import vazkii.quark.world.entity.EntityStoneling;

import javax.annotation.Nonnull;

public class ItemDiamondHeart extends ItemMod implements IQuarkItem {

	public ItemDiamondHeart() {
		super("diamond_heart");
		setCreativeTab(CreativeTabs.MISC);
	}
	
	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState stateAt = worldIn.getBlockState(pos);
		ItemStack stack = player.getHeldItem(hand);
		
		if(player.canPlayerEdit(pos, facing, stack) && stateAt.getBlockHardness(worldIn, pos) != -1) {

			EnumStonelingVariant variant = null;
			for (EnumStonelingVariant possibleVariant : EnumStonelingVariant.values()) {
				if (possibleVariant.acceptsState(stateAt))
					variant = possibleVariant;
			}

			if (variant != null) {
				if (!worldIn.isRemote) {
					worldIn.setBlockToAir(pos);
					worldIn.playEvent(2001, pos, Block.getStateId(stateAt));

					EntityStoneling stoneling = new EntityStoneling(worldIn);
					stoneling.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
					stoneling.setPlayerMade(true);
					stoneling.rotationYaw = player.rotationYaw + 180F;
					stoneling.onInitialSpawn(worldIn.getDifficultyForLocation(pos), variant);
					worldIn.spawnEntity(stoneling);

					if (!player.capabilities.isCreativeMode)
						stack.shrink(1);
				}

				return EnumActionResult.SUCCESS;
			}
		}
		
		return EnumActionResult.PASS;
	}

	@Nonnull
	@Override
	public IRarity getForgeRarity(@Nonnull ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

}
