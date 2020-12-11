/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 08, 2019, 16:57 AM (EST)]
 */
package vazkii.quark.misc.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.misc.ai.EntityAIHorseFollow;
import vazkii.quark.misc.feature.HorseWhistle;

import javax.annotation.Nonnull;

public class ItemHorseWhistle extends ItemMod implements IQuarkItem {
	public ItemHorseWhistle(String name) {
		super(name);
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);
	}

	private void callHorses(EntityLivingBase player) {
		for (AbstractHorse horse : player.world.getEntitiesWithinAABB(AbstractHorse.class,
				player.getEntityBoundingBox().grow(HorseWhistle.horseSummonRange))) {
			if (horse.getDistanceSq(player) <= HorseWhistle.horseSummonRange * HorseWhistle.horseSummonRange) {
				for(EntityAITasks.EntityAITaskEntry task : horse.tasks.taskEntries)
					if(task.action instanceof EntityAIHorseFollow)
						((EntityAIHorseFollow) task.action).setOwner(player);
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 40;
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (!player.world.isRemote) {
			if (count >= 20)
				callHorses(player);
		}
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase player) {
		if (!world.isRemote)
			callHorses(player);
		if (player instanceof EntityPlayer)
			((EntityPlayer) player).getCooldownTracker().setCooldown(this, 20);
		return stack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
		if (!world.isRemote && timeLeft <= 20)
			callHorses(player);

		if (player instanceof EntityPlayer)
			((EntityPlayer) player).getCooldownTracker().setCooldown(this, 20);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack held = player.getHeldItem(hand);

		if (!world.isRemote)
			world.playSound(null, player.posX, player.posY, player.posZ, QuarkSounds.ITEM_HORSE_WHISTLE_BLOW, SoundCategory.PLAYERS, 1f, 1f);

		if (!player.isHandActive())
			player.setActiveHand(hand);

		return ActionResult.newResult(EnumActionResult.SUCCESS, held);
	}
}
