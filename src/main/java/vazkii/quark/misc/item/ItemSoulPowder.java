package vazkii.quark.misc.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.misc.entity.EntitySoulPowder;

import javax.annotation.Nonnull;

public class ItemSoulPowder extends ItemMod implements IQuarkItem {

	public ItemSoulPowder() {
		super("soul_powder");
		setCreativeTab(CreativeTabs.MISC);
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("ConstantConditions")
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if(!worldIn.isRemote) {
			BlockPos blockpos = playerIn.getEntityWorld().findNearestStructure("Fortress", playerIn.getPosition(), false);

			if(blockpos != null) {
				itemstack.shrink(1);
				EntitySoulPowder entity = new EntitySoulPowder(worldIn, blockpos.getX(), blockpos.getZ());
				Vec3d look = playerIn.getLookVec();
				entity.setPosition(playerIn.posX + look.x * 2, playerIn.posY + 0.25, playerIn.posZ + look.z * 2);
				worldIn.spawnEntity(entity);

				worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, QuarkSounds.ITEM_SOUL_POWDER_SPAWN, SoundCategory.PLAYERS, 1F, 1F);
			}
		} else playerIn.swingArm(handIn);

		playerIn.addStat(StatList.getObjectUseStats(this));
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

}
