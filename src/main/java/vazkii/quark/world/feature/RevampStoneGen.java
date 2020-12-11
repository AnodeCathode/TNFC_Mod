package vazkii.quark.world.feature;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockJasper;
import vazkii.quark.world.block.BlockLimestone;
import vazkii.quark.world.block.BlockMarble;
import vazkii.quark.world.block.BlockSlate;
import vazkii.quark.world.block.slab.BlockBasicStoneSlab;
import vazkii.quark.world.world.StoneInfoBasedGenerator;

public class RevampStoneGen extends Feature {

	public static BlockMod marble;
	public static BlockMod limestone;
	public static BlockMod jasper;
	public static BlockMod slate;

	public static boolean enableStairsAndSlabs;
	public static boolean enableWalls;
	public static boolean outputCSV;
	
	public static boolean generateBasedOnBiomes;
	public static boolean enableMarble;
	public static boolean enableLimestone;
	public static boolean enableJasper;
	public static boolean enableSlate;

	public static StoneInfo graniteInfo, dioriteInfo, andesiteInfo, marbleInfo, limestoneInfo, jasperInfo, slateInfo;
	private static List<StoneInfoBasedGenerator> generators;
	
	private Queue<Runnable> deferedInit = new ArrayDeque<>();

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
		enableMarble = loadPropBool("Enable Marble", "", true);
		enableLimestone = loadPropBool("Enable Limestone", "", true);
		enableJasper = loadPropBool("Enable Jasper", "", true);
		enableSlate = loadPropBool("Enable Slate", "", true);
		generateBasedOnBiomes = loadPropBool("Generate Based on Biomes", "Note: The stone rarity values are tuned based on this being true. If you turn it off, also change the stones' rarity (around 50 is fine).", true);
		outputCSV = loadPropBool("Output CSV Debug Info", "If this is true, CSV debug info will be printed out to the console on init, to help test biome spreads.", false);

		int defSize = 14;
		int defRarity = 9;
		int defUpper = 80;
		int defLower = 20;

