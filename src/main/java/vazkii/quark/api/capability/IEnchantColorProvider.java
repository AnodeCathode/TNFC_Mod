/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Apr 17, 2019, 14:39 AM (EST)]
 */
package vazkii.quark.api.capability;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * Provide from an ItemStack to allow it to have a custom glint color.
 */
public interface IEnchantColorProvider {

	@CapabilityInject(IEnchantColorProvider.class)
	Capability<IEnchantColorProvider> CAPABILITY = null;

	static boolean hasProvider(ItemStack stack) {
		return stack.hasCapability(CAPABILITY, null);
	}

	static IEnchantColorProvider getProvider(ItemStack stack) {
		return stack.getCapability(CAPABILITY, null);
	}

	int getEnchantEffectColor();

	/**
 	* Due to how enchantment color blending works, by default, the brightness of the effect
 	* color is truncated so the sum of RGB is less or equal to 396, the sum of the RGB
 	* components of the vanilla purple color. Setting this to false allows the color to go
 	* as bright as possible, up to complete opaque if (255, 255, 255).
 	*/
	default boolean shouldTruncateColorBrightness() {
		return true;
	}

}
