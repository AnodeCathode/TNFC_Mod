package vazkii.quark.oddities.client.render;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import vazkii.quark.oddities.tile.TilePipe;
import vazkii.quark.oddities.tile.TilePipe.PipeItem;

public class RenderTilePipe extends TileEntitySpecialRenderer<TilePipe> {

	private Random random = new Random();
	
	@Override
	public void render(TilePipe te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		RenderItem render = Minecraft.getMinecraft().getRenderItem();
		Iterator<PipeItem> items = te.getItemIterator();

		while(items.hasNext())
			renderItem(items.next(), render, partialTicks);
		GlStateManager.popMatrix();
	}

	private void renderItem(PipeItem item, RenderItem render, float partial) {
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		RenderHelper.enableStandardItemLighting();

		float scale = 0.4F;
		float fract = item.getTimeFract(partial);
		float shiftFract = fract - 0.5F;
		EnumFacing face = item.outgoingFace;
		if(fract < 0.5)
			face = item.incomingFace.getOpposite();

		float offX = (face.getXOffset() * 1F);
		float offY = (face.getYOffset() * 1F);
		float offZ = (face.getZOffset() * 1F);
		GlStateManager.translate(offX * shiftFract, offY * shiftFract, offZ * shiftFract);

		GlStateManager.scale(scale, scale, scale);

		float speed = 4F;
		GlStateManager.rotate((item.timeInWorld + partial) * speed, 0F, 1F, 0F);

        int seed = item.stack.isEmpty() ? 187 : Item.getIdFromItem(item.stack.getItem()) + item.stack.getMetadata();
        random.setSeed(seed);
		
		int count = getModelCount(item.stack);
		for(int i = 0; i < count; i++) {
			GlStateManager.pushMatrix();
			if(i > 0) {
				float spread = 0.15F;
                float x = (this.random.nextFloat() * 2.0F - 1.0F) * spread;
                float y = (this.random.nextFloat() * 2.0F - 1.0F) * spread;
                float z = (this.random.nextFloat() * 2.0F - 1.0F) * spread;
                GlStateManager.translate(x, y, z);
			}
				
			render.renderItem(item.stack, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}

		RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	// RenderEntityItem copy
	protected int getModelCount(ItemStack stack) {
		if(stack.getCount() > 48)
			return 5;
		
		if(stack.getCount() > 32)
			return 4;
		
		if(stack.getCount() > 16)
			return 3;
		
		if (stack.getCount() > 1)
			return 2;

		return 1;
	}

}
