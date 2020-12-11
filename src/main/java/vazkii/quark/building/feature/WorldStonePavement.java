package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockWorldStonePavement;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;

public class WorldStonePavement extends Feature {

	public static Block world_stone_pavement;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		world_stone_pavement = new BlockWorldStonePavement();
	}
	
	@Override
	public void postPreInit() {
		for(int i = 0; i < 3; i++)
			addRecipe(BlockWorldStonePavement.Variants.values()[i], ProxyRegistry.newStack(Blocks.STONE, 1, i * 2 + 1));

		addRecipe(BlockWorldStonePavement.Variants.STONE_BASALT_PAVEMENT, ProxyRegistry.newStack(Basalt.basalt, 1, 0));
		addRecipe(BlockWorldStonePavement.Variants.STONE_MARBLE_PAVEMENT, ProxyRegistry.newStack(RevampStoneGen.marble, 1, 0));
		addRecipe(BlockWorldStonePavement.Variants.STONE_LIMESTONE_PAVEMENT, ProxyRegistry.newStack(RevampStoneGen.limestone, 1, 0));
		addRecipe(BlockWorldStonePavement.Variants.STONE_JASPER_PAVEMENT, ProxyRegistry.newStack(RevampStoneGen.jasper, 1, 0));
		addRecipe(BlockWorldStonePavement.Variants.STONE_SLATE_PAVEMENT, ProxyRegistry.newStack(RevampStoneGen.slate, 1, 0));
	}
	
	private void addRecipe(BlockWorldStonePavement.Variants variant, ItemStack baseStack) {
		if(!variant.isEnabled())
			return;
		
		int meta = variant.ordinal();
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(world_stone_pavement, 9, meta),
				"SSS", "SSS", "SSS",
				'S', baseStack);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
