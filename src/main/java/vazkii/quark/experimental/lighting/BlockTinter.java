package vazkii.quark.experimental.lighting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.experimental.features.ColoredLights;

public class BlockTinter {
	
	private static DynamicTexture lightmapTex;

	public static void tintBlockFlat(IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder buffer, BakedQuad quad, int lightColor) {
		if((lightColor & 0xF0) == 0)
			return;
		
		float[] colors = ColoredLightSystem.getLightColor(world, pos.offset(quad.getFace()));
		
		if(ColoredLights.cullToLightmap)
			colors = cullColorsToLightmap(colors, lightColor);
		
		if(colors.length > 0) {
			float[] quadTint = tintQuad(quad, state, world, pos);
			if(quadTint != null)
				for(int i = 0; i < 3; i++)
					colors[i] *= quadTint[i];
			
			for(int i = 1; i < 5; i++)
				buffer.putColorMultiplier(colors[0], colors[1], colors[2], i);
		}
	}
	
	// Copied from vanilla BlockModelRenderer
	private static float[] tintQuad(BakedQuad bakedquad, IBlockState stateIn, IBlockAccess blockAccessIn, BlockPos posIn) {
		if(bakedquad.hasTintIndex()) {
			int k = Minecraft.getMinecraft().getBlockColors().colorMultiplier(stateIn, blockAccessIn, posIn, bakedquad.getTintIndex());

			if(EntityRenderer.anaglyphEnable)
				k = TextureUtil.anaglyphColor(k);

			float f = (k >> 16 & 255) / 255.0F;
			float f1 = (k >> 8 & 255) / 255.0F;
			float f2 = (k & 255) / 255.0F;
			if(bakedquad.shouldApplyDiffuseLighting()) {
				float diffuse = LightUtil.diffuseLight(bakedquad.getFace());
				f *= diffuse;
				f1 *= diffuse;
				f2 *= diffuse;
			}

			return new float[] { f, f1, f2 };
		}
		else if(bakedquad.shouldApplyDiffuseLighting()) {
			float diffuse = LightUtil.diffuseLight(bakedquad.getFace());
			return new float[] { diffuse, diffuse, diffuse };
		}
		
		return null;
	}
	
	private static float[] cullColorsToLightmap(float[] colors, int lightmapPos) {
		if(colors.length < 3)
			return colors;
		
		int sunlight = (lightmapPos & 0xF00000) >> 20;
		
		int index = sunlight * 16;
		int[] lightmap = getLightmapColors();
		int lightmapColor = lightmap[index];

		float r = (float) ((lightmapColor >> 16) & 0xFF) / 0xFF; 
		float g = (float) ((lightmapColor >> 8) & 0xFF)  / 0xFF;
		float b = (float) ((lightmapColor) & 0xFF)  / 0xFF;
		
		return new float[] { Math.max(r, colors[0]), Math.max(g, colors[1]), Math.max(b, colors[2]) };
	}
	
	private static int[] getLightmapColors() {
		if(lightmapTex == null) 
			lightmapTex = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, LibObfuscation.LIGHTMAP_TEXTURE);
		
		return lightmapTex.getTextureData();
	}
	
}
