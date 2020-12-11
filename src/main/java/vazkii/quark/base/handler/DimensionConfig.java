package vazkii.quark.base.handler;

import net.minecraft.world.World;
import vazkii.quark.base.module.ModuleLoader;

import java.util.ArrayList;
import java.util.List;

public class DimensionConfig {

	private final boolean blacklist;
	private final List<Integer> dims;
	
	public DimensionConfig(String parent) {
		this(parent, false, "0");
	}
	
	public DimensionConfig(String parent, String dimStr) {
		this(parent, false, dimStr.split(","));
	}
	
	public DimensionConfig(String parent, boolean blacklist, String... defaultValues) {
		String category = parent + ".dimensions";
		this.blacklist = ModuleLoader.config.getBoolean("Is Blacklist", category, blacklist, "");
		
		String[] dimensionValues = ModuleLoader.config.getStringList("Dimensions", category, defaultValues, "");
		dims = new ArrayList<>();
		for(String s : dimensionValues)
			try {
				dims.add(Integer.parseInt(s));
			} catch(NumberFormatException ignored) {}
	}
	
	public boolean canSpawnHere(World world) {
		return dims.contains(world.provider.getDimension()) != blacklist;
	}
	
}
