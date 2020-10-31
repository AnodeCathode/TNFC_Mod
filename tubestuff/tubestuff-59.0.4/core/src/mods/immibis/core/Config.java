package mods.immibis.core;


import java.io.File;

import mods.immibis.core.api.porting.SidedProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
	//private static File configFile;
	public static Configuration config;
	private static boolean autoAssign;
	
	static {
		File configFile;
		{
			File configDir = new File(SidedProxy.instance.getMinecraftDir(), "config");
			configDir.mkdir();
			configFile = new File(configDir, "immibis.cfg");
		}
		config = new Configuration(configFile);
		config.load();
		
		autoAssign = getBoolean("autoAssign", true);
		
		config.get(Configuration.CATEGORY_GENERAL, "autoAssign", false).set("false");
		config.save();
	}
	
	public static boolean getBoolean(String name, boolean def)
	{
		boolean result = config.get(Configuration.CATEGORY_GENERAL, name, def).getBoolean(def);
		config.save();
		return result;
	}
	
	//private static HashSet<String> forceAllowReassign = new HashSet<String>();
	
	public static void save() {
		if(config.hasChanged())
			config.save();
	}
	
	public static String getString(String name, String def, String category, String comment) {
		boolean save = !config.hasCategory(category) || config.getCategory(category).get(name) == null;
		Property prop = config.get(category, name, def);
		save |= (prop.comment == null && comment != null) || !prop.comment.equals(comment);
		prop.comment = comment;
		if(save)
			config.save();
		return prop.getString();
	}

	public static int getInt(String name, int def)
	{
		return config.get(Configuration.CATEGORY_GENERAL, name, def).getInt(def);
	}
	
	public static int getInt(String name, int def, String comment)
	{
		return getInt(name, def, Configuration.CATEGORY_GENERAL, comment);
	}
	
	public static int getInt(String name, int def, String category, String comment)
	{
		boolean save = !config.hasCategory(category) || config.getCategory(category).get(name) == null;
		Property prop = config.get(category, name, def);
		save |= (prop.comment == null && comment != null) || !prop.comment.equals(comment);
		prop.comment = comment;
		if(save)
			config.save();
		return prop.getInt(def);
	}
}
