package mods.immibis.core;
import java.awt.image.BufferedImage;

import mods.immibis.core.impl.texslice.TextureSlice;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderUtilsIC {
	public static void setBrightness(IBlockAccess w, int x, int y, int z) {
		Tessellator.instance.setBrightness(w.getLightBrightnessForSkyBlocks(x, y, z, 0));
	}
	public static void setBrightnessDirect(IBlockAccess w, int x, int y, int z) {
		int i = w.getLightBrightnessForSkyBlocks(x, y, z, 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i & 0xFFFF, i >> 16);
	}
	public static void setFullBrightness() {
		Tessellator.instance.setBrightness(0x00F000F0);
	}
	public static void renderCube(double x, double y, double z, double cx, double cy, double cz, int tex_top, int tex_bottom, int tex_side, double du, double dv) {
		Tessellator t = Tessellator.instance;
		float u, v;
		
		u = (tex_bottom & 15) / 16.0f; v = (tex_bottom / 16) / 16.0f;
		
		t.setNormal(0.0F, -1.0F, 0.0F);
        t.addVertexWithUV(x   , y, z   , u, v);
		t.addVertexWithUV(x+cx, y, z   , u+du, v);
		t.addVertexWithUV(x+cx, y, z+cz, u+du, v+dv);
		t.addVertexWithUV(x   , y, z+cz, u, v+dv);
		
		u = (tex_side & 15) / 16.0f; v = (tex_side / 16) / 16.0f;
		
		t.setNormal(0.0F, 0.0F, -1.0F);
        t.addVertexWithUV(x+cx, y+cy, z, u, v);
		t.addVertexWithUV(x+cx, y   , z, u, v+dv);
		t.addVertexWithUV(x   , y   , z, u+du, v+dv);
		t.addVertexWithUV(x   , y+cy, z, u+du, v);
		
		t.setNormal(0.0F, 0.0F, 1.0F);
        t.addVertexWithUV(x+cx, y+cy, z+cz, u, v);
		t.addVertexWithUV(x   , y+cy, z+cz, u+du, v);
		t.addVertexWithUV(x   , y   , z+cz, u+du, v+dv);
		t.addVertexWithUV(x+cx, y   , z+cz, u, v+dv);
		
		t.setNormal(-1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(x, y+cy, z   , u, v);
		t.addVertexWithUV(x, y   , z   , u, v+dv);
		t.addVertexWithUV(x, y   , z+cz, u+du, v+dv);
		t.addVertexWithUV(x, y+cy, z+cz, u+du, v);
		
		t.setNormal(1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(x+cx, y+cy, z   , u, v);
		t.addVertexWithUV(x+cx, y+cy, z+cz, u+du, v);
		t.addVertexWithUV(x+cx, y   , z+cz, u+du, v+dv);
		t.addVertexWithUV(x+cx, y   , z   , u, v+dv);
		
		u = (tex_top & 15) / 16.0f; v = (tex_top / 16) / 16.0f;
		
		t.setNormal(0.0F, 1.0F, 0.0F);
        t.addVertexWithUV(x   , y+cy, z   , u, v);
		t.addVertexWithUV(x   , y+cy, z+cz, u, v+dv);
		t.addVertexWithUV(x+cx, y+cy, z+cz, u+du, v+dv);
		t.addVertexWithUV(x+cx, y+cy, z   , u+du, v);
	}
	
	public static IIcon[] loadIconArray(IIconRegister reg, String prefix, int num) {
		IIcon[] rv = new IIcon[num];
		for(int k = 0; k < num; k++)
			rv[k] = loadIcon(reg, prefix + k);
		return rv;
	}
	
	public static IIcon loadIcon(IIconRegister reg, String name) {
		if(reg instanceof TextureMap && name.contains("!")) {
			((TextureMap) reg).setTextureEntry(name, new TextureSlice(name));
		} else if(reg instanceof TextureMap)
			((TextureMap) reg).setTextureEntry(name, new TextureNonSquare(name));
		return reg.registerIcon(name);
	}
	
	private static class TextureNonSquare extends TextureAtlasSprite {
		public TextureNonSquare(String name) {
			super(name);
		}
		
		int actualW, actualH, paddedSize;
		
		@Override
		public void initSprite(int par1, int par2, int par3, int par4, boolean par5) {
			width = actualW;
			height = actualH;
			super.initSprite(par1, par2, par3, par4, par5);
			width = paddedSize;
			height = paddedSize;
		}
		
		@Override
		public void loadSprite(BufferedImage[] p_147964_1_, AnimationMetadataSection p_147964_2_, boolean p_147964_3_)
	    {
			try {
				BufferedImage bufferedimage = p_147964_1_[0];
		        actualH = bufferedimage.getHeight();
		        actualW = bufferedimage.getWidth();
		        
		        paddedSize = Math.max(actualW, actualH);
		        width = height = paddedSize;
		        
		        int[][] aint = new int[p_147964_1_.length][];
		        for (int k = 0; k < p_147964_1_.length; ++k)
		        {
		            bufferedimage = p_147964_1_[k];

		            if (bufferedimage != null)
		            {
		                if (k > 0 && (bufferedimage.getWidth() != actualW >> k || bufferedimage.getHeight() != actualH >> k))
		                {
		                    throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", new Object[] {Integer.valueOf(k), Integer.valueOf(bufferedimage.getWidth()), Integer.valueOf(bufferedimage.getHeight()), Integer.valueOf(this.width >> k), Integer.valueOf(this.height >> k)}));
		                }

		                aint[k] = new int[width * height];
		                bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k], 0, width);
		            }
		        }
	
		        framesTextureData.add(aint);
			} catch(Exception e) { e.printStackTrace(); }
	    }
	}
	
	
}
