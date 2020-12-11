/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 13:31 AM (EST)]
 */
package vazkii.quark.world.client.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.world.client.render.RenderFoxhound;
import vazkii.quark.world.entity.EntityFoxhound;

import javax.annotation.Nonnull;

public class LayerFoxhoundCollar implements LayerRenderer<EntityFoxhound> {
	private static final ResourceLocation WOLF_COLLAR = new ResourceLocation(LibMisc.MOD_ID, "textures/entity/foxhound_collar.png");
	private final RenderFoxhound wolfRenderer;

	public LayerFoxhoundCollar(RenderFoxhound wolfRendererIn) {
		this.wolfRenderer = wolfRendererIn;
	}

	@Override
	public void doRenderLayer(@Nonnull EntityFoxhound foxhound, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (foxhound.isTamed() && !foxhound.isInvisible()) {
			this.wolfRenderer.bindTexture(WOLF_COLLAR);
			float[] afloat = foxhound.getCollarColor().getColorComponentValues();
			GlStateManager.color(afloat[0], afloat[1], afloat[2]);
			this.wolfRenderer.getMainModel().render(foxhound, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}
}
