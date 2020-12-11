/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 21:51:45 (GMT)]
 */
package vazkii.quark.base.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.base.client.ResourceProxy;
import vazkii.quark.base.client.gui.config.ConfigEvents;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.vanity.client.emotes.EmoteHandler;
import vazkii.quark.vanity.feature.EmoteSystem;

import java.util.List;

public class ClientProxy extends CommonProxy {

	private static ResourceProxy resourceProxy;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		List<IResourcePack> packs = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), LibObfuscation.DEFAULT_RESOURCE_PACKS);
		resourceProxy = new ResourceProxy(event.getSourceFile());
		packs.add(resourceProxy);

		EmoteSystem.addResourcePack(packs);

		super.preInit(event);
		ModuleLoader.preInitClient(event);

		FMLClientHandler.instance().refreshResources(VanillaResourceType.TEXTURES);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ContributorRewardHandler.setupClient();

		super.init(event);
		ModuleLoader.initClient(event);

		MinecraftForge.EVENT_BUS.register(ConfigEvents.class);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		ModuleLoader.postInitClient(event);
	}

	@Override
	public void doEmote(String playerName, String emoteName, int tier) {
		World world = Minecraft.getMinecraft().world;
		EntityPlayer player = world.getPlayerEntityByName(playerName);
		if(player instanceof AbstractClientPlayer)
			EmoteHandler.putEmote((AbstractClientPlayer) player, emoteName, tier);
	}

	@Override
	public void addResourceOverride(String path, String file) {
		resourceProxy.addResource(path, file);
	}

}
