package vazkii.quark.world.feature;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.client.render.RenderStoneling;
import vazkii.quark.world.entity.EntityStoneling;
import vazkii.quark.world.item.ItemDiamondHeart;

public class Stonelings extends Feature {

	public static int maxYLevel, weight;
	public static DimensionConfig dimensions;
	public static boolean enableDiamondHeart;
	public static boolean cautiousStonelings;
	public static boolean tamableStonelings;

	public static Item diamond_heart;

	@Override
	public void setupConfig() {
		maxYLevel = loadPropInt("Max Y Level", "", 24);
		weight = loadPropInt("Spawning Weight", "Higher = more stonelings", 80);
		enableDiamondHeart = loadPropBool("Enable Diamond Heart", "", true);
		cautiousStonelings = loadPropBool("Cautious Stonelings", "Do stonelings get spooked when players move suddenly near them?", true);
		tamableStonelings = loadPropBool("Tamable Stonelings", "Can stonelings be tamed by feeding them diamonds?", true);

		dimensions = new DimensionConfig(configCategory);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(enableDiamondHeart)
			diamond_heart = new ItemDiamondHeart();

		String stonelingName = "quark:stoneling";
		LootTableList.register(EntityStoneling.LOOT_TABLE);
		LootTableList.register(EntityStoneling.CARRY_LOOT_TABLE);
		EntityRegistry.registerModEntity(new ResourceLocation(stonelingName), EntityStoneling.class, stonelingName, LibEntityIDs.STONELING, Quark.instance, 80, 3, true, 0xA1A1A1, 0x505050);

		EntityRegistry.addSpawn(EntityStoneling.class, weight, 1, 1, EnumCreatureType.MONSTER, DepthMobs.getBiomesWithMob(EntityZombie.class));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityStoneling.class, RenderStoneling.FACTORY);
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
