/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 23:13:21 (GMT)]
 */
package vazkii.quark.world.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemModArmor;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.world.client.model.ModelPirateHat;

import javax.annotation.Nonnull;

public class ItemPirateHat extends ItemModArmor implements IQuarkItem {

	@SideOnly(Side.CLIENT)
	public static ModelBiped headModel;

	public ItemPirateHat() {
		super("pirate_hat", ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD);
		setMaxDamage(-1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
		if(headModel == null)
			headModel = new ModelPirateHat();

		return headModel;
	}

	@Override
	public boolean hasColor(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "quark:textures/entity/pirate_hat.png";
	}

	@Nonnull
	@Override
	public IRarity getForgeRarity(@Nonnull ItemStack stack) {
		return EnumRarity.RARE;
	}

}
