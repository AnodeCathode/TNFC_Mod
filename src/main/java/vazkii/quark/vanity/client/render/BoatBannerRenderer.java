/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 18:42:38 (GMT)]
 */
package vazkii.quark.vanity.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.vanity.feature.BoatSails;

public class BoatBannerRenderer {

	private static final TileEntityBanner BANNER_TE = new TileEntityBanner();

	// Called from ASM. See ClassTransformer
	public static void renderBanner(EntityBoat boat, float partialTicks) {
		if(!ModuleLoader.isFeatureEnabled(BoatSails.class))
			return;

		ItemStack banner = BoatSails.getBanner(boat);
		if(!banner.isEmpty()) {
			BANNER_TE.setItemValues(banner, false);
			GlStateManager.pushMatrix();
			GlStateManager.rotate(180F, 1F, 0F, 0F);
			GlStateManager.rotate(90F, 0F, 1F, 0F);
			GlStateManager.translate(-0.5F, 0.15F, -1.45F);

			TileEntityRendererDispatcher.instance.render(BANNER_TE, 0, 0, 0, partialTicks);
			GlStateManager.popMatrix();
		}
	}

}

