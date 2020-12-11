package vazkii.quark.oddities.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.arl.client.AtlasSpriteHelper;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.oddities.entity.EntityTotemOfHolding;
import vazkii.quark.oddities.feature.TotemOfHolding;

import javax.annotation.Nonnull;

public class RenderTotemOfHolding extends Render<EntityTotemOfHolding> {

	protected RenderTotemOfHolding(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(@Nonnull EntityTotemOfHolding entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		
		int deathTicks = entity.getDeathTicks();
		boolean dying = entity.isDying();
		float time = ClientTicker.ticksInGame + partialTicks;
		float scale = !dying ? 1F : (Math.max(0, EntityTotemOfHolding.DEATH_TIME - (deathTicks + partialTicks)) / EntityTotemOfHolding.DEATH_TIME);
		float rotation = time + (!dying ? 0 : (deathTicks + partialTicks) * 5);
		double translation = !dying ? (Math.sin(time * 0.03) * 0.1) : ((deathTicks + partialTicks) / EntityTotemOfHolding.DEATH_TIME * 5);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(rotation, 0F, 1F, 0F);
		GlStateManager.translate(-0.5, translation, 0);
		GlStateManager.scale(scale, scale, scale);
		renderTotemIcon();
		GlStateManager.popMatrix();
	}
	
	@Override
	protected boolean canRenderName(EntityTotemOfHolding entity) {
		if(entity.hasCustomName()) {
			Minecraft mc = Minecraft.getMinecraft();
			return !mc.gameSettings.hideGUI && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == Type.ENTITY && mc.objectMouseOver.entityHit == entity;
		}
		
		return false;
	}
	
	private void renderTotemIcon() {
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		Minecraft mc = Minecraft.getMinecraft();
		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		AtlasSpriteHelper.renderIconThicc(TotemOfHolding.totemSprite, 1F / 16F);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityTotemOfHolding entity) {
		return null;
	}
	
	public static IRenderFactory<EntityTotemOfHolding> factory() {
		return RenderTotemOfHolding::new;
	}

}
