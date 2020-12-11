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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import vazkii.quark.api.capability.IBoatBanner;

import javax.annotation.Nonnull;

public class BoatBanner implements IBoatBanner, INBTSerializable<NBTTagCompound> {

	@Nonnull
	private ItemStack stack = ItemStack.EMPTY;

	@Override
	public void setStack(@Nonnull ItemStack stack) {
		this.stack = stack;
	}

	@Nonnull
	@Override
	public ItemStack getStack() {
		return stack;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return getStack().serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		getStack().deserializeNBT(nbt);
	}
}
