package vazkii.quark.world.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.world.entity.EntityPickarang;

import javax.annotation.Nonnull;

public class RenderPickarang extends Render<EntityPickarang> {

	public static final IRenderFactory<EntityPickarang> FACTORY = RenderPickarang::new;

	protected RenderPickarang(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(@Nonnull EntityPickarang entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.2F, z);
		GlStateManager.rotate(90F, 1F, 0F, 0F);
		
		Minecraft mc = Minecraft.getMinecraft();
		float time = entity.ticksExisted + (mc.isGamePaused() ? 0 : partialTicks);
		GlStateManager.rotate(time * 20F, 0F, 0F, 1F);

		GlStateManager.enableBlend();
		mc.getRenderItem().renderItem(entity.getStack(), TransformType.FIXED);
		
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityPickarang entity) {
		return null;
	}

}
