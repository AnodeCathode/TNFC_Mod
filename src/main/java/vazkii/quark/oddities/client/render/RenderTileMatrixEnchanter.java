package vazkii.quark.oddities.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;

public class RenderTileMatrixEnchanter extends TileEntitySpecialRenderer<TileMatrixEnchanter>
{
	/** The texture for the book above the enchantment table. */
	private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
	private final ModelBook modelBook = new ModelBook();

	@Override
	public void render(TileMatrixEnchanter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		float time = te.tickCount + partialTicks;

		float f1 = te.bookRotation - te.bookRotationPrev;
		while (f1 >= Math.PI)
			f1 -= (Math.PI * 2F);
		while (f1 < -Math.PI)
			f1 += (Math.PI * 2F);

		float rot = te.bookRotationPrev + f1 * partialTicks;
		float bookOpen = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
		
		renderBook(te, time, rot, partialTicks);
		
		ItemStack item = te.getStackInSlot(0);
		if(!item.isEmpty())
			renderItem(item, time, bookOpen, rot);

		GlStateManager.popMatrix();
	}

	private void renderItem(ItemStack item, float time, float bookOpen, float rot) {
		GlStateManager.pushMatrix();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.translate(0.5F, 0.8F, 0.5F);
		GlStateManager.scale(0.6F, 0.6F, 0.6F);
		
		rot *= -180F / (float) Math.PI;
		rot += 90F;
		rot *= bookOpen;
		
		GlStateManager.rotate(rot, 0F, 1F, 0F);
		GlStateManager.translate(0, bookOpen * 1.4F, Math.sin(bookOpen * Math.PI));
		GlStateManager.rotate(-90F * (bookOpen - 1F), 1F, 0F, 0F);
		
		float trans = (float) Math.sin(time * 0.06) * bookOpen * 0.2F;
		GlStateManager.translate(0F, trans, 0F);

		RenderItem render = Minecraft.getMinecraft().getRenderItem();
		render.renderItem(item, ItemCameraTransforms.TransformType.FIXED);

		RenderHelper.disableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	// Copy of vanilla's book render
	private void renderBook(TileMatrixEnchanter te, float time, float bookRot, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.85F + MathHelper.sin(time * 0.1F) * 0.01F, 0.5F);
		
		GlStateManager.rotate(-bookRot * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
		this.bindTexture(TEXTURE_BOOK);
		float f3 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.25F;
		float f4 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.75F;
		f3 = (f3 - MathHelper.fastFloor(f3)) * 1.6F - 0.3F;
		f4 = (f4 - MathHelper.fastFloor(f4)) * 1.6F - 0.3F;

		if (f3 < 0.0F)
		{
			f3 = 0.0F;
		}

		if (f4 < 0.0F)
		{
			f4 = 0.0F;
		}

		if (f3 > 1.0F)
		{
			f3 = 1.0F;
		}

		if (f4 > 1.0F)
		{
			f4 = 1.0F;
		}

		float f5 = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
		GlStateManager.enableCull();
		this.modelBook.render(null, time, f3, f4, f5, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}

}
