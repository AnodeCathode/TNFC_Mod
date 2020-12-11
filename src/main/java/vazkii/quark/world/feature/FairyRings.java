package vazkii.quark.world.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.FairyRingGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FairyRings extends Feature {

	private static final Pattern BLOCKSTATE_PARSER = Pattern.compile("^(\\D+?):(\\d+)$");
	
	public static double forestChance, plainsChance;
	public static DimensionConfig dimensions;
	public static List<IBlockState> ores;

	private static boolean initialized = false;
	public static String[] oresArr;
	
	@Override
	public void setupConfig() {
		forestChance = loadLegacyPropChance("Forest Percentage Chance", "Forest Chance", "", 0.00625);
		plainsChance = loadLegacyPropChance("Plains Percentage Chance", "Plains Chance", "", 0.0025);
		dimensions = new DimensionConfig(configCategory, "0");
		
		oresArr = loadPropStringList("Spawnable Ores", "", new String[] {
			Objects.toString(Blocks.EMERALD_ORE.getRegistryName()),
				Objects.toString(Blocks.DIAMOND_ORE.getRegistryName())
		});
		if(initialized)
			loadOres();
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new FairyRingGenerator(), 5);
	}
	
	@Override
	public void postInit() {
		loadOres();
	}

	@SuppressWarnings("deprecation")
	private void loadOres() {
		ores = new ArrayList<>(oresArr.length);
		for(String s : oresArr) {
			int meta = 0;
			Matcher m = BLOCKSTATE_PARSER.matcher(s);
			if(m.matches()) {
				s = m.group(1);
				meta = Integer.parseInt(m.group(2));
			}
			
			Block b = Block.getBlockFromName(s);
			if(b == null)
				new IllegalArgumentException("Block " + s + " does not exist!").printStackTrace();
			else ores.add(b.getStateFromMeta(meta));
		}
			
		initialized = true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
