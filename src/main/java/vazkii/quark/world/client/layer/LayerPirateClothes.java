/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 23:10:27 (GMT)]
 */
package vazkii.quark.world.client.layer;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class LayerPirateClothes implements LayerRenderer<EntitySkeleton> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/pirate_overlay.png");
	private final RenderLivingBase<?> render;
	private final ModelSkeleton model;

	public LayerPirateClothes(RenderLivingBase<?> render) {
		this.render = render;
		model = new ModelSkeleton(0.25F, true);
	}

	@Override
	public void doRenderLayer(@Nonnull EntitySkeleton entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		model.setModelAttributes(render.getMainModel());
		model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
		render.bindTexture(TEXTURE);
		model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}

}
