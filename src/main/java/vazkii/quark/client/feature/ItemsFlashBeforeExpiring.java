package vazkii.quark.client.feature;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageItemUpdate;
import vazkii.quark.client.render.RenderItemFlashing;

import java.util.WeakHashMap;

public class ItemsFlashBeforeExpiring extends Feature {

	public static int minTime;
	
	@Override
	public void setupConfig() {
		minTime = loadPropInt("Time To Start Flashing", "How many ticks should the item have left when it starts flashing. Default is 10 seconds (200).", 200);
	}

	// Note: The following code, despite this being a client tweak, must run on servers! However, it doesn't affect clients without the tweak.

	public static void setItemAge(EntityItem item, int age) {
		item.age = age;
	}

	public static int getItemAge(EntityItem item) {
		return item.age;
	}

	private static final WeakHashMap<EntityItem, Integer> AGE_MAP = new WeakHashMap<>();
	private static final WeakHashMap<EntityItem, Integer> LIFESPAN_MAP = new WeakHashMap<>();

	public static void updateItemInfo(EntityItem item) {
		if (item.world.isRemote || !(item.world instanceof WorldServer))
			return;

		int age = getItemAge(item);
		int lifespan = item.lifespan;

		boolean anyChange = false;

		if (!AGE_MAP.containsKey(item))
			AGE_MAP.put(item, age);
		else {
			int prev = AGE_MAP.get(item);

			if (age != prev && age != prev + 1) {
				anyChange = true;
			} else
				AGE_MAP.put(item, age);
		}

		if (!LIFESPAN_MAP.containsKey(item))
			LIFESPAN_MAP.put(item, lifespan);
		else {
			int prev = LIFESPAN_MAP.get(item);

			if (lifespan != prev) {
				anyChange = true;
			}
		}

		if (anyChange) {
			WorldServer server = (WorldServer) item.world;
			for (EntityPlayer player : server.getEntityTracker().getTrackingPlayers(item)) {
				if (player instanceof EntityPlayerMP)
					NetworkHandler.INSTANCE.sendTo(new MessageItemUpdate(item.getEntityId(), age, lifespan),
							(EntityPlayerMP) player);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityItem.class, RenderItemFlashing.factory());
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
