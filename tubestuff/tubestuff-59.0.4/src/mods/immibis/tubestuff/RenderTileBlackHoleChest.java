package mods.immibis.tubestuff;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.util.Random;

import mods.immibis.core.RenderUtilsIC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileBlackHoleChest extends TileEntitySpecialRenderer {
	
	private int centreDL = -1, backgroundDL = -1, setupDL = -1;
	
	private void renderUnitBox(float x, float y, float z) {
		glBegin(GL_QUADS);
		glVertex3f(x, y, z);
		glVertex3f(x, y+1, z);
		glVertex3f(x+1, y+1, z);
		glVertex3f(x+1, y, z);
		
		glVertex3f(x, y, z+1);
		glVertex3f(x+1, y, z+1);
		glVertex3f(x+1, y+1, z+1);
		glVertex3f(x, y+1, z+1);
		
		glVertex3f(x+1, y, z);
		glVertex3f(x+1, y+1, z);
		glVertex3f(x+1, y+1, z+1);
		glVertex3f(x+1, y, z+1);
		
		glVertex3f(x, y, z);
		glVertex3f(x, y, z+1);
		glVertex3f(x, y+1, z+1);
		glVertex3f(x, y+1, z);
		
		glVertex3f(x+1, y, z);
		glVertex3f(x+1, y, z+1);
		glVertex3f(x, y, z+1);
		glVertex3f(x, y, z);
		
		glVertex3f(x, y+1, z+1);
		glVertex3f(x+1, y+1, z+1);
		glVertex3f(x+1, y+1, z);
		glVertex3f(x, y+1, z);
		glEnd();
	}
	
	private void renderUnitBoxWithUV(float x, float y, float z, Tessellator t, float umin, float vmin, float umax, float vmax) {
		t.addVertexWithUV(x, y, z, umin, vmin);
		t.addVertexWithUV(x, y+1, z, umin, vmax);
		t.addVertexWithUV(x+1, y+1, z, umax, vmax);
		t.addVertexWithUV(x+1, y, z, umax, vmin);
		
		t.addVertexWithUV(x, y, z+1, umin, vmin);
		t.addVertexWithUV(x+1, y, z+1, umin, vmax);
		t.addVertexWithUV(x+1, y+1, z+1, umax, vmax);
		t.addVertexWithUV(x, y+1, z+1, umax, vmin);
		
		t.addVertexWithUV(x+1, y, z, umin, vmin);
		t.addVertexWithUV(x+1, y+1, z, umin, vmax);
		t.addVertexWithUV(x+1, y+1, z+1, umax, vmax);
		t.addVertexWithUV(x+1, y, z+1, umax, vmin);
		
		t.addVertexWithUV(x, y, z, umin, vmin);
		t.addVertexWithUV(x, y, z+1, umin, vmax);
		t.addVertexWithUV(x, y+1, z+1, umax, vmax);
		t.addVertexWithUV(x, y+1, z, umax, vmin);
		
		t.addVertexWithUV(x+1, y, z, umin, vmin);
		t.addVertexWithUV(x+1, y, z+1, umin, vmax);
		t.addVertexWithUV(x, y, z+1, umax, vmax);
		t.addVertexWithUV(x, y, z, umax, vmin);
		
		t.addVertexWithUV(x, y+1, z+1, umin, vmin);
		t.addVertexWithUV(x+1, y+1, z+1, umin, vmax);
		t.addVertexWithUV(x+1, y+1, z, umax, vmax);
		t.addVertexWithUV(x, y+1, z, umax, vmin);
	}
	
	private FloatBuffer func_76907_a(float par1, float par2, float par3, float par4)
    {
        this.field_76908_a.clear();
        this.field_76908_a.put(par1).put(par2).put(par3).put(par4);
        this.field_76908_a.flip();
        return this.field_76908_a;
    }
	
	FloatBuffer field_76908_a = GLAllocation.createDirectFloatBuffer(16);
	
	private static final ResourceLocation RES_SKY = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation RES_PORTAL = new ResourceLocation("textures/entity/end_portal.png");
	
	public void renderTileEntityAt_endportal(TileEntity te, double par2, double par4, double par6, float f) {
		float var9 = (float)this.field_147501_a.field_147560_j;
        float var10 = (float)this.field_147501_a.field_147561_k;
        float var11 = (float)this.field_147501_a.field_147558_l;
        GL11.glDisable(GL11.GL_LIGHTING);
        Random var12 = new Random(31100L);
        float var13 = 0.75F;
        
        for (int var14 = 0; var14 < 16; ++var14)
	    {
	        GL11.glPushMatrix();
	        float var15 = (float)(16 - var14);
	        float var16 = 0.0625F;
	        float var17 = 1.0F / (var15 + 1.0F);

	        if (var14 == 0)
	        {
	            this.bindTexture(RES_SKY);
	            var17 = 0.1F;
	            var15 = 65.0F;
	            var16 = 0.125F;
	            GL11.glEnable(GL11.GL_BLEND);
	            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        }

	        if (var14 == 1)
	        {
	            this.bindTexture(RES_PORTAL);
	            GL11.glEnable(GL11.GL_BLEND);
	            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
	            var16 = 0.5F;
	        }

	        float var18 = (float)(-(par4 + (double)var13));
	        float var19 = var18 + ActiveRenderInfo.objectY;
	        float var20 = var18 + var15 + ActiveRenderInfo.objectY;
	        float var21 = var19 / var20;
	        var21 += (float)(par4 + (double)var13);
	        GL11.glTranslatef(var9, var21, var11);
	        GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
	        GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
	        GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
	        GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
	        GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.func_76907_a(1.0F, 0.0F, 0.0F, 0.0F));
	        GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.func_76907_a(0.0F, 0.0F, 1.0F, 0.0F));
	        GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.func_76907_a(0.0F, 0.0F, 0.0F, 1.0F));
	        GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, this.func_76907_a(0.0F, 1.0F, 0.0F, 0.0F));
	        GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
	        GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
	        GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
	        GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
	        GL11.glPopMatrix();
	        GL11.glMatrixMode(GL11.GL_TEXTURE);
	        GL11.glPushMatrix();
	        GL11.glLoadIdentity();
	        GL11.glTranslatef(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
	        GL11.glScalef(var16, var16, var16);
	        GL11.glTranslatef(0.5F, 0.5F, 0.0F);
	        GL11.glRotatef((float)(var14 * var14 * 4321 + var14 * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
	        GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
	        GL11.glTranslatef(-var9, -var11, -var10);
	        var19 = var18 + ActiveRenderInfo.objectY;
	        GL11.glTranslatef(ActiveRenderInfo.objectX * var15 / var19, ActiveRenderInfo.objectZ * var15 / var19, -var10);
	        Tessellator var24 = Tessellator.instance;
	        var24.startDrawingQuads();
	        var21 = var12.nextFloat() * 0.5F + 0.1F;
	        float var22 = var12.nextFloat() * 0.5F + 0.4F;
	        float var23 = var12.nextFloat() * 0.5F + 0.5F;

	        if (var14 == 0)
	        {
	            var23 = 1.0F;
	            var22 = 1.0F;
	            var21 = 1.0F;
	        }

	        var24.setColorRGBA_F(var21 * var17, var22 * var17, var23 * var17, 1.0F);
	        
	        final double D = 0.99;
	        par2 += 0.5-D/2;
	        par4 += 0.5-D/2;
	        par6 += 0.5-D/2;
	        
	        // +Y
	        var24.addVertex(par2, par4 + D, par6);
	        var24.addVertex(par2, par4 + D, par6 + D);
	        var24.addVertex(par2 + D, par4 + D, par6 + D);
	        var24.addVertex(par2 + D, par4 + D, par6);
	        
	        // -Y
	        var24.addVertex(par2 + D, par4, par6);
	        var24.addVertex(par2 + D, par4, par6 + D);
	        var24.addVertex(par2, par4, par6 + D);
	        var24.addVertex(par2, par4, par6);
	        
	        // +X
	        var24.addVertex(par2 + D, par4 + D, par6);
	        var24.addVertex(par2 + D, par4 + D, par6 + D);
	        var24.addVertex(par2 + D, par4, par6 + D);
	        var24.addVertex(par2 + D, par4, par6);
	        
	        // -X
	        var24.addVertex(par2, par4, par6);
	        var24.addVertex(par2, par4, par6 + D);
	        var24.addVertex(par2, par4 + D, par6 + D);
	        var24.addVertex(par2, par4 + D, par6);
	        
	        // +Z
	        var24.addVertex(par2 + D, par4, par6 + D);
	        var24.addVertex(par2 + D, par4 + D, par6 + D);
	        var24.addVertex(par2, par4 + D, par6 + D);
	        var24.addVertex(par2, par4, par6 + D);
	        
	        // -Z
	        var24.addVertex(par2, par4, par6);
	        var24.addVertex(par2, par4 + D, par6);
	        var24.addVertex(par2 + D, par4 + D, par6);
	        var24.addVertex(par2 + D, par4, par6);
	        
	        par2 -= 0.5-D/2;
	        par4 -= 0.5-D/2;
	        par6 -= 0.5-D/2;
	        
	        var24.draw();
	        GL11.glPopMatrix();
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    }
		
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
        GL11.glEnable(GL11.GL_LIGHTING);
        
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        RenderUtilsIC.setBrightness(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
		
        this.bindTexture(R.bhc_world);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
		t.setColorRGBA(255, 255, 255, 255);
		renderUnitBoxWithUV((float)par2, (float)par4, (float)par6, t, 0, 0, 1/16.0f, 1/16.0f);
		t.draw();
		
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		if(!SharedProxy.enableBHCAnim())
			return;
		
		double playerDistSq = x*x + y*y + z*z;
		
		if(playerDistSq > 32*32)
		{
			// Just render a black box if the player is too far away.
			glColor4f(0, 0, 0, 1);
			renderUnitBox((float)x, (float)y, (float)z);
			glColor4f(1, 1, 1, 1);
			return;
		}
		
		Tessellator t = Tessellator.instance;
		this.bindTexture(R.bhc_world);
		
		glPushMatrix();
		glTranslatef((float)x, (float)y, (float)z);
		//if(setupDL == -1) {
		//	setupDL = glGenLists(1);
		//	glNewList(setupDL, GL_COMPILE);
		
			// Set the stencil buffer to 1 where the tile is, and 0 everywhere else
			// Also render the tile in black without editing the depth buffer
			glClearStencil(0);
			glEnable(GL_STENCIL_TEST);
			glEnable(GL_DEPTH_TEST);
			glClear(GL_STENCIL_BUFFER_BIT);
			glStencilFunc(GL_ALWAYS, 1, 255);
			glStencilOp(GL_REPLACE, GL_KEEP, GL_REPLACE);
			glColorMask(true, true, true, true);
			glDepthMask(false);
			glDisable(GL_TEXTURE_2D);
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			glDisable(GL_TEXTURE_2D);
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
			glDisable(GL_LIGHTING);
			glColor4f(0, 0, 0, 1);
			//renderUnitBox(0, 0, 0);
			glEnable(GL_TEXTURE_2D);
			
			glPopMatrix();
			
			// Draw in the colour and depth buffers, but only
			// where the stencil buffer is 1
			glColorMask(true, true, true, true);
			glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
			glStencilFunc(GL_NOTEQUAL, 0, 255);
			
			// Draw a black plane in an orthographic projection with depth testing
			// disabled, to clear the depth buffer in the stencilled area
			glDisable(GL_DEPTH_TEST);
			glPushMatrix();
			glLoadIdentity();
			glMatrixMode(GL_PROJECTION);
			glPushMatrix();
			glLoadIdentity();
			glOrtho(-1, 1, -1, 1, -1, 1);
			glColor3f(0, 0, 0);
			glBegin(GL_QUADS);
			glVertex2f( 1,-1);
			glVertex2f( 1, 1);
			glVertex2f(-1, 1);
			glVertex2f(-1,-1);
			glEnd();
			glColor3f(1, 1, 1);
			glPopMatrix();
			glMatrixMode(GL_MODELVIEW);
			glPopMatrix();
			glEnable(GL_DEPTH_TEST);
			
		//	glEndList();
		//}
		//glCallList(setupDL);
		
		int time = (int)(System.currentTimeMillis() % 86400000);
		float angle1 = (time / 10) % 360;
		float angle2 = (time / 30) % 360;
		float angle3 = angle2 + (float)Math.sin(angle1 * 3.14159 / 180.0) * 30;
		float tx = (time % 10000) / 10000.0f;
		float tz = (1 + (float)Math.cos(angle2 * 3.14159 / 180.0)) % 1.0f;
		
		glPushMatrix();
		glTranslated(x+0.5, y+0.5, z+0.5);
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		if(playerDistSq < 16*16)
		{
			// Draw the background of "stars"/dust particles
			glPushMatrix();
			glScalef(2.0f, 2.0f, 2.0f);
			glRotatef(angle1, 1, 0, 0);
			glRotatef(angle2, 0, 1, 0);
			glRotatef(angle3, 0, 0, 1);
			if(backgroundDL == -1) {
				backgroundDL = glGenLists(1);
				glNewList(backgroundDL, GL_COMPILE);
			
				glTranslatef(-1, -1, -1);
				glTranslatef(tx, 0, tz);
				t.startDrawingQuads();
				t.setColorRGBA(64, 64, 64, 255);
				for(int xo = -2; xo <= 2; xo++)
					for(int yo = -2; yo <= 2; yo++)
						for(int zo = -2; zo <= 2; zo++)
							renderUnitBoxWithUV(xo, yo, zo, t, 0.00f, 0.75f, 0.25f, 1.00f);
				t.draw();
				
				glEndList();
			}
			glCallList(backgroundDL);
			glPopMatrix();
		}
		if(playerDistSq < 24*24)	
		{
			glPushMatrix();
			// Draw the accretion disc
			glRotatef(angle1, 0, 1, 0);
			glBegin(GL_QUADS);
			glColor3f(0.25f, 0.25f, 1.25f); glTexCoord2f(0.5f, 0.0f); glVertex3f(-1.0f, 0.0f, -1.0f);
			glColor3f(0.21f, 0.21f, 0.21f); glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f, 0.0f, -1.0f);
			glColor3f(0.18f, 0.18f, 0.18f); glTexCoord2f(1.0f, 0.5f); glVertex3f( 1.0f, 0.0f,  1.0f);
			glColor3f(0.21f, 0.21f, 0.21f); glTexCoord2f(0.5f, 0.5f); glVertex3f(-1.0f, 0.0f,  1.0f);
			glEnd();
			glPopMatrix();
		
			if(centreDL == -1) {
				centreDL = glGenLists(1);
				glNewList(centreDL, GL_COMPILE);
				
				// Draw the centre
				glPushMatrix();
				glScalef(0.2f, 0.2f, 0.2f);
				glColor3f(0.0f, 0.0f, 0.0f);
				glBegin(GL_QUADS);
				
				final float layersep = 0.325f;
				
				for(float dy = -1.0f + layersep/2; dy < 1.0f; dy += layersep) {
					float dh = (float)Math.sqrt((1 - dy*dy) * 2);
					
					glTexCoord2f(0.5f, 0.5f); glVertex3f(-dh, dy, -dh);
					glTexCoord2f(1.0f, 0.5f); glVertex3f( dh, dy, -dh);
					glTexCoord2f(1.0f, 1.0f); glVertex3f( dh, dy,  dh);
					glTexCoord2f(0.5f, 1.0f); glVertex3f(-dh, dy,  dh);
					
					glTexCoord2f(0.5f, 0.5f); glVertex3f(dy, -dh, -dh);
					glTexCoord2f(1.0f, 0.5f); glVertex3f(dy,  dh, -dh);
					glTexCoord2f(1.0f, 1.0f); glVertex3f(dy,  dh,  dh);
					glTexCoord2f(0.5f, 1.0f); glVertex3f(dy, -dh,  dh);
					
					glTexCoord2f(0.5f, 0.5f); glVertex3f(-dh, -dh, dy);
					glTexCoord2f(1.0f, 0.5f); glVertex3f( dh, -dh, dy);
					glTexCoord2f(1.0f, 1.0f); glVertex3f( dh,  dh, dy);
					glTexCoord2f(0.5f, 1.0f); glVertex3f(-dh,  dh, dy);
				}
				glEnd();
				glPopMatrix();
				
				glEndList();
			}
			
			glCallList(centreDL);
		}
		glEnable(GL_ALPHA_TEST);
		glEnable(GL_CULL_FACE);
		glPopMatrix();
		
		glDisable(GL_STENCIL_TEST);
		glDepthMask(true);
		
		// draw the frame around the chest, and also fill the depth buffer
		// with the right depth for the chest
		t.startDrawingQuads();
		t.setColorRGBA(255, 255, 255, 255);
		renderUnitBoxWithUV((float)x, (float)y, (float)z, t, 0, 0, 1/16.0f, 1/16.0f);
		t.draw();
		
		glDisable(GL_BLEND);
	}

}