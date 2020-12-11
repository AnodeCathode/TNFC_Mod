/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 14, 2019, 13:05 AM (EST)]
 */
package vazkii.quark.base.capability;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vazkii.quark.api.capability.IBoatBanner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BackedUpBoatBanner implements IBoatBanner {

	private static final String TAG_BANNER = "quark:banner";

	@Nonnull
	private final EntityBoat boat;

	@Nonnull
	private ItemStack cacheStack = ItemStack.EMPTY;
	@Nullable
	private NBTTagCompound cacheCompound = null;

	public BackedUpBoatBanner(@Nonnull EntityBoat boat) {
		this.boat = boat;
	}

	@Override
	public void setStack(@Nonnull ItemStack stack) {
		NBTTagCompound itemCompound = stack.serializeNBT();

		boat.getEntityData().setTag(TAG_BANNER, itemCompound);
		cacheCompound = itemCompound;
		cacheStack = stack;
	}

	private void setStack(@Nonnull NBTTagCompound itemCompound) {
		ItemStack stack = new ItemStack(itemCompound);

		boat.getEntityData().setTag(TAG_BANNER, itemCompound);
		cacheCompound = itemCompound;
		cacheStack = stack;
	}

	@Nonnull
	@Override
	public ItemStack getStack() {
		NBTTagCompound itemCompound = boat.getEntityData().getCompoundTag(TAG_BANNER);
		if (itemCompound != cacheCompound) setStack(itemCompound);

		return cacheStack;
	}
}
