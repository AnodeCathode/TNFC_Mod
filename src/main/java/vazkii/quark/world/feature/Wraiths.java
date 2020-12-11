/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 03:48:53 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.lib.LibPotionIndices;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.potion.PotionMod;
import vazkii.quark.world.client.render.RenderWraith;
import vazkii.quark.world.entity.EntityWraith;
import vazkii.quark.world.item.ItemSoulBead;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Wraiths extends Feature {

	public static Item soul_bead;

	public static Potion curse;

	public static int curseTime;
	public static boolean enableCurse;

	public static int weight, min, max;
	public static int curseRange;

	public static String[] wraithSounds;
	public static List<String> validWraithSounds;

	@Override
	public void setupConfig() {
		weight = loadPropInt("Spawn Weight", "The higher, the more will spawn", 60);
		min = loadPropInt("Smallest spawn group", "", 4);
		max = loadPropInt("Largest spawn group", "", 6);
		curseRange = loadPropInt("Curse Range", "", 64);
		curseTime = loadPropInt("Curse Time", "How long the curse effect lasts for (in ticks)", 12000);
		enableCurse = loadPropBool("Enable Curse", "", true);
		wraithSounds = loadPropStringList("Wraith sound effects",
				"List of sound sets to use with wraiths.\nThree sounds must be provided per entry, separated by | (in the format idle|hurt|death). Leave blank for no sound (i.e. if a mob has no ambient noise)", new String[] {
						"entity.sheep.ambient|entity.sheep.hurt|entity.sheep.death",
						"entity.cow.ambient|entity.cow.hurt|entity.cow.death",
						"entity.pig.ambient|entity.pig.hurt|entity.pig.death",
						"entity.chicken.ambient|entity.chicken.hurt|entity.chicken.death",
						"entity.horse.ambient|entity.horse.hurt|entity.horse.death",
						"entity.cat.ambient|entity.cat.hurt|entity.cat.death",
						"entity.wolf.ambient|entity.wolf.hurt|entity.wolf.death",
						"entity.villager.ambient|entity.villager.hurt|entity.villager.death",
						"entity.polar_bear.ambient|entity.polar_bear.hurt|entity.polar_bear.death",
						"entity.zombie.ambient|entity.zombie.hurt|entity.zombie.death",
						"entity.skeleton.ambient|entity.skeleton.hurt|entity.skeleton.death",
						"entity.spider.ambient|entity.spider.hurt|entity.spider.death",
						"|entity.creeper.hurt|entity.creeper.death",
						"entity.endermen.ambient|entity.endermen.hurt|entity.endermen.death",
						"entity.zombie_pig.ambient|entity.zombie_pig.hurt|entity.zombie_pig.death",
						"entity.witch.ambient|entity.witch.hurt|entity.witch.death",
						"entity.blaze.ambient|entity.blaze.hurt|entity.blaze.death",
						"entity.llama.ambient|entity.llama.hurt|entity.llama.death",
						"|quark:entity.stoneling.cry|quark:entity.stoneling.die",
						"quark:entity.frog.idle|quark:entity.frog.hurt|quark:entity.frog.die"
				});

		validWraithSounds = Arrays.stream(wraithSounds)
				.filter((s) -> s.split("\\|").length == 3).collect(Collectors.toList());
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		soul_bead = new ItemSoulBead();

		if(enableCurse)
			curse = new PotionMod("curse", true, 0x000000, LibPotionIndices.CURSE);

		String wraithName = "quark:wraith";
		EntityRegistry.registerModEntity(new ResourceLocation(wraithName), EntityWraith.class, wraithName, LibEntityIDs.WRAITH, Quark.instance, 80, 3, true, 0xececec, 0xbdbdbd);
		LootTableList.register(EntityWraith.LOOT_TABLE);
	}
	
	@Override
	public void init() {
		EntityRegistry.addSpawn(EntityWraith.class, weight, min, max, EnumCreatureType.MONSTER, BiomeDictionary.getBiomes(Type.NETHER).toArray(new Biome[0]));
	}

	@Override
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityWraith.class, RenderWraith.FACTORY);
	}

	@SubscribeEvent
	public void onSpawn(LivingSpawnEvent.CheckSpawn event) {
		if(event.getResult() != Result.ALLOW && event.getEntityLiving() instanceof IMob && event.getWorld() instanceof WorldServer) {
			List<EntityPlayer> players = ((WorldServer) event.getWorld()).playerEntities;
			for(EntityPlayer player : players)
				if(!player.isSpectator() && player.getActivePotionEffect(curse) != null && player.getDistanceSq(event.getEntity()) < curseRange * curseRange) {
					if(event.getEntityLiving().getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
						event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
					event.setResult(Result.ALLOW);
					return;
				}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
