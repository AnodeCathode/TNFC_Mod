package vazkii.quark.decoration.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
import vazkii.arl.util.ModelHandler;
import vazkii.quark.decoration.entity.EntityFlatItemFrame;

public class RenderGlassItemFrame extends RenderFlatItemFrame {

	public static final IRenderFactory<EntityItemFrame> FACTORY = RenderGlassItemFrame::new;
	
	private final TileEntityBanner banner = new TileEntityBanner();
	
	public RenderGlassItemFrame(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected void renderModel(EntityFlatItemFrame entity, Minecraft mc) {
		BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
		ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();

		if(entity.getDisplayedItem().isEmpty()) {
			IBakedModel ibakedmodel = modelmanager.getModel(ModelHandler.resourceLocations.get("glass_item_frame_world"));
			blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
	
	@Override
	protected void renderItemStack(EntityItemFrame itemFrame, ItemStack stack) {
		if(stack.getItem() instanceof ItemBanner) {
			banner.setItemValues(stack, false);
			ResourceLocation res = BannerTextures.BANNER_DESIGNS.getResourceLocation(banner.getPatternResourceLocation(), banner.getPatternList(), banner.getColorList());
			if (res != null) {
				Minecraft.getMinecraft().renderEngine.bindTexture(res);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();

				float f = 1F / 64F;
				float u = 1 * f;
				float v = 1 * f;
				float w = 20 * f;
				float h = 40 * f;

				GlStateManager.pushMatrix();
				GlStateManager.rotate(180, 0F, 0F, 1F);
				GlStateManager.translate(-0.5F, -0.5F, 0.058F);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buffer.pos(0, 1, 0).tex(u + 0, v + h).endVertex();
				buffer.pos(1, 1, 0).tex(u + w, v + h).endVertex();
				buffer.pos(1, 0, 0).tex(u + w, v + 0).endVertex();
				buffer.pos(0, 0, 0).tex(u + 0, v + 0).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}
		}
		else super.renderItemStack(itemFrame, stack);
	}
	
	@Override
	protected void transformItem(EntityItemFrame frame, ItemStack stack) {
		float s = 1.5F;
		if(stack.getItem() instanceof ItemShield) {
			s = 4F;
			GlStateManager.translate(-0.25F, 0F, 0.2F);
			GlStateManager.scale(s, s, s);
		} else {
			GlStateManager.translate(0F, 0F, 0.05F);
			GlStateManager.scale(s, s, s);
		}
		
		super.transformItem(frame, stack);
	}

}
