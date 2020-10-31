package mods.immibis.tubestuff;


import java.util.List;

import mods.immibis.core.Config;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// TODO: move this stuff into the main mod file

public class SharedProxy {
	public static final boolean DEBUG_MODE = TubeStuff.class.getName().equals("net.minecraft.src.mod_TubeStuff");
	
	public static boolean enableBHCParticles, enableBHC, enableBHCAnim, enableCraftingIncinerator;
	public static boolean enableCraftingRetrievulator, enableStorageBlocks, enableStorageBlocksVanilla, enableStorageBlockOreDictionary;
	public static boolean enableCraftingBlockBreaker;
	
	private static Class<?> findBCClass(String name)
	{
		try
		{
			return SharedProxy.class.getClassLoader().loadClass("buildcraft."+name);
		}
		catch(ClassNotFoundException e)
		{
			return null;
		}
	}
	
	//public static IConfigReader redpowerConfig = null;
	
	public static void FirstTick() {
		Item woodenPipeID = null;
		Block engineID = null;
		Block actID = null;
		
		/*try
		{
			Class<?> bcTransport = findBCClass("BuildCraftTransport");
			if(bcTransport != null)
				woodenPipeID = ((Item)bcTransport.getDeclaredField("pipeItemsWood").get(null)).itemID;
		}
		catch(Exception e)
		{
			//System.out.println(TubeStuff.class.getSimpleName()+": BC Transport doesn't seem to be installed");
		}

		try
		{
			Class<?> bcEnergy = findBCClass("BuildCraftEnergy");
			if(bcEnergy != null)
				engineID = ((Block)bcEnergy.getDeclaredField("engineBlock").get(null)).blockID;
		}
		catch(Exception e)
		{
			//System.out.println(TubeStuff.class.getSimpleName()+": BC Energy doesn't seem to be installed");
		}
		
		try
		{
			Class<?> bcFactory = findBCClass("BuildCraftFactory");
			if(bcFactory != null)
				actID = ((Block)bcFactory.getDeclaredField("autoWorkbenchBlock").get(null)).blockID;
		}
		catch(Exception e)
		{
			//System.out.println(TubeStuff.class.getSimpleName()+": BC Factory doesn't seem to be installed");
		}*/
		
		ItemStack bufferIS = new ItemStack(TubeStuff.block, 1, 0);
		ItemStack actIS = new ItemStack(TubeStuff.block, 1, 1);
		ItemStack infChestIS = new ItemStack(TubeStuff.block, 1, 2);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(actIS,
			"GGG",
			"WCW",
			"OcO",
			'G', Items.gold_nugget,
			'C', Blocks.crafting_table,
			'c', Blocks.chest,
			'W', "plankWood",
			'O', Blocks.cobblestone
		));
		
		if(woodenPipeID != null)
		{
			if(engineID != null)
				GameRegistry.addShapelessRecipe(bufferIS, new Object[] {
					woodenPipeID,
					Blocks.chest,
					new ItemStack(engineID, 1, 0) // redstone engine
				});
			if(engineID == null)
				GameRegistry.addShapelessRecipe(bufferIS, new Object[] {
					woodenPipeID,
					Blocks.chest
				});
			
			GameRegistry.addRecipe(new ShapedOreRecipe(actIS,
				"GWG",
				"PCP",
				"OcO",
				'G', Items.gold_nugget,
				'C', actID == null ? Blocks.crafting_table : actID,
				'c', Blocks.chest,
				'W', woodenPipeID,
				'P', "plankWood",
				'O', Blocks.cobblestone
			));
		}
		
		enableBHC = Config.getBoolean("tubestuff.enableBlackHoleChest", false);
		
		if(enableBHC) {
			GameRegistry.addRecipe(infChestIS, new Object[] {
				"ODO",
				"OCO",
				"ODO",
				'O', Blocks.obsidian,
				'C', Blocks.chest,
				'D', Blocks.diamond_block
			});
		}
		
		enableBHCAnim = Config.getBoolean("tubestuff.enableBHCAnim", true);
		enableBHCParticles = Config.getBoolean("tubestuff.enableBHCParticles", true);
		
		if(enableCraftingIncinerator = Config.getBoolean("tubestuff.enableCraftingIncinerator", true))
		{
			// Incinerator
			
			GameRegistry.addRecipe(new ItemStack(TubeStuff.block, 1, 3), new Object[] {
				"CCC",
				"CLC",
				"CCC",
				'C', Blocks.cobblestone,
				'L', Items.lava_bucket
			});
		}
		