		graniteInfo = loadStoneInfo("granite", defSize, defRarity, defUpper, defLower, true, Type.MOUNTAIN, Type.HILLS);
		dioriteInfo = loadStoneInfo("diorite", defSize, defRarity, defUpper, defLower, true, Type.SAVANNA, Type.JUNGLE, Type.MUSHROOM);
		andesiteInfo = loadStoneInfo("andesite", defSize, defRarity, defUpper, defLower, true, Type.FOREST);
		marbleInfo = loadStoneInfo("marble", defSize, defRarity, defUpper, defLower, enableMarble, Type.PLAINS);
		limestoneInfo = loadStoneInfo("limestone", defSize, defRarity, defUpper, defLower, enableLimestone, Type.SWAMP, Type.OCEAN);
		jasperInfo = loadStoneInfo("jasper", defSize, defRarity, defUpper, defLower, enableJasper, Type.MESA, Type.SANDY);
		slateInfo = loadStoneInfo("slate", defSize, defRarity, defUpper, defLower, enableSlate, Type.COLD);
	}
	
	public StoneInfo loadStoneInfo(String name, int clusterSize, int clusterRarity, int upperBound, int lowerBound, boolean enabled, BiomeDictionary.Type... biomes) {
		return loadStoneInfo(configCategory, name, clusterSize, clusterRarity, upperBound, lowerBound, enabled, "0", biomes);
	}

	public static StoneInfo loadStoneInfo(String configCategory, String name, int clusterSize, int clusterRarity, int upperBound, int lowerBound, boolean enabled, String dims, BiomeDictionary.Type... biomes) {
		String category = configCategory + "." + name;
		return new StoneInfo(category, clusterSize, clusterRarity, upperBound, lowerBound, enabled, dims, biomes);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		generators = new ArrayList<>();
		
		IBlockState graniteState = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE);
		IBlockState dioriteState = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);
		IBlockState andesiteState = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE);
		
		generators.add(new StoneInfoBasedGenerator(() -> graniteInfo, graniteState, "granite"));
		generators.add(new StoneInfoBasedGenerator(() -> dioriteInfo, dioriteState, "diorite"));
		generators.add(new StoneInfoBasedGenerator(() -> andesiteInfo, andesiteState, "andesite"));
		
		marble = makeStone(BlockMarble.class, "marble", marbleInfo, enableMarble);
		limestone = makeStone(BlockLimestone.class, "limestone", limestoneInfo, enableLimestone);
		jasper = makeStone(BlockJasper.class, "jasper", jasperInfo, enableJasper);
		slate = makeStone(BlockSlate.class, "slate", slateInfo, enableSlate);
		
		if(outputCSV)
			BiomeTypeConfigHandler.debugStoneGeneration(generators);
	}
	
	private BlockMod makeStone(Class<? extends BlockMod> clazz, String name, StoneInfo info, boolean enable) {
		if(!enable)
			return null;
		
		try {
			BlockMod block = clazz.newInstance();
			
			if(enableStairsAndSlabs) {
				BlockBasicStoneSlab.initSlab(block, 0, "stone_" + name + "_slab");
				BlockModStairs.initStairs(block, 0, new BlockQuarkStairs("stone_" + name + "_stairs", block.getDefaultState()));
			}

			VanillaWalls.add(name, block, 0, enableWalls);

			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(block, 4, 1),
					"BB", "BB",
					'B', ProxyRegistry.newStack(block, 1, 0));
			
			generators.add(new StoneInfoBasedGenerator(() -> info, block.getDefaultState(), name));
			
			String capName = WordUtils.capitalize(name);
			addOreDict("stone" + capName, ProxyRegistry.newStack(block, 1, 0));
			addOreDict("stone" + capName + "Polished", ProxyRegistry.newStack(block, 1, 1));
			
			deferedInit.add(() -> {
				ModIntegrationHandler.registerChiselVariant(name, ProxyRegistry.newStack(block, 1, 0));
				ModIntegrationHandler.registerChiselVariant(name, ProxyRegistry.newStack(block, 1, 1));
			});
			
			return block;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init() {
		while(!deferedInit.isEmpty())
			deferedInit.poll().run();
	}

	@SubscribeEvent
	public void onOreGenerate(OreGenEvent.GenerateMinable event) {
		switch(event.getType()) {
		case GRANITE:
			if(graniteInfo.enabled)
				event.setResult(Result.DENY);
			break;
		case DIORITE:
			if(dioriteInfo.enabled)
				event.setResult(Result.DENY);
			break;
		case ANDESITE:
			if(andesiteInfo.enabled)
				event.setResult(Result.DENY);
			break;
		case DIRT:
			generateNewStones(event);
			break;
		default:
		}
	}

	private void generateNewStones(OreGenEvent.GenerateMinable event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		Chunk chunk = world.getChunk(pos);
		
		for(StoneInfoBasedGenerator gen : generators)
			gen.generate(chunk.x, chunk.z, world);
	}
	
	@Override
	public boolean hasOreGenSubscriptions() {
		return true;
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	public static class StoneInfo {

		public final boolean enabled;
		public final int clusterSize, clusterRarity, upperBound, lowerBound;
		public final boolean clustersRarityPerChunk;
		
		public final DimensionConfig dims;
		public final List<BiomeDictionary.Type> allowedBiomes;

		private StoneInfo(String category, int clusterSize, int clusterRarity, int upperBound, int lowerBound, boolean enabled, String dimStr, BiomeDictionary.Type... biomes) {
			this.enabled = ModuleLoader.config.getBoolean("Enabled", category, true, "") && enabled;
			this.clusterSize = ModuleLoader.config.getInt("Cluster Radius", category, clusterSize, 0, Integer.MAX_VALUE, "");
			this.clusterRarity = ModuleLoader.config.getInt("Cluster Rarity", category, clusterRarity, 0, Integer.MAX_VALUE, "Out of how many chunks would one of these clusters generate");
			this.upperBound = ModuleLoader.config.getInt("Y Level Max", category, upperBound, 0, 255, "");
			this.lowerBound = ModuleLoader.config.getInt("Y Level Min", category, lowerBound, 0, 255, "");
			clustersRarityPerChunk = ModuleLoader.config.getBoolean("Invert Cluster Rarity", category, false, "Setting this to true will make the 'Cluster Rarity' feature be X per chunk rather than 1 per X chunks");
			
			dims = new DimensionConfig(category, dimStr);
			allowedBiomes = BiomeTypeConfigHandler.parseBiomeTypeArrayConfig("Allowed Biome Types", category, biomes);
		}
	}

}

