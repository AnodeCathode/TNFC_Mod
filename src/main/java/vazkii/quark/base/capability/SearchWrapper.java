/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Apr 17, 2019, 15:13 AM (EST)]
 */
package vazkii.quark.base.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.quark.api.ICustomSearchHandler;
import vazkii.quark.api.capability.ISearchHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SearchWrapper implements ISearchHandler, ICapabilityProvider {
	private final ItemStack stack;
	private final ICustomSearchHandler item;

	public SearchWrapper(ItemStack stack) {
		this.stack = stack;
		this.item = (ICustomSearchHandler) stack.getItem();
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CAPABILITY;
	}

	@Nullable
	@Override
	@SuppressWarnings("ConstantConditions")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == CAPABILITY ? CAPABILITY.cast(this) : null;
	}

	@Override
	public boolean stackMatchesSearchQuery(String query, ICustomSearchHandler.StringMatcher matcher, ICustomSearchHandler.SearchMethod search) {
		return item.stackMatchesSearchQuery(stack, query, matcher, search);
	}
}
