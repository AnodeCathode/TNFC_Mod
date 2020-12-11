/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 04:35:10 (GMT)]
 */
package vazkii.quark.world.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.world.feature.Wraiths;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSoulBead extends ItemMod implements IQuarkItem {

	public ItemSoulBead() {
		super("soul_bead");
		setCreativeTab(CreativeTabs.COMBAT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Wraiths.enableCurse) {
			PotionEffect effect = new PotionEffect(Wraiths.curse, Wraiths.curseTime, 0, true, true);

			String eff = TextFormatting.RED + I18n.format(effect.getEffectName().trim());
			eff = eff + " (" + Potion.getPotionDurationString(effect, 1F) + ")";

			tooltip.add(eff);
		}
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.EAT;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);

		if (Wraiths.enableCurse) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		} else
			return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase player) {
		if (Wraiths.enableCurse) {
			PotionEffect effect = new PotionEffect(Wraiths.curse, Wraiths.curseTime, 0, false, true);
			player.addPotionEffect(effect);

			worldIn.playSound(null, player.posX, player.posY, player.posZ, QuarkSounds.ITEM_SOUL_BEAD_CURSE, player.getSoundCategory(), 1F, 1F);
			player.renderBrokenItemStack(stack);
			stack.shrink(1);
		}

		return super.onItemUseFinish(stack, worldIn, player);
	}

}
