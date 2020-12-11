/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 14, 2019, 13:02 AM (EST)]
 */
package vazkii.quark.api.capability;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;

/**
 * Defines a boat-banner holder.
 *
 * Fairly self-explanatory.
 */
public interface IBoatBanner {

	@CapabilityInject(IBoatBanner.class)
	Capability<IBoatBanner> CAPABILITY = null;

	static boolean hasBoatBanner(Entity entity) {
		return entity.hasCapability(CAPABILITY, null);
	}

	static IBoatBanner getBoatBanner(Entity entity) {
		return entity.getCapability(CAPABILITY, null);
	}


	void setStack(@Nonnull ItemStack stack);

	@Nonnull
	ItemStack getStack();
}
