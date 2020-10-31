package mods.immibis.tubestuff;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.*;
import static org.lwjgl.opengl.GL11.*;

import mods.immibis.core.api.util.Dir;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// render pickaxe coming out the front, swinging when it breaks a block
@SideOnly(Side.CLIENT)
public class RenderTileBlockBreaker extends TileEntitySpecialRenderer {
	private RenderBlocks renderBlocks = new RenderBlocks();
	
	//private static final boolean TOOL_STICKS_OUT = false;
	private static final boolean RENDER_PISTON = false;
	static final boolean HALF_HEIGHT = false;

	@Override
	public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float partialTick) {
		TileBlockBreaker te = (TileBlockBreaker)var1;
		ItemStack tool = te.getTool();
		
		float swingTime = te.swingTime + (te.isBreaking || te.swingTime != 0 ? partialTick : 0);
		float angle = (float)(1 - Math.cos(swingTime / TileBlockBreaker.SWING_PERIOD * 2 * Math.PI)) * -45;
		
		//OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		//OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		
		int br = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, br & 0xFFFF, br >> 16);
		
		glPushMatrix();
		glTranslated(x+0.5, y+8/16.0, z+0.5);
		
		glDisable(GL_LIGHTING);
		
		switch(te.facing) {
		case Dir.PX: break;
		case Dir.NX: glRotatef(180, 0, 1, 0); break;
		case Dir.PZ: glRotatef(-90, 0, 1, 0); break;
		case Dir.NZ: glRotatef(90, 0, 1, 0); break;
		}
		
		// Render piston
		if(RENDER_PISTON)
		{
			double pistonExtensionDist = angle / -90;
			
			field_147501_a.field_147553_e.bindTexture(TextureMap.locationBlocksTexture);
			Tessellator t = Tessellator.instance;
			double u, v, du, dv;
			
			double _x = -0.5;
			double _y = -0.5;
			double _z = -0.25;
			double cx = 0.5, cy=0.5*0.75, cz=0.5;
			
			t.startDrawingQuads();
			t.setBrightness(te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0));
			
			// base
			
			u = 208/256.0; v = 96/256.0; du = dv = 1/16.0;
			
			t.setNormal(0.0F, -1.0F, 0.0F);
	        t.addVertexWithUV(_x   , _y, _z   , u, v);
			t.addVertexWithUV(_x+cx, _y, _z   , u+du, v);
			t.addVertexWithUV(_x+cx, _y, _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x   , _y, _z+cz, u, v+dv);
			
			u = 192/256.0; v = 100/256.0; dv = 12/256.0; 
			
			t.setNormal(0.0F, 0.0F, -1.0F);
	        t.addVertexWithUV(_x+cx, _y+cy, _z, u, v);
			t.addVertexWithUV(_x+cx, _y   , _z, u, v+dv);
			t.addVertexWithUV(_x   , _y   , _z, u+du, v+dv);
			t.addVertexWithUV(_x   , _y+cy, _z, u+du, v);
			
			t.setNormal(0.0F, 0.0F, 1.0F);
	        t.addVertexWithUV(_x+cx, _y+cy, _z+cz, u, v);
			t.addVertexWithUV(_x   , _y+cy, _z+cz, u+du, v);
			t.addVertexWithUV(_x   , _y   , _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x+cx, _y   , _z+cz, u, v+dv);
			
			t.setNormal(-1.0F, 0.0F, 0.0F);
	        t.addVertexWithUV(_x, _y+cy, _z   , u, v);
			t.addVertexWithUV(_x, _y   , _z   , u, v+dv);
			t.addVertexWithUV(_x, _y   , _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x, _y+cy, _z+cz, u+du, v);
			
			t.setNormal(1.0F, 0.0F, 0.0F);
	        t.addVertexWithUV(_x+cx, _y+cy, _z   , u, v);
			t.addVertexWithUV(_x+cx, _y+cy, _z+cz, u+du, v);
			t.addVertexWithUV(_x+cx, _y   , _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x+cx, _y   , _z   , u, v+dv);
			
			u = 224/256.0; v = 96/256.0; dv = 1/16.0;
			
			t.setNormal(0.0F, 1.0F, 0.0F);
	        t.addVertexWithUV(_x   , _y+cy, _z   , u, v);
			t.addVertexWithUV(_x   , _y+cy, _z+cz, u, v+dv);
			t.addVertexWithUV(_x+cx, _y+cy, _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x+cx, _y+cy, _z   , u+du, v);
			
			double baseTop = y + cy;
			
			// head
			cy /= 3;
			//_y = (1 - 4*cy) * pistonExtensionDist - 0.5 + 3*cy;
			_y = Math.sin(-angle*Math.PI/180)*6/16 - cy;
			double headBottom = _y;
			
			u = 176/256.0; v = 96/256.0; du = dv = 1/16.0;
			
			t.setNormal(0.0F, -1.0F, 0.0F);
	        t.addVertexWithUV(_x   , _y, _z   , u, v);
			t.addVertexWithUV(_x+cx, _y, _z   , u+du, v);
			t.addVertexWithUV(_x+cx, _y, _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x   , _y, _z+cz, u, v+dv);
			
			u = 192/256.0; v = 96/256.0; dv = 4/256.0; 
			
			t.setNormal(0.0F, 0.0F, -1.0F);
	        t.addVertexWithUV(_x+cx, _y+cy, _z, u, v);
			t.addVertexWithUV(_x+cx, _y   , _z, u, v+dv);
			t.addVertexWithUV(_x   , _y   , _z, u+du, v+dv);
			t.addVertexWithUV(_x   , _y+cy, _z, u+du, v);
			
			t.setNormal(0.0F, 0.0F, 1.0F);
	        t.addVertexWithUV(_x+cx, _y+cy, _z+cz, u, v);
			t.addVertexWithUV(_x   , _y+cy, _z+cz, u+du, v);
			t.addVertexWithUV(_x   , _y   , _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x+cx, _y   , _z+cz, u, v+dv);
			
			t.setNormal(-1.0F, 0.0F, 0.0F);
	        t.addVertexWithUV(_x, _y+cy, _z   , u, v);
			t.addVertexWithUV(_x, _y   , _z   , u, v+dv);
			t.addVertexWithUV(_x, _y   , _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x, _y+cy, _z+cz, u+du, v);
			
			t.setNormal(1.0F, 0.0F, 0.0F);
	        t.addVertexWithUV(_x+cx, _y+cy, _z   , u, v);
			t.addVertexWithUV(_x+cx, _y+cy, _z+cz, u+du, v);
			t.addVertexWithUV(_x+cx, _y   , _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x+cx, _y   , _z   , u, v+dv);
			
			u = 176/256.0; v = 96/256.0; dv = 1/16.0;
			
			t.setNormal(0.0F, 1.0F, 0.0F);
	        t.addVertexWithUV(_x   , _y+cy, _z   , u, v);
			t.addVertexWithUV(_x   , _y+cy, _z+cz, u, v+dv);
			t.addVertexWithUV(_x+cx, _y+cy, _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x+cx, _y+cy, _z   , u+du, v);
			
			// arm
			cx /= 4;
			cz /= 4;
			cy *= 4;
			_x += cx*1.5;
			_z += cz*1.5;
			_y = headBottom - cy;
			
			//u = 208/256.0; v = 96/256.0; du = dv = 1/16.0;
			
			//t.setNormal(0.0F, -1.0F, 0.0F);
	        //t.addVertexWithUV(_x   , _y, _z   , u, v);
			//t.addVertexWithUV(_x+cx, _y, _z   , u+du, v);
			//t.addVertexWithUV(_x+cx, _y, _z+cz, u+du, v+dv);
			//t.addVertexWithUV(_x   , _y, _z+cz, u, v+dv);
			
			u = 192/256.0; v = 96/256.0; dv = 4/256.0; 
			
			t.setNormal(0.0F, 0.0F, -1.0F);
	        t.addVertexWithUV(_x+cx, _y+cy, _z, u, v);
			t.addVertexWithUV(_x+cx, _y   , _z, u+du, v);
			t.addVertexWithUV(_x   , _y   , _z, u+du, v+dv);
			t.addVertexWithUV(_x   , _y+cy, _z, u, v+dv);
			
			t.setNormal(0.0F, 0.0F, 1.0F);
	        t.addVertexWithUV(_x+cx, _y+cy, _z+cz, u, v);
			t.addVertexWithUV(_x   , _y+cy, _z+cz, u, v+dv);
			t.addVertexWithUV(_x   , _y   , _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x+cx, _y   , _z+cz, u+du, v);
			
			t.setNormal(-1.0F, 0.0F, 0.0F);
	        t.addVertexWithUV(_x, _y+cy, _z   , u, v);
			t.addVertexWithUV(_x, _y   , _z   , u+du, v);
			t.addVertexWithUV(_x, _y   , _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x, _y+cy, _z+cz, u, v+dv);
			
			t.setNormal(1.0F, 0.0F, 0.0F);
	        t.addVertexWithUV(_x+cx, _y+cy, _z   , u, v);
			t.addVertexWithUV(_x+cx, _y+cy, _z+cz, u, v+dv);
			t.addVertexWithUV(_x+cx, _y   , _z+cz, u+du, v+dv);
			t.addVertexWithUV(_x+cx, _y   , _z   , u+du, v);
			
			//u = 224/256.0; v = 96/256.0; dv = 1/16.0;
			
			//t.setNormal(0.0F, 1.0F, 0.0F);
	        //t.addVertexWithUV(_x   , _y+cy, _z   , u, v);
			//t.addVertexWithUV(_x   , _y+cy, _z+cz, u, v+dv);
			//t.addVertexWithUV(_x+cx, _y+cy, _z+cz, u+du, v+dv);
			//t.addVertexWithUV(_x+cx, _y+cy, _z   , u+du, v);
			
			t.draw();
		}
		
		
		glRotatef(angle, 0, 0, 1);
		
		glColor3f(1, 1, 1);
		
		// Render tool
		if(tool != null)
		{
			GL11.glPushMatrix();
			//float f4 = 0.8F;

            //float f7 = 0; // swing progress
            //float f6 = MathHelper.sin(f7 * (float)Math.PI);
            //float f5 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
            //GL11.glTranslatef(-f5 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI * 2.0F) * 0.2F, -f6 * 0.2F);

            GL11.glTranslatef(0.5f, -0.5f, 0);
            GL11.glRotatef(180.0F, 0.0F, 1.00F, 0.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            //f7 = 0; // swing progress
            //f6 = MathHelper.sin(f7 * f7 * (float)Math.PI);
            //f5 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
            //GL11.glRotatef(-f6 * 20.0F, 0.0F, 1.0F, 0.0F);
            //GL11.glRotatef(-f5 * 20.0F, 0.0F, 0.0F, 1.0F);
            //GL11.glRotatef(-f5 * 80.0F, 1.0F, 0.0F, 0.0F);
            //float f8 = 0.4F;
            //GL11.glScalef(f8, f8, f8);
            float f11;
            float f12;

            
            if (tool.getItem().shouldRotateAroundWhenRendering())
            {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (tool.getItem().requiresMultipleRenderPasses())
            {
                this.renderItem(tool, 0, te);
                for (int k = 1; k < tool.getItem().getRenderPasses(tool.getItemDamage()); k++)
                {
                    int i1 = tool.getItem().getColorFromItemStack(tool, k);
                    float f10 = (float)(i1 >> 16 & 255) / 255.0F;
                    f11 = (float)(i1 >> 8 & 255) / 255.0F;
                    f12 = (float)(i1 & 255) / 255.0F;
                    GL11.glColor4f(f10, f11, f12, 1.0F);
                    this.renderItem(tool, k, te);
                }
            }
            else
            {
                this.renderItem(tool, 0, te);
            }

            GL11.glPopMatrix();
		}
		
		// Render axle
		{
			double th = 1/16.0;
			Tessellator t = Tessellator.instance;
			glDisable(GL_TEXTURE_2D);
			t.startDrawing(GL_QUAD_STRIP);
			t.setBrightness(te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0));
			t.setColorOpaque(0, 50, 50);
			t.addVertex(-th,  0,-0.5);
			t.addVertex(-th,  0, 0.5);
			t.addVertex(  0, th,-0.5);
			t.addVertex(  0, th, 0.5);
			t.addVertex( th,  0,-0.5);
			t.addVertex( th,  0, 0.5);
			t.addVertex(  0,-th,-0.5);
			t.addVertex(  0,-th, 0.5);
			t.addVertex(-th,  0,-0.5);
			t.addVertex(-th,  0, 0.5);
			t.draw();
			glEnable(GL_TEXTURE_2D);
		}
		
		// Render piston "handle"
		if(RENDER_PISTON)
		{
			Tessellator t = Tessellator.instance;
			//GL11.glBindTexture(GL11.GL_TEXTURE_2D, tileEntityRenderer.renderEngine.getTexture("/immibis/tubestuff/blocks.png"));
			t.startDrawing(GL_QUADS);
			t.setBrightness(te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0));
			t.addVertexWithUV( 0  , 0.01, -0.5, 224/256.0, 8/256.0);
			t.addVertexWithUV( 0  , 0.01,  0.5, 240/256.0, 8/256.0);
			t.addVertexWithUV(-0.5, 0.01,  0.5, 240/256.0, 16/256.0);
			t.addVertexWithUV(-0.5, 0.01, -0.5, 224/256.0, 16/256.0);
			
			t.addVertexWithUV( 0  , 0.01, -0.5, 224/256.0, 8/256.0);
			t.addVertexWithUV(-0.5, 0.01, -0.5, 224/256.0, 16/256.0);
			t.addVertexWithUV(-0.5, 0.01,  0.5, 240/256.0, 16/256.0);
			t.addVertexWithUV( 0  , 0.01,  0.5, 240/256.0, 8/256.0);
			t.draw();
		}
        
		glPopMatrix();
		
		glEnable(GL_LIGHTING);
		
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
	
	public void renderItem(ItemStack par2ItemStack, int par3, TileBlockBreaker tile)
    {
		if(tile.disableAllItemRender)
			return;

        GL11.glPushMatrix();
		
        try {
	        Block block = null;
	        if (par2ItemStack.getItem() instanceof ItemBlock)
	            block = ((ItemBlock)par2ItemStack.getItem()).field_150939_a;
	        
	        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(par2ItemStack, EQUIPPED);
	        
	        if (customRenderer != null && !tile.disableCustomRender)
	        {
	        	try {
		            field_147501_a.field_147553_e.bindTexture(field_147501_a.field_147553_e.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
		            ForgeHooksClient.renderEquippedItem(ItemRenderType.EQUIPPED, customRenderer, renderBlocks, null, par2ItemStack);
	        	} catch(Throwable t) {
	        		tile.disableCustomRender = true;
	        		t.printStackTrace();
	        	}
	        }
	        else if (block != null && par2ItemStack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(block.getRenderType()))
	        {
	        	field_147501_a.field_147553_e.bindTexture(field_147501_a.field_147553_e.getResourceLocation(0));
	            GL11.glTranslatef(0.5f, 0.5f, 0);
	            float s = (float)(1/Math.sqrt(2));
	            GL11.glScalef(s, s, s);
	            this.renderBlocks.renderBlockAsItem(block, par2ItemStack.getItemDamage(), 1.0F);
	        }
	        else
	        {
	            IIcon icon = par2ItemStack.getIconIndex();
	
	            if (icon == null)
	            {
	                return;
	            }
	
	            field_147501_a.field_147553_e.bindTexture(field_147501_a.field_147553_e.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
	            
	            Tessellator tessellator = Tessellator.instance;
	            //float f4 = 0.0F;
	            //float f5 = 0.3F;
	            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	            //GL11.glTranslatef(-f4, -f5, 0.0F);
	            //float f6 = 1.5F;
	            //GL11.glScalef(f6, f6, f6);
	            //GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
	            //GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
	            //GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
	            renderItemIn2D(tessellator, icon, 0.0625F);
	
	            // Todo?
	            /*if (par2ItemStack != null && par2ItemStack.hasEffect() && par3 == 0)
	            {
	                GL11.glDepthFunc(GL11.GL_EQUAL);
	                GL11.glDisable(GL11.GL_LIGHTING);
	                this.tileEntityRenderer.renderEngine.bindTexture("%blur%/misc/glint.png");
	                GL11.glEnable(GL11.GL_BLEND);
	                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
	                float f7 = 0.76F;
	                GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
	                GL11.glMatrixMode(GL11.GL_TEXTURE);
	                GL11.glPushMatrix();
	                float f8 = 0.125F;
	                GL11.glScalef(f8, f8, f8);
	                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
	                GL11.glTranslatef(f9, 0.0F, 0.0F);
	                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
	                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
	                GL11.glPopMatrix();
	                GL11.glPushMatrix();
	                GL11.glScalef(f8, f8, f8);
	                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
	                GL11.glTranslatef(-f9, 0.0F, 0.0F);
	                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
	                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
	                GL11.glPopMatrix();
	                GL11.glMatrixMode(GL11.GL_MODELVIEW);
	                GL11.glDisable(GL11.GL_BLEND);
	                GL11.glEnable(GL11.GL_LIGHTING);
	                GL11.glDepthFunc(GL11.GL_LEQUAL);
	            }*/
	
	            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	        }
	
		} catch(Throwable t) {
			t.printStackTrace();
			tile.disableAllItemRender = true;
		}
		GL11.glPopMatrix();
    }

    private void renderItemIn2D(Tessellator par0Tessellator, IIcon icon, float par7)
    {
    	float minu = icon.getMinU();
    	float maxu = icon.getMaxU();
    	float minv = icon.getMinV();
    	float maxv = icon.getMaxV();
    	
    	par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, 1.0F);
        par0Tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, maxu, maxv);
        par0Tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, minu, maxv);
        par0Tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, minu, minv);
        par0Tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, maxu, minv);
        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, -1.0F);
        par0Tessellator.addVertexWithUV(0.0D, 1.0D, (double)(0.0F - par7), maxu, minv);
        par0Tessellator.addVertexWithUV(1.0D, 1.0D, (double)(0.0F - par7), minu, minv);
        par0Tessellator.addVertexWithUV(1.0D, 0.0D, (double)(0.0F - par7), minu, maxv);
        par0Tessellator.addVertexWithUV(0.0D, 0.0D, (double)(0.0F - par7), maxu, maxv);
        par0Tessellator.draw();
        
        int numX = (int)(icon.getIconWidth() * (maxu - minu) + 0.5f);
        int numY = (int)(icon.getIconHeight() * (maxv - minv) + 0.5f);
        
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        int k;
        float f7;
        float f8;
        
        float texelU = (icon.getMaxU() - icon.getMinU()) / icon.getIconWidth();
        float texelV = (icon.getMaxV() - icon.getMaxV()) / icon.getIconHeight();

        for (k = 0; k < numX; ++k)
        {
            f7 = (float)k / numX;
            f8 = maxu + (minu - maxu) * f7 - texelV / 2;
            par0Tessellator.addVertexWithUV((double)f7, 0.0D, (double)(0.0F - par7), (double)f8, (double)maxv);
            par0Tessellator.addVertexWithUV((double)f7, 0.0D, 0.0D, (double)f8, (double)maxv);
            par0Tessellator.addVertexWithUV((double)f7, 1.0D, 0.0D, (double)f8, (double)minv);
            par0Tessellator.addVertexWithUV((double)f7, 1.0D, (double)(0.0F - par7), (double)f8, (double)minv);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(1.0F, 0.0F, 0.0F);
        float f9;

        for (k = 0; k < numX; ++k)
        {
            f7 = (float)k / numX;
            f8 = maxu + (minu - maxu) * f7 - texelU / 2;
            f9 = f7 + 1.0F / numX;
            par0Tessellator.addVertexWithUV((double)f9, 1.0D, (double)(0.0F - par7), (double)f8, (double)minv);
            par0Tessellator.addVertexWithUV((double)f9, 1.0D, 0.0D, (double)f8, (double)minv);
            par0Tessellator.addVertexWithUV((double)f9, 0.0D, 0.0D, (double)f8, (double)maxv);
            par0Tessellator.addVertexWithUV((double)f9, 0.0D, (double)(0.0F - par7), (double)f8, (double)maxv);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 1.0F, 0.0F);

        for (k = 0; k < numY; ++k)
        {
            f7 = (float)k / numY;
            f8 = maxv + (minv - maxv) * f7 - texelU / 2;
            f9 = f7 + 1.0F / numY;
            par0Tessellator.addVertexWithUV(0.0D, (double)f9, 0.0D, (double)maxu, (double)f8);
            par0Tessellator.addVertexWithUV(1.0D, (double)f9, 0.0D, (double)minu, (double)f8);
            par0Tessellator.addVertexWithUV(1.0D, (double)f9, (double)(0.0F - par7), (double)minu, (double)f8);
            par0Tessellator.addVertexWithUV(0.0D, (double)f9, (double)(0.0F - par7), (double)maxu, (double)f8);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, -1.0F, 0.0F);

        for (k = 0; k < numY; ++k)
        {
            f7 = (float)k / numY;
            f8 = maxv + (minv - maxv) * f7 - texelV / 2;
            par0Tessellator.addVertexWithUV(1.0D, (double)f7, 0.0D, (double)minu, (double)f8);
            par0Tessellator.addVertexWithUV(0.0D, (double)f7, 0.0D, (double)maxu, (double)f8);
            par0Tessellator.addVertexWithUV(0.0D, (double)f7, (double)(0.0F - par7), (double)maxu, (double)f8);
            par0Tessellator.addVertexWithUV(1.0D, (double)f7, (double)(0.0F - par7), (double)minu, (double)f8);
        }

        par0Tessellator.draw();
    }
}