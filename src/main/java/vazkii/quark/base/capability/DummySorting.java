/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Apr 17, 2019, 15:20 AM (EST)]
 */
package vazkii.quark.base.capability;

import net.minecraft.item.ItemStack;
import vazkii.quark.api.ICustomSorting;

import java.util.Comparator;

public class DummySorting implements ICustomSorting {
	@Override
	public Comparator<ItemStack> getItemComparator() {
		return (first, second) -> 0;
	}

	@Override
	public String getSortingCategory() {
		return "";
	}
}
