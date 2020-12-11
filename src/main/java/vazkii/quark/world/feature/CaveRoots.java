package vazkii.quark.world.feature;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.lib.LibPotionIndices;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.block.BlockRoots;
import vazkii.quark.world.block.BlockRootsFlower;
import vazkii.quark.world.effects.PotionColorizer;
import vazkii.quark.world.item.ItemRoot;
import vazkii.quark.world.item.ItemRootDye;
import vazkii.quark.world.item.ItemRootFlower;
import vazkii.quark.world.world.CaveRootGenerator;

public class CaveRoots extends Feature {

	public static int chunkAttempts, minY, maxY;
	public static boolean enableFlowers;
	public static float flowerChance, rootDropChance, rootFlowerDropChance;
	public static DimensionConfig dimensions;
	
	public static Block roots;
	public static Block roots_blue_flower, roots_black_flower, roots_white_flower;
	public static PotionColorizer blue_effect, black_effect, white_effect;
	
	public static Item root, root_flower, root_dye;

	@Override
	public void setupConfig() {
		chunkAttempts = loadPropInt("Attempts per Chunk", "How many times the world generator will try to place roots per chunk", 300);
		minY = loadPropInt("Min Y", "", 16);
		maxY = loadPropInt("Max Y", "", 52);
		enableFlowers = loadPropBool("Enable Flowers", "", true);
		flowerChance = (float) loadPropDouble("Flower Chance", "The chance for a root to sprout a flower when it grows. 0 is 0%, 1 is 100%", 0.2);
		rootDropChance = (float) loadPropDouble("Root Drop Chance", "The chance for a root to drop the root item when broken. 0 is 0%, 1 is 100%", 0.1);
		rootFlowerDropChance = (float) loadPropDouble("Root Flower Drop Chance", "The chance for a flower root to drop the sprout item when broken. 0 is 0%, 1 is 100%", 1);
		
		dimensions = new DimensionConfig(configCategory);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		roots = new BlockRoots();
		root = new ItemRoot();

		MinecraftForge.EVENT_BUS.register(PotionColorizer.class);
		
		if(enableFlowers) {
			blue_effect = new PotionColorizer("blue", 0x0000FF, LibPotionIndices.BLUE);
			black_effect = new PotionColorizer("black", 0x303030, LibPotionIndices.BLACK);
			white_effect = new PotionColorizer("white", 0xCCCCCC, LibPotionIndices.WHITE);

			roots_blue_flower = new BlockRootsFlower("roots_blue_flower", 0);
			roots_black_flower = new BlockRootsFlower("roots_black_flower", 1);
			roots_white_flower = new BlockRootsFlower("roots_white_flower", 2);
			
			root_flower = new ItemRootFlower();
			root_dye = new ItemRootDye();
			
			for(int i = 0; i < 3; i++)
				RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(root_dye, 1, i), ProxyRegistry.newStack(root_flower, 1, i));
			
			addOreDict("dyeBlue", ProxyRegistry.newStack(root_dye, 1, 0));
			addOreDict("dyeBlack", ProxyRegistry.newStack(root_dye, 1, 1));
			addOreDict("dyeWhite", ProxyRegistry.newStack(root_dye, 1, 2));
		}
		
		GameRegistry.registerWorldGenerator(new CaveRootGenerator(), 2000);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	
}
