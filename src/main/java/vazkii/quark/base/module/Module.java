/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 21:52:14 (GMT)]
 */
package vazkii.quark.base.module;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.api.module.FeatureEvent;
import vazkii.quark.api.module.IFeature;
import vazkii.quark.api.module.IModule;
import vazkii.quark.base.Quark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Module implements IModule {

	public final String name = makeName();
	public final Map<String, Feature> features = new HashMap<>();
	public final List<Feature> enabledFeatures = new ArrayList<>();
	public boolean enabled;
	public Property prop;

	public void addFeatures() {
		// NO-OP
	}

	public void registerFeature(Feature feature) {
		registerFeature(feature, convertName(feature.getClass().getSimpleName()));
	}

	public void registerFeature(Feature feature, boolean enabledByDefault) {
		registerFeature(feature, convertName(feature.getClass().getSimpleName()), enabledByDefault);
	}

	public String convertName(String origName) {
		String withSpaces = origName.replaceAll("(?<=.)([A-Z])", " $1").toLowerCase();
		return Character.toUpperCase(withSpaces.charAt(0)) + withSpaces.substring(1);
	}

	public void registerFeature(Feature feature, String name) {
		registerFeature(feature, name, true);
	}

	public void registerFeature(Feature feature, String name, boolean enabledByDefault) {
		Class<? extends Feature> clazz = feature.getClass();
		if(ModuleLoader.featureInstances.containsKey(clazz))
			throw new IllegalArgumentException("Feature " + clazz + " is already registered!");

		feature.enabledByDefault = enabledByDefault;
		feature.prevEnabled = false;

		feature.module = this;
		feature.configName = name;
		feature.configCategory = this.name + "." + name;

		if (!MinecraftForge.EVENT_BUS.post(new FeatureEvent.Loaded(feature))) {
			ModuleLoader.featureInstances.put(clazz, feature);
			ModuleLoader.featureClassnames.put(clazz.getSimpleName(), feature);
			features.put(name, feature);
		}
	}

	public void setupConfig() {
		if(features.isEmpty())
			addFeatures();
		
		forEachFeature(feature -> {
			ConfigHelper.needsRestart = feature.requiresMinecraftRestartToEnable();
			feature.enabled = loadPropBool(feature.configName, feature.getFeatureDescription(), feature.enabledByDefault) && enabled;
			feature.prop = ConfigHelper.lastProp;
			
			feature.setupConstantConfig();
			
			if(!feature.forceLoad && GlobalConfig.enableAntiOverlap) {
				String[] incompatibilities = feature.getIncompatibleMods();
				if(incompatibilities != null) {
					List<String> failures = new ArrayList<>();

					for(String s : incompatibilities)
						if(Loader.isModLoaded(s)) {
							feature.enabled = false;
							failures.add(s);
						}
					
					if(!failures.isEmpty())
						Quark.LOG.info("'" + feature.configName + "' is forcefully disabled as it's incompatible with the following loaded mods: " + failures);
				}
			}
			
			if(!feature.loadtimeDone) {
				feature.enabledAtLoadtime = feature.enabled;
				feature.loadtimeDone = true;
			}
			
			if(feature.enabled && !enabledFeatures.contains(feature))
				enabledFeatures.add(feature);
			else if(!feature.enabled)
				enabledFeatures.remove(feature);
			
			feature.setupConfig();
			
			if(!feature.enabled && feature.prevEnabled) {
				MinecraftForge.EVENT_BUS.post(new FeatureEvent.Disabled(feature));
				if(feature.hasSubscriptions())
					MinecraftForge.EVENT_BUS.unregister(feature);
				if(feature.hasTerrainSubscriptions())
					MinecraftForge.TERRAIN_GEN_BUS.unregister(feature);
				if(feature.hasOreGenSubscriptions())
					MinecraftForge.ORE_GEN_BUS.unregister(feature);
				feature.onDisabled();
				MinecraftForge.EVENT_BUS.post(new FeatureEvent.PostDisable(feature));
			} else if(feature.enabled && (feature.enabledAtLoadtime || !feature.requiresMinecraftRestartToEnable()) && !feature.prevEnabled) {
				MinecraftForge.EVENT_BUS.post(new FeatureEvent.Enabled(feature));
				if(feature.hasSubscriptions())
					MinecraftForge.EVENT_BUS.register(feature);
				if(feature.hasTerrainSubscriptions())
					MinecraftForge.TERRAIN_GEN_BUS.register(feature);
				if(feature.hasOreGenSubscriptions())
					MinecraftForge.ORE_GEN_BUS.register(feature);
				feature.onEnabled();
				MinecraftForge.EVENT_BUS.post(new FeatureEvent.PostEnable(feature));
			}
			
			feature.prevEnabled = feature.enabled;
		});
	}

	public void preInit(FMLPreInitializationEvent event) {
		forEachEnabled(feature -> feature.preInit(event));
	}
	
	public void postPreInit() {
		forEachEnabled(Feature::postPreInit);
	}

	public void init() {
		forEachEnabled(Feature::init);
	}

	public void postInit() {
		forEachEnabled(Feature::postInit);
	}
	
	public void finalInit() {
		forEachEnabled(Feature::finalInit);
	}
	
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		forEachEnabled(Feature::preInitClient);
	}

	@SideOnly(Side.CLIENT)
	public void initClient() {
		forEachEnabled(Feature::initClient);
	}

	@SideOnly(Side.CLIENT)
	public void postInitClient() {
		forEachEnabled(Feature::postInitClient);
	}

	public void serverStarting() {
		forEachEnabled(Feature::serverStarting);
	}

	public boolean canBeDisabled() {
		return true;
	}

	public boolean isEnabledByDefault() {
		return true;
	}

	String makeName() {
		return getClass().getSimpleName().replaceAll("Quark", "").toLowerCase();
	}

	public String getModuleDescription() {
		return "";
	}
	
	public ItemStack getIconStack() {
		return new ItemStack(Blocks.BARRIER);
	}

	public final void forEachFeature(Consumer<Feature> consumer) {
		features.values().forEach(consumer);
	}

	public final void forEachEnabled(Consumer<Feature> consumer) {
		enabledFeatures.forEach(consumer);
	}

	public final int loadPropInt(String propName, String desc, int default_) {
		return ConfigHelper.loadPropInt(propName, name, desc, default_);
	}

	public final double loadPropDouble(String propName, String desc, double default_) {
		return ConfigHelper.loadPropDouble(propName, name, desc, default_);
	}

	public final boolean loadPropBool(String propName, String desc, boolean default_) {
		return ConfigHelper.loadPropBool(propName, name, desc, default_);
	}

	public final String loadPropString(String propName, String desc, String default_) {
		return ConfigHelper.loadPropString(propName, name, desc, default_);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Map<String, ? extends IFeature> getFeatures() {
		return features;
	}

	@Override
	public List<? extends IFeature> getEnabledFeatures() {
		return enabledFeatures;
	}
}
