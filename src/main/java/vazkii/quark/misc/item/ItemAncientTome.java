/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [01/06/2016, 19:43:18 (GMT)]
 */
package vazkii.quark.misc.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.misc.feature.AncientTomes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemAncientTome extends ItemMod implements IQuarkItem {

	public ItemAncientTome() {
		super("ancient_tome");
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		NBTTagList ench = ItemNBTHelper.getList(stack, "ench", Constants.NBT.TAG_COMPOUND, true);
		if (ench != null && !ItemNBTHelper.verifyExistence(stack, "StoredEnchantments")) {
			ItemNBTHelper.setList(stack, "StoredEnchantments", ench.copy());
			ItemNBTHelper.getNBT(stack).removeTag("ench");
		}
	}

	@Override
	public boolean isEnchantable(@Nonnull ItemStack stack)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return true;
	}

	@Nonnull
	@Override
	public IRarity getForgeRarity(@Nonnull ItemStack stack) {
		return ItemEnchantedBook.getEnchantments(stack).isEmpty() ? super.getForgeRarity(stack) : EnumRarity.UNCOMMON;
	}

	public static ItemStack getEnchantedItemStack(EnchantmentData ench) {
		ItemStack newStack = ProxyRegistry.newStack(AncientTomes.ancient_tome);
		ItemEnchantedBook.addEnchantment(newStack, ench);
		return newStack;
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (tab.getRelevantEnchantmentTypes().length != 0 || tab == CreativeTabs.SEARCH) {
			for (Enchantment ench : Enchantment.REGISTRY) {
				if (AncientTomes.validEnchants.contains(ench) &&
						(tab == CreativeTabs.SEARCH ? ench.type != null : tab.hasRelevantEnchantmentType(ench.type)))
					subItems.add(getEnchantedItemStack(new EnchantmentData(ench, ench.getMaxLevel())));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		NBTTagList enchantments = ItemEnchantedBook.getEnchantments(stack);

		for (int i = 0; i < enchantments.tagCount(); ++i) {
			NBTTagCompound ench = enchantments.getCompoundTagAt(i);
			Enchantment enchantment = Enchantment.getEnchantmentByID(ench.getShort("id"));

			if (enchantment != null)
				tooltip.add(enchantment.getTranslatedName(ench.getShort("lvl")));
		}
	}

}
