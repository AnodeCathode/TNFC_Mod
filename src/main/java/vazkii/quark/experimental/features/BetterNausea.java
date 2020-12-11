/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/03/2016, 16:37:16 (GMT)]
 */
package vazkii.quark.experimental.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.quark.base.client.ClientReflectiveAccessor;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class BetterNausea extends Feature {

	private float timeInPortal;
	private float prevTimeInPortal;
	private boolean viewBobbing;

	protected static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation("textures/misc/vignette.png");
	private static final float TIME_CONST = (float) Math.PI / 40;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderTick(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if (player != null && player.isPotionActive(MobEffects.NAUSEA)) {
			if (event.phase == TickEvent.Phase.START) {
				timeInPortal = player.timeInPortal;
				prevTimeInPortal = player.prevTimeInPortal;
				viewBobbing = mc.gameSettings.viewBobbing;

				player.timeInPortal = 0;
				player.prevTimeInPortal = 0;

				mc.gameSettings.viewBobbing = true;
			} else {
				player.timeInPortal = timeInPortal;
				player.prevTimeInPortal = prevTimeInPortal;
				mc.gameSettings.viewBobbing = viewBobbing;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderTick(RenderGameOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if (player != null && player.isPotionActive(MobEffects.NAUSEA)) {
			if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {

				float time = ClientReflectiveAccessor.getUpdateCount(mc.entityRenderer) + event.getPartialTicks();
				float sinAmount = MathHelper.sin(time * 1.5f * TIME_CONST) * 0.0625f;

				ScaledResolution res = event.getResolution();
				mc.entityRenderer.setupOverlayRendering();
				GlStateManager.enableBlend();

				GlStateManager.disableDepth();
				GlStateManager.depthMask(false);
				GlStateManager.tryBlendFuncSeparate(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR,
						SourceFactor.ONE, DestFactor.ZERO);

				GlStateManager.color(0.9375f + sinAmount, 0.9375f + sinAmount, 0.9375f + sinAmount);

				int w = res.getScaledWidth();
				int h = res.getScaledHeight();

				mc.getTextureManager().bindTexture(VIGNETTE_TEX_PATH);
				for (int i = 0; i < 2; i++) {
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferbuilder = tessellator.getBuffer();
					bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					bufferbuilder.pos(0, h, -90).tex(0, 1).endVertex();
					bufferbuilder.pos(w, h, -90).tex(1, 1).endVertex();
					bufferbuilder.pos(w, 0, -90).tex(1, 0).endVertex();
					bufferbuilder.pos(0, 0, -90).tex(0, 0).endVertex();
					tessellator.draw();
				}

				GlStateManager.depthMask(true);
				GlStateManager.enableDepth();
				GlStateManager.color(1, 1, 1);
				GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
						SourceFactor.ONE, DestFactor.ZERO);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void renderNausea(EntityRenderer renderer, float partialTicks) {
		if (!ModuleLoader.isFeatureEnabled(BetterNausea.class))
			return;

		Minecraft mc = Minecraft.getMinecraft();

		EntityPlayer player = mc.player;
		if (player != null) {
			PotionEffect nausea = player.getActivePotionEffect(MobEffects.NAUSEA);
			if (nausea != null) {

				float time = ClientReflectiveAccessor.getUpdateCount(renderer) + partialTicks;
				float sinAmount = MathHelper.sin(time * TIME_CONST) / 2;

				float yaw = player.cameraYaw;
				float prevYaw = player.prevCameraYaw;

				double distX = player.posX - player.prevPosX;
				double distY = player.posY - player.prevPosY;
				double distZ = player.posZ - player.prevPosZ;

				float dist = MathHelper.sqrt(distX * distX + distY * distY + distZ * distZ);

				dist = Math.min(0.15f, Math.max(0.1f, dist));

				player.cameraYaw += dist * sinAmount;
				player.prevCameraYaw += dist * sinAmount;

				ClientReflectiveAccessor.applyBobbing(renderer, partialTicks);

				player.cameraYaw = yaw;
				player.prevCameraYaw = prevYaw;
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
}
