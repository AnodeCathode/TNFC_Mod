package vazkii.quark.experimental.lighting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IColoredLightSource {

	float[][] VANILLA_SPECTRUM_COLORS = new float[][] {
		{ 1.0F, 1.0F, 1.0F }, // White
		{ 1.0F, 0.5F, 0.0F }, // Orange
		{ 1.0F, 0.0F, 1.0F }, // Magenta
		{ 0.0F, 0.5F, 1.0F }, // Light Blue
		{ 1.0F, 1.0F, 0.0F }, // Yellow
		{ 0.5F, 1.0F, 0.0F }, // Lime
		{ 1.0F, 0.0F, 0.5F }, // Pink
		{ 1F, 1F, 1F }, // Gray
		{ 1F, 1F, 1F }, // Light Gray
		{ 0.0F, 1.0F, 1.0F }, // Cyan
		{ 0.5F, 0.0F, 1.0F }, // Purple
		{ 0.0F, 0.0F, 1.0F }, // Blue
		{ 0.5F, 0.25F, 0.0F }, // Brown
		{ 0.0F, 1.0F, 0.0F }, // Green
		{ 1.0F, 0.0F, 0.0F }, // Red
		{ 1F, 1F, 1F } // Black
	};
	
	float[] getColoredLight(IBlockAccess world, BlockPos pos);
	
}
