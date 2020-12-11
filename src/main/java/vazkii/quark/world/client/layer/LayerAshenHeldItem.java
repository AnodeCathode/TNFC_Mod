/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 21:19:43 (GMT)]
 */
package vazkii.quark.world.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

import javax.annotation.Nonnull;

public class LayerAshenHeldItem implements LayerRenderer<EntitySkeleton> {

	protected final RenderLivingBase<?> livingEntityRenderer;

	public LayerAshenHeldItem(RenderLivingBase<?> livingEntityRendererIn) {
		livingEntityRenderer = livingEntityRendererIn;
	}

	@Override
	public void doRenderLayer(@Nonnull EntitySkeleton living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		boolean flag = living.getPrimaryHand() == EnumHandSide.RIGHT;
		ItemStack leftHand = flag ? ItemStack.EMPTY : living.getHeldItemMainhand();
		ItemStack rightHand = flag ? living.getHeldItemMainhand() : ItemStack.EMPTY;

		if(!leftHand.isEmpty() || !rightHand.isEmpty()) {
			GlStateManager.pushMatrix();

			if(livingEntityRenderer.getMainModel().isChild) {
				GlStateManager.translate(0.0F, 0.625F, 0.0F);
				GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
			}

			renderHeldItem(living, rightHand, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
			renderHeldItem(living, leftHand, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
			GlStateManager.popMatrix();
		}
	}

	private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transform, EnumHandSide handSide) {
		if(!stack.isEmpty()) {
			GlStateManager.pushMatrix();

			if(entity.isSneaking())
				GlStateManager.translate(0.0F, 0.2F, 0.0F);

			// Forge: moved this call down, fixes incorrect offset while sneaking.
			((ModelBiped)livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			boolean flag = handSide == EnumHandSide.LEFT;
			GlStateManager.translate((flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, transform, flag);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}
