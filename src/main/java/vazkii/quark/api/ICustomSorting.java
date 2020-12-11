package vazkii.quark.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Comparator;

/**
 * Implement on an item or provide as a capability
 * to have it include a custom method for sorting
 * with quark's sorting system.
 *
 * You should not check for Items implementing this.
 * Instead, check if they provide this as a capability.
 */
public interface ICustomSorting {

	@CapabilityInject(ICustomSorting.class)
	Capability<ICustomSorting> CAPABILITY = null;

	static boolean hasSorting(ItemStack stack) {
		return stack.hasCapability(CAPABILITY, null);
	}

	static ICustomSorting getSorting(ItemStack stack) {
		return stack.getCapability(CAPABILITY, null);
	}

	/**
	 * Gets a comparator to compare the items. 
	 */
	Comparator<ItemStack> getItemComparator();
	
	/**
	 * Gets this item's category. Items will only be compared together if they are in the same category.
	 * Make sure your category is unique. Prefixing it with the mod ID is a good idea.
	 */
	String getSortingCategory();
	
}
