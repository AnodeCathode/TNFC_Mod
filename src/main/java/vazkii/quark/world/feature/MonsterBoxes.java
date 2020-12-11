package vazkii.quark.world.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.block.BlockMonsterBox;
import vazkii.quark.world.tile.TileMonsterBox;
import vazkii.quark.world.world.MonsterBoxGenerator;

public class MonsterBoxes extends Feature {

	public static Block monster_box;
	
	public static int minY, maxY, minMobs, maxMobs;
	public static float chunkChance;
	
	public static DimensionConfig dimensions;
	
	@Override
	public void setupConfig() {
		minY = loadPropInt("Min Y Level", "", 3);
		maxY = loadPropInt("Max Y Level", "", 20);
		minMobs = loadPropInt("Min Mob Count", "", 5);
		maxMobs = loadPropInt("Max Mob Count", "", 8);
		chunkChance = (float) loadPropDouble("Chance to Spawn", "The chance for the monster box generator to try and place one in a chunk, 1 is 100%\nThis can be higher than 100% if you want multiple per chunk, , 0 is 0%", 0.6);
		
		dimensions = new DimensionConfig(configCategory, "0");
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		monster_box = new BlockMonsterBox();
		
		registerTile(TileMonsterBox.class, "monster_box");
		
		GameRegistry.registerWorldGenerator(new MonsterBoxGenerator(), 200);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
