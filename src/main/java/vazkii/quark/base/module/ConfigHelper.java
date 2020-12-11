/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:16:30 (GMT)]
 */
package vazkii.quark.base.module;

import net.minecraftforge.common.config.Property;

public class ConfigHelper {

	public static boolean needsRestart;
	public static boolean allNeedRestart = false;
	public static Property lastProp;
	
	public static int loadPropInt(String propName, String category, String desc, int default_) {
		Property prop = ModuleLoader.config.get(category, propName, default_);
		prop.setComment(desc);
		setNeedsRestart(prop);
		
		lastProp = prop;
		return prop.getInt(default_);
	}

	public static double loadPropDouble(String propName, String category, String desc, double default_, double min, double max) {
		Property prop = ModuleLoader.config.get(category, propName, default_, desc, min, max);
		setNeedsRestart(prop);

		lastProp = prop;
		return prop.getDouble(default_);
	}

	public static double loadPropDouble(String propName, String category, String desc, double default_) {
		Property prop = ModuleLoader.config.get(category, propName, default_);
		prop.setComment(desc);
		setNeedsRestart(prop);
		
		lastProp = prop;
		return prop.getDouble(default_);
	}

	public static boolean loadPropBool(String propName, String category, String desc, boolean default_) {
		Property prop = ModuleLoader.config.get(category, propName, default_);
		prop.setComment(desc);
		setNeedsRestart(prop);
		
		lastProp = prop;
		return prop.getBoolean(default_);
	}

	public static String loadPropString(String propName, String category, String desc, String default_) {
		Property prop = ModuleLoader.config.get(category, propName, default_);
		prop.setComment(desc);
		setNeedsRestart(prop);
		
		lastProp = prop;
		return prop.getString();
	}

	public static String[] loadPropStringList(String propName, String category, String desc, String[] default_) {
		Property prop = ModuleLoader.config.get(category, propName, default_);
		prop.setComment(desc);
		setNeedsRestart(prop);
		
		lastProp = prop;
		return prop.getStringList();
	}

	public static double loadPropChance(String propName, String category, String desc, double default_) {
		return loadPropDouble(propName, category, desc, default_, 0, 1);
	}

	public static double loadLegacyPropChance(String propName, String category, String oldName, String desc, double default_) {
		double chanceValue = default_;
		if (hasConfigKey(category, oldName)) {
			int value = loadPropInt(oldName, category, "", 0);
			removeKey(oldName, category);
			if (value <= 0)
				chanceValue = 0;
			else
				chanceValue = 1.0 / value;
		}
		return loadPropChance(propName, category, desc, chanceValue);
	}

	public static boolean hasConfigKey(String propName, String category) {
		return ModuleLoader.config.hasKey(category, propName);
	}

	public static void removeKey(String propName, String category) {
		ModuleLoader.config.getCategory(category).remove(propName);
	}

	private static void setNeedsRestart(Property prop) {
		if(needsRestart)
			prop.setRequiresMcRestart(true);
		needsRestart = allNeedRestart;
	}
	
}
