/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jan 27, 2015, 3:44:10 PM (GMT)]
 */
package vazkii.quark.oddities.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemModBlock;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.base.lib.LibMisc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemBlockTinyPotato extends ItemModBlock {

	private static final List<String> TYPOS = Arrays.asList("vaskii", "vazki", "voskii", "vazkkii", "vazkki", "vazzki", "vaskki", "vozkii", "vazkil", "vaskil", "vazkill", "vaskill", "vaski");

	private static final int NOT_MY_NAME = 17;

	private static final String TAG_TICKS = "notMyNameTicks";

	public ItemBlockTinyPotato(Block block, ResourceLocation loc) {
		super(block, loc);
		addPropertyOverride(new ResourceLocation(LibMisc.MOD_ID, "angry"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return ItemNBTHelper.getBoolean(stack, "angery", false) ? 1 : 0;
			}
		});
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		return super.isValidArmor(stack, armorType, entity) ||
				(entity instanceof EntityPlayer && ContributorRewardHandler.getTier((EntityPlayer) entity) > 0);
	}

	@Nonnull
	@Override
	public String getTranslationKey(ItemStack stack) {
		if (ItemNBTHelper.getBoolean(stack, "angery", false))
			return super.getTranslationKey(stack) + ".angry";
		return super.getTranslationKey(stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity holder, int itemSlot, boolean isSelected) {
		if(!world.isRemote && holder instanceof EntityPlayer && holder.ticksExisted % 30 == 0 && TYPOS.contains(stack.getDisplayName().toLowerCase())) {
			EntityPlayer player = (EntityPlayer) holder;
			int ticks = ItemNBTHelper.getInt(stack, TAG_TICKS, 0);
			if(ticks < NOT_MY_NAME) {
				player.sendMessage(new TextComponentTranslation("quarkmisc.you_came_to_the_wrong_neighborhood." + ticks).setStyle(new Style().setColor(TextFormatting.RED)));
				ItemNBTHelper.setInt(stack, TAG_TICKS, ticks + 1);
			}
		}
	}

}
