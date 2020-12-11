package vazkii.quark.base.client;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.lib.LibObfuscation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID)
public class ContributorRewardHandler {

	private static final ImmutableSet<String> DEV_UUID = ImmutableSet.of(
			"8c826f34-113b-4238-a173-44639c53b6e6",
			"0d054077-a977-4b19-9df9-8a4d5bf20ec3",
			"458391f5-6303-4649-b416-e4c0d18f837a");

	private static final Set<String> done = Collections.newSetFromMap(new WeakHashMap<>());

	private static Thread thread;

	private static String name;

	private static final Map<String, Integer> tiers = new HashMap<>();

	private static Properties patreonTiers;

	public static int localPatronTier = 0;
	public static String featuredPatron = "";

	@SideOnly(Side.CLIENT)
	public static void setupClient() {
		name = Minecraft.getMinecraft().getSession().getUsername().toLowerCase();
	}

	public static void init() {
		if (thread != null && thread.isAlive())
			return;
		thread = new ThreadContributorListLoader();
	}

	public static int getTier(EntityPlayer player) {
		return getTier(player.getName());
	}
	
	public static int getTier(String name) {
		return tiers.getOrDefault(name.toLowerCase(Locale.ROOT), 0);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderPlayer(RenderPlayerEvent.Post event) {
		EntityPlayer player = event.getEntityPlayer();
		String uuid = EntityPlayer.getUUID(player.getGameProfile()).toString();
		if(player instanceof AbstractClientPlayer && DEV_UUID.contains(uuid) && !done.contains(uuid)) {
			AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;
			if(clientPlayer.hasPlayerInfo()) {
				NetworkPlayerInfo info = ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayer.class, clientPlayer, LibObfuscation.PLAYER_INFO);
				Map<Type, ResourceLocation> textures = ObfuscationReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, info, LibObfuscation.PLAYER_TEXTURES);
				ResourceLocation loc = new ResourceLocation("quark", "textures/misc/dev_cape.png");
				textures.put(Type.CAPE, loc);
				textures.put(Type.ELYTRA, loc);
				done.add(uuid);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public static void onPlayerJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		ContributorRewardHandler.init();
	}
	
	private static void load(Properties props) {
		List<String> allPatrons = new ArrayList<>(props.size());

		props.forEach((k, v) -> {
			String key = (String) k;
			String value = (String) v;
			
			int tier = Integer.parseInt(value);
			if(tier < 10)
				allPatrons.add(key);
			tiers.put(key.toLowerCase(Locale.ROOT), tier);
			
			if(name != null && key.toLowerCase(Locale.ROOT).equals(name))
				localPatronTier = tier;
		});
		
		if(!allPatrons.isEmpty())
			featuredPatron = allPatrons.get((int) (Math.random() * allPatrons.size()));
	}

	private static class ThreadContributorListLoader extends Thread {

		public ThreadContributorListLoader() {
			setName("Quark Contributor Loading Thread");
			setDaemon(true);
			start();
		}

		@Override
		public void run() {
			try {
				URL url = new URL("https://raw.githubusercontent.com/Vazkii/Quark/master/contributors.properties");
				patreonTiers = new Properties();
				try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
					patreonTiers.load(reader);
					load(patreonTiers);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