		if(enableCraftingBlockBreaker = Config.getBoolean("tubestuff.enableCraftingBlockBreaker", true)) {
			GameRegistry.addRecipe(new ItemStack(TubeStuff.block, 4, 6), new Object[] {
				"O O",
				"---",
				"OXO",
				'O', Blocks.obsidian,
				'-', Items.stick,
				'X', Blocks.sticky_piston
			});
		}
		
		if(Config.getBoolean("tubestuff.enableCraftingDeployer", true)) {
			GameRegistry.addShapedRecipe(new ItemStack(TubeStuff.block, 1, BlockTubestuff.META_DEPLOYER),
				"SCS",
				"SPS",
				"SRS",
				'S', Blocks.cobblestone,
				'C', Blocks.chest,
				'P', Blocks.piston,
				'R', Items.redstone
				);
		}
		
		if(Config.getBoolean("tubestuff.enableCraftingLiquidIncinerator", true)) {
			GameRegistry.addShapelessRecipe(new ItemStack(TubeStuff.block, 1, 7),
				new ItemStack(TubeStuff.block, 1, 3),
				Items.bucket);
		}
		
		if(Config.getBoolean("tubestuff.enableCraftingOnlinePlayerDetector", true)) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TubeStuff.block, 1, BlockTubestuff.META_ONLINE_DETECTOR),
				"/r/",
				"g g",
				"/r/",
				'/', Items.gold_ingot,
				'g', "dyeGreen",
				'r', "dyeRed"
			));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TubeStuff.block, 1, BlockTubestuff.META_ONLINE_DETECTOR),
				"/g/",
				"r r",
				"/g/",
				'/', Items.gold_ingot,
				'g', "dyeGreen",
				'r', "dyeRed"
			));
		}
		
		
		
		if(enableStorageBlocks) {
			addStorageRecipes(0, null, enableStorageBlocks, "ingotSilver");
			addStorageRecipes(1, null, enableStorageBlocks, "ingotTin");
			addStorageRecipes(2, null, enableStorageBlocks, "ingotCopper");
			addStorageRecipes(3, null, enableStorageBlocks, null);
			addStorageRecipes(6, null, enableStorageBlocks, null);
			addStorageRecipes(7, null, enableStorageBlocks, null);
			addStorageRecipes(8, null, enableStorageBlocks, "ingotBrass");
			addStorageRecipes(9, new ItemStack(Items.coal, 1, 1), enableStorageBlocksVanilla, null);
			
			if(enableStorageBlocksVanilla) {
				// Redstone block
				GameRegistry.addShapelessRecipe(new ItemStack(TubeStuff.blockStorage, 1, 5), Blocks.redstone_block, Blocks.sand);
				GameRegistry.addRecipe(new ItemStack(Blocks.redstone_block), "#", '#', new ItemStack(TubeStuff.blockStorage, 1, 5));
				
				// Coal block
				GameRegistry.addShapelessRecipe(new ItemStack(TubeStuff.blockStorage, 1, 4), Blocks.coal_block, Blocks.stonebrick);
				GameRegistry.addRecipe(new ItemStack(Blocks.coal_block), "#", '#', new ItemStack(TubeStuff.blockStorage, 1, 4));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void addStorageRecipes(int block_meta, ItemStack item, boolean enable, String oreName) {
		
		if(enable) {
			if(item != null)
				GameRegistry.addRecipe(new ItemStack(TubeStuff.blockStorage, 1, block_meta), "###", "###", "###", '#', item);
			if(enableStorageBlockOreDictionary && oreName != null)
				CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(TubeStuff.blockStorage, 1, block_meta), "###", "###", "###", '#', oreName));
		}
		
		// decompressing recipe is always enabled
		ItemStack result;
		if(item != null)
			result = item.copy();
		else if(enableStorageBlockOreDictionary && oreName != null) {
			List<ItemStack> ores = net.minecraftforge.oredict.OreDictionary.getOres(oreName);
			if(ores.isEmpty())
				result = null;
			else
				result = ores.get(0).copy();
		} else
			result = null;
		
		if(result != null) {
			result.stackSize = 9;
			GameRegistry.addRecipe(result, "#", '#', new ItemStack(TubeStuff.blockStorage, 1, block_meta));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean enableBHCAnim() {
		GameSettings gs = Minecraft.getMinecraft().gameSettings;
		return enableBHCAnim && !gs.anaglyph && gs.fancyGraphics;
	}
}
