/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 21:49:33 (GMT)]
 */
package vazkii.quark.base.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.capability.CapabilityHandler;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.base.command.CommandConfig;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.GuiHandler;
import vazkii.quark.base.network.MessageRegister;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.world.feature.DungeonTweaksCompat;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		CapabilityHandler.register();
		QuarkSounds.init();
		ModuleLoader.preInit(event);

		NetworkRegistry.INSTANCE.registerGuiHandler(Quark.instance, new GuiHandler());
		MessageRegister.init();
	}

	public void init(FMLInitializationEvent event) {
		ModuleLoader.init(event);
		ContributorRewardHandler.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		DungeonTweaksCompat.legacyCheck();
		DungeonTweaksCompat.registerDungeons();
		ModuleLoader.postInit(event);
	}
	
	public void finalInit(FMLPostInitializationEvent event) {
		ModuleLoader.finalInit(event);
	}

	public void serverStarting(FMLServerStartingEvent event) {
		ModuleLoader.serverStarting(event);
		
		if(GlobalConfig.enableConfigCommand)
			event.registerServerCommand(new CommandConfig());
	}

	public void doEmote(String playerName, String emoteName, int tier) {
		// proxy override
	}
	
	public void addResourceOverride(String path, String file) {
		// proxy override
	}

	
}
