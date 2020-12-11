/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 19:56:42 (GMT)]
 */
package vazkii.quark.building.feature;

import java.util.ArrayDeque;
import java.util.Queue;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockWorldStoneBricks;
import vazkii.quark.building.block.BlockWorldStoneCarved;
import vazkii.quark.building.block.slab.BlockVanillaSlab;
import vazkii.quark.building.block.stairs.BlockVanillaStairs;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;

public class WorldStoneBricks extends Feature {

	public static BlockMod world_stone_bricks;
	public static BlockMod world_stone_chiseled;

	public static final BlockModSlab[] slabs = new BlockVanillaSlab[BlockWorldStoneBricks.Variants.values().length];

	public static boolean enableStairsAndSlabs;
	public static boolean enableWalls;
	
	private Queue<Runnable> deferedInit = new ArrayDeque<>();

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void preInit(FMLPreInitializationEvent event) {
		world_stone_bricks = new BlockWorldStoneBricks();
		world_stone_chiseled = new BlockWorldStoneCarved();

		if(enableStairsAndSlabs) {
			for(BlockWorldStoneBricks.Variants variant : BlockWorldStoneBricks.Variants.values()) {
				if(!variant.isEnabled())
					continue;

				IBlockState state = world_stone_bricks.getDefaultState().withProperty(world_stone_bricks.getVariantProp(), variant);
				BlockModStairs.initStairs(world_stone_bricks, variant.ordinal(), new BlockVanillaStairs(variant.getName() + "_stairs", state));

				slabs[variant.ordinal()] = BlockVanillaSlab.initSlab(world_stone_bricks, variant.ordinal(), state, variant.getName() + "_slab");
			}
		}

		if(enableWalls) {
			for (BlockWorldStoneBricks.Variants variant : BlockWorldStoneBricks.Variants.values()) {
				if (!variant.isEnabled())
					continue;

				world_stone_bricks.getDefaultState().withProperty(world_stone_bricks.getVariantProp(), variant);
				String name = variant.getName();
				VanillaWalls.add(name, world_stone_bricks, variant.ordinal(), true);
			}
		}
	}

	@Override
	public void postPreInit() {
		for(int i = 0; i < 3; i++)
			addRecipes(BlockWorldStoneBricks.Variants.values()[i], ProxyRegistry.newStack(Blocks.STONE, 1, i * 2 + 2));

		addRecipes(BlockWorldStoneBricks.Variants.STONE_BASALT_BRICKS, ProxyRegistry.newStack(Basalt.basalt, 1, 1));
		addRecipes(BlockWorldStoneBricks.Variants.STONE_MARBLE_BRICKS, ProxyRegistry.newStack(RevampStoneGen.marble, 1, 1));
		addRecipes(BlockWorldStoneBricks.Variants.STONE_LIMESTONE_BRICKS, ProxyRegistry.newStack(RevampStoneGen.limestone, 1, 1));
		addRecipes(BlockWorldStoneBricks.Variants.STONE_JASPER_BRICKS, ProxyRegistry.newStack(RevampStoneGen.jasper, 1, 1));
		addRecipes(BlockWorldStoneBricks.Variants.STONE_SLATE_BRICKS, ProxyRegistry.newStack(RevampStoneGen.slate, 1, 1));
	}
	
	private void addRecipes(BlockWorldStoneBricks.Variants variant, ItemStack baseStack) {
		if(!variant.isEnabled())
			return;
		
		int meta = variant.ordinal();
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(world_stone_bricks, 4, meta),
				"SS", "SS",
				'S', baseStack);

		if (enableStairsAndSlabs)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(world_stone_chiseled, 1, meta),
					"S", "S",
					'S', ProxyRegistry.newStack(slabs[meta]));
		else
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(world_stone_chiseled, 8, meta),
					"SSS", "S S", "SSS",
					'S', ProxyRegistry.newStack(world_stone_bricks, 1, 3));
		
		deferedInit.add(() -> ModIntegrationHandler.registerChiselVariant(variant.blockName, new ItemStack(world_stone_bricks, 1, meta)));
	}

	@Override
	public void init() {
		while(!deferedInit.isEmpty())
			deferedInit.poll().run();
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
