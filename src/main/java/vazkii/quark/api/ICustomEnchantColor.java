/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 03:39:21 (GMT)]
 */
package vazkii.quark.api;

import net.minecraft.item.ItemStack;
import vazkii.quark.api.capability.IEnchantColorProvider;

/**
 * Implement on an Item to allow it to have a custom glint color.
 *
 * This should not be manually checked for. Instead, check if the item provides {@link IEnchantColorProvider}.
 */
public interface ICustomEnchantColor {

	int getEnchantEffectColor(ItemStack stack);

	/**
	 * Due to how enchantment color blending works, by default, the brightness of the effect
	 * color is truncated so the sum of RGB is less or equal to 396, the sum of the RGB
	 * components of the vanilla purple color. Setting this to false allows the color to go
	 * as bright as possible, up to complete opaque if (255, 255, 255).
	 */
	default boolean shouldTruncateColorBrightness(ItemStack stack) {
		return true;
	}

}
