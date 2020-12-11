package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.module.Feature;
import vazkii.quark.management.feature.RightClickAddToShulkerBox;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlastproofShulkerBoxes extends Feature {

	public static List<ResourceLocation> shulkerBoxes;
	public static boolean dropoffAnyShulkerBox;

	@Override
	public void setupConfig() {
		String[] shulkerArr = loadPropStringList("Shulker Boxes",
				"Blocks which should be interpreted as Shulker Boxes.",
				RightClickAddToShulkerBox.getBasicShulkerBoxes());
		shulkerBoxes = Arrays.stream(shulkerArr).map(ResourceLocation::new).collect(Collectors.toList());

		dropoffAnyShulkerBox = loadPropBool("Dropoff to Any Shulker Box",
				"Allow anything with 'shulker_box' in its item identifier to be treated as a shulker box?", true);
	}

	@Override
	public void init() {
		for (Block block : Block.REGISTRY) {
			if (RightClickAddToShulkerBox.isShulkerBox(block, shulkerBoxes, dropoffAnyShulkerBox))
				block.setResistance(2000);
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
