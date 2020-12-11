/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 11, 2019, 16:46 AM (EST)]
 */
package vazkii.quark.world.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.world.entity.EntityStoneling;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class LayerStonelingItem implements LayerRenderer<EntityStoneling> {
	@Override
	public void doRenderLayer(@Nonnull EntityStoneling stoneling, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		ItemStack stack = stoneling.getCarryingItem();
		if (!stack.isEmpty()) {
			boolean isBlock = stack.getItem() instanceof ItemBlock;
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, 0.5F, 0F);
			if(!isBlock) {
				GlStateManager.rotate(stoneling.getItemAngle() + 180, 0F, 1F, 0F);
				GlStateManager.rotate(90F, 1F, 0F, 0F);
			} else GlStateManager.rotate(180F, 1F, 0F, 0F);
			
			GlStateManager.scale(0.725F, 0.725F, 0.725F);
			Minecraft mc = Minecraft.getMinecraft();
			mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
