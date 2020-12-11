/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 00:05:39 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.feature.RevampStoneGen;

public class ImprovedStoneToolCrafting extends Feature {

	public static final String mat = "materialStoneTool";
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		String[][] patterns = new String[][] {{"XXX", " # ", " # "}, {"X", "#", "#"}, {"XX", "X#", " #"}, {"XX", " #", " #"}, {"X", "X", "#"}};
		Item[] items = new Item[] { Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.STONE_AXE, Items.STONE_HOE, Items.STONE_SWORD };

		for(int i = 0; i < patterns.length; i++)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(items[i]),
					patterns[i][0], patterns[i][1], patterns[i][2],
					'X', mat,
					'#', ProxyRegistry.newStack(Items.STICK));
		
		addOreDict();
	}
	
	private void addOreDict() {
		addOreDict(mat, ProxyRegistry.newStack(Items.FLINT));
		addOreDict(mat, ProxyRegistry.newStack(Blocks.STONE));
		addOreDict(mat, ProxyRegistry.newStack(Blocks.STONE, 1, 1));
		addOreDict(mat, ProxyRegistry.newStack(Blocks.STONE, 1, 3));
		addOreDict(mat, ProxyRegistry.newStack(Blocks.STONE, 1, 5));
		addOreDict(mat, ProxyRegistry.newStack(Blocks.COBBLESTONE));
		
		if(RevampStoneGen.limestone != null)
			addOreDict(mat, ProxyRegistry.newStack(RevampStoneGen.limestone));
		if(RevampStoneGen.marble != null)
			addOreDict(mat, ProxyRegistry.newStack(RevampStoneGen.marble));
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Better Stone Tool Crafting";
	}
	
}
