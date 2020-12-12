/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 21:52:08 (GMT)]
 */
package tnfcmod.qfc.module;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tnfcmod.qfc.api.module.ModuleLoadedEvent;
import tnfcmod.qfc.util.LibMisc;
import tnfcmod.qfc.QfcWorld;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ModuleLoader {
	
	// Checks if the Java Debug Wire Protocol is enabled
	public static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp"); 

	private static List<Class<? extends Module>> moduleClasses;
	public static final Map<Class<? extends Module>, Module> moduleInstances = new HashMap<>();
	public static final Map<Class<? extends Feature>, Feature> featureInstances = new HashMap<>();
	public static final Map<String, Feature> featureClassnames = new HashMap<>();

	public static List<Module> enabledModules;
	public static final List<Runnable> lazyOreDictRegisters = new ArrayList<>();

	public static Configuration config;
	public static File configFile;
	public static boolean firstLoad;

	private static void setupModuleClasses() {
		moduleClasses = new ArrayList<>();
		registerModule(QfcWorld.class);

	}
	
	public static void preInit(FMLPreInitializationEvent event) {
		setupModuleClasses();
		moduleClasses.forEach(clazz -> {
			try {
				Module instance = clazz.newInstance();
				if (!MinecraftForge.EVENT_BUS.post(new ModuleLoadedEvent(instance)))
					moduleInstances.put(clazz, instance);
			} catch (Exception e) {
				throw new RuntimeException("Can't initialize module " + clazz, e);
			}
		});

		setupConfig(event);

		forEachModule(module -> tnfcmod.tnfcmod.getLog().info("Module " + module.name + " is " + (module.enabled ? "enabled" : "disabled")));

		forEachEnabled(module -> module.preInit(event));
		forEachEnabled(Module::postPreInit);
		
		//RecipeProcessor.runConsumers();
	}
	
	public static void init(FMLInitializationEvent event) {
		forEachEnabled(Module::init);
	}

	public static void postInit(FMLPostInitializationEvent event) {
		forEachEnabled(Module::postInit);
	}

	public static void finalInit(FMLPostInitializationEvent event) {
		forEachEnabled(Module::finalInit);
	}
	
	@SideOnly(Side.CLIENT)
	public static void preInitClient(FMLPreInitializationEvent event) {
		forEachEnabled(Module::preInitClient);
	}

	@SideOnly(Side.CLIENT)
	public static void initClient(FMLInitializationEvent event) {
		forEachEnabled(Module::initClient);
	}

	@SideOnly(Side.CLIENT)
	public static void postInitClient(FMLPostInitializationEvent event) {
		forEachEnabled(Module::postInitClient);
	}

	public static void serverStarting(FMLServerStartingEvent event) {
		forEachEnabled(Module::serverStarting);
	}

	public static void setupConfig(FMLPreInitializationEvent event) {
		configFile = event.getSuggestedConfigurationFile();
		if(!configFile.exists())
			firstLoad = true;
		
		config = new Configuration(configFile);
		config.load();
		
		loadConfig();
	}
	
	public static void loadConfig() {
		GlobalConfig.initGlobalConfig();

		forEachModule(module -> {
			module.enabled = true;
			if(module.canBeDisabled()) {
				ConfigHelper.needsRestart = true;
				module.enabled = ConfigHelper.loadPropBool(module.name, "_modules", module.getModuleDescription(), module.isEnabledByDefault());
				module.prop = ConfigHelper.lastProp;
			}
		});


		enabledModules = new ArrayList<>(moduleInstances.values());
		enabledModules.removeIf(module -> !module.enabled);

		loadModuleConfigs();
		
		if(config.hasChanged())
			config.save();
	}

	private static void loadModuleConfigs() {
		forEachModule(Module::setupConfig);
	}
	
	public static boolean isModuleEnabled(Class<? extends Module> clazz) {
		Module module = moduleInstances.get(clazz);
		return module != null && module.enabled;
	}

	public static boolean isFeatureEnabled(Class<? extends Feature> clazz) {
		Feature feature = featureInstances.get(clazz);
		return feature != null && feature.enabled;
	}

	public static void forEachModule(Consumer<Module> consumer) {
		moduleInstances.values().forEach(consumer);
	}

	public static void forEachEnabled(Consumer<Module> consumer) {
		enabledModules.forEach(consumer);
	}

	private static void registerModule(Class<? extends Module> clazz) {
		if(!moduleClasses.contains(clazz))
			moduleClasses.add(clazz);
	}

	@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID)
	public static class EventHandler {

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
			if(eventArgs.getModID().equals(LibMisc.MOD_ID))
				loadConfig();
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		public static void onRegistered(RegistryEvent.Register<Item> event) {
			lazyOreDictRegisters.forEach(Runnable::run);
		}

	}

}
