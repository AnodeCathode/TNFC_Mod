package tnfcmod.qfc.module;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class GlobalConfig {

	public static boolean enableAntiOverlap;
	public static boolean enableConfigCommand;


	public static void initGlobalConfig() {
		String category = "_global";
		ConfigHelper.needsRestart = ConfigHelper.allNeedRestart = true;

	}
	
	public static void changeConfig(String category, String key, String value, Property.Type type, boolean saveToFile) {
		if(!enableConfigCommand)
			return;
		
		Configuration config = ModuleLoader.config;
		
		if(config.hasKey(category, key)) {
			try {
				switch(type) {
					case BOOLEAN:
						boolean b = Boolean.parseBoolean(value);
						config.get(category, key, false).setValue(b);
					case INTEGER:
						int i = Integer.parseInt(value);
						config.get(category, key, 0).setValue(i);
					case DOUBLE:
						double d = Double.parseDouble(value);
						config.get(category, key, 0.0).setValue(d);
					default:
						config.get(category, key, "", "", type).setValue(value);
				}
			} catch(IllegalArgumentException ignored) {}
			
			if(config.hasChanged()) {
				ModuleLoader.forEachModule(Module::setupConfig);
				
				if(saveToFile)
					config.save();
			}
		}
	}
	
}
