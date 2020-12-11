/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/03/2016, 16:57:46 (GMT)]
 */
package vazkii.quark.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderCreeperAngry extends RenderCreeper {

	public RenderCreeperAngry(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	protected void renderModel(@Nonnull EntityCreeper living, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		GlStateManager.color(1F, 1F, 1F);
		super.renderModel(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		GlStateManager.color(1F, 1F, 1F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCreeper entity) {
		if(entity.getCreeperState() == 1) {
			float inverseRed = 1F - (entity.getCreeperFlashIntensity(0F) / 1.17F + 0.1F);
			GL11.glColor3f(1F, inverseRed, inverseRed);
		}
		return super.getEntityTexture(entity);
	}

	public static IRenderFactory<EntityCreeper> factory() {
		return RenderCreeperAngry::new;
	}

}
