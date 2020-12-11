package vazkii.quark.world.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemModArmor;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.world.client.model.ModelArchaeologistHat;

import javax.annotation.Nonnull;

public class ItemArchaeologistHat extends ItemModArmor implements IQuarkItem {

	public static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/archaeologist_hat.png");
	
	@SideOnly(Side.CLIENT)
	public static ModelBiped headModel;

	public ItemArchaeologistHat() {
		super("archaeologist_hat", ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
		if(headModel == null)
			headModel = new ModelArchaeologistHat();

		return headModel;
	}

	@Override
	public boolean hasColor(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return TEXTURE.toString();
	}

	@Nonnull
	@Override
	public IRarity getForgeRarity(@Nonnull ItemStack stack) {
		return EnumRarity.RARE;
	}

}
