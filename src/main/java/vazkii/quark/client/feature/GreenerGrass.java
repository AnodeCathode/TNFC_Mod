/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [09/04/2016, 22:38:05 (GMT)]
 */
package vazkii.quark.client.feature;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;
import vazkii.quark.base.module.Feature;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GreenerGrass extends Feature {

	public static boolean affectFoliage;
	public static boolean alphaGrass;
	public static boolean absoluteValues;
	public static int redShift, greenShift, blueShift;

	public static List<String> extraBlocks;

	@Override
	public void setupConfig() {
		affectFoliage = loadPropBool("Should affect foliage", "", true);
		alphaGrass = loadPropBool("Alpha grass", "Sets the grass color to be a \"Minecraft Alpha\" tone.\nThis will override all manual shift values.", false);
		absoluteValues = loadPropBool("Treat shifts as absolute and ignore biome colors", "", false);
		redShift = loadPropInt("Shift reds by", "", -30);
		greenShift = loadPropInt("Shift greens by", "", 30);
		blueShift = loadPropInt("Shift blues by", "", -30);

		extraBlocks = Arrays.asList(loadPropStringList("Extra blocks", "", new String[] {
				"buildingbrickscompatvanilla:grass_slab",
				"buildingbrickscompatvanilla:grass_step",
				"buildingbrickscompatvanilla:grass_corner",
				"buildingbrickscompatvanilla:grass_vertical_slab",
				"buildingbrickscompatvanilla:grass_stairs",
				"betterwithmods:dirt_slab",
				"biomesoplenty:plant_0",
				"biomesoplenty:plant_1",
				"biomesoplenty:leaves_1",
				"biomesoplenty:leaves_2",
				"biomesoplenty:leaves_3",
				"biomesoplenty:leaves_4",
				"biomesoplenty:leaves_5",
				"biomesoplenty:grass"
		}));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initClient() {
		registerGreenerColor(Blocks.GRASS, Blocks.TALLGRASS, Blocks.DOUBLE_PLANT, Blocks.REEDS);
		if(affectFoliage)
			registerGreenerColor(Blocks.LEAVES, Blocks.LEAVES2, Blocks.VINE);

		for(String s : extraBlocks) {
			Block b = Block.REGISTRY.getObject(new ResourceLocation(s));
			registerGreenerColor(b);
		}	
	}

	@SideOnly(Side.CLIENT)
	private void registerGreenerColor(Block... blocks) {
		BlockColors colors = Minecraft.getMinecraft().getBlockColors();
		Map<IRegistryDelegate<Block>, IBlockColor> map = ObfuscationReflectionHelper.getPrivateValue(BlockColors.class, colors, "blockColorMap"); // This is a forge field so obfuscation is meaningless

		for(Block b : blocks) {
			IBlockColor color = map.get(b.delegate);
			if(color != null)
				colors.registerBlockColorHandler(getGreenerColor(color), b);
		}
	}

	@SideOnly(Side.CLIENT)
	private IBlockColor getGreenerColor(IBlockColor color) {
		return (state, world, pos, tintIndex) -> {
			int originalColor = color.colorMultiplier(state, world, pos, tintIndex);

			int r = originalColor >> 16 & 0xFF;
			int g = originalColor >> 8 & 0xFF;
			int b = originalColor & 0xFF;

			int shiftRed = alphaGrass ? 30 : redShift;
			int shiftGreen = alphaGrass ? 120 : greenShift;
			int shiftBlue = alphaGrass ? 30 : blueShift;

			if(absoluteValues)
				return (Math.max(0, Math.min(0xFF, redShift)) << 16) + Math.max(0, Math.min(0xFF, greenShift) << 8) + Math.max(0, Math.min(0xFF, blueShift));
			return (Math.max(0, Math.min(0xFF, r + shiftRed)) << 16) + Math.max(0, Math.min(0xFF, g + shiftGreen) << 8) + Math.max(0, Math.min(0xFF, b + shiftBlue));
		};
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "sereneseasons" };
	}

}
