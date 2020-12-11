/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 14, 2019, 12:16 AM (EST)]
 */
package vazkii.quark.experimental.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

public class TextureDump extends Feature {
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void postTextureStitch(TextureStitchEvent.Post e) {
		String basePath = ObfuscationReflectionHelper.getPrivateValue(TextureMap.class, e.getMap(), "field_94254_c");
		int mipMapLevel = ObfuscationReflectionHelper.getPrivateValue(TextureMap.class, e.getMap(), "field_147636_j");

		saveGlTexture(basePath, e.getMap().getGlTextureId(), mipMapLevel);
	}

	@SideOnly(Side.CLIENT)
	private void saveGlTexture(String name, int textureId, int mipmapLevels) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		File instanceDirectory = Minecraft.getMinecraft().gameDir;

		for (int level = 0; level < mipmapLevels; level++) {
			int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_WIDTH);
			int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_HEIGHT);
			int size = width * height;

			BufferedImage image = new BufferedImage(width, height, 2);
			IntBuffer buffer = BufferUtils.createIntBuffer(size);
			int[] data = new int[size];

			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
			buffer.get(data);
			image.setRGB(0, 0, width, height, data, 0, width);

			try {
				File output = new File(instanceDirectory, name + "_" + level + ".png").getCanonicalFile();
				ImageIO.write(image, "png", output);
				Quark.LOG.info("[TextureDump] Exported atlas to: " + output.getAbsolutePath());
			} catch (IOException e) {
				Quark.LOG.info("[TextureDump] Unable to write: ", e);
			}

		}
	}
}
