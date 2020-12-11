/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 13:34 AM (EST)]
 */
package vazkii.quark.world.feature;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.client.render.RenderFoxhound;
import vazkii.quark.world.entity.EntityFoxhound;

public class Foxhounds extends Feature {

	public static double tameChance;

	public static int weight, min, max;
	
	@Override
	public void setupConfig() {
		weight = loadPropInt("Spawn Weight", "The higher, the more will spawn", 4);
		min = loadPropInt("Smallest spawn group", "", 1);
		max = loadPropInt("Largest spawn group", "", 2);
		
		tameChance = loadPropChance("Chance to Tempt", "The chance coal will tame a foxhound", 0.05);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		String foxName = "quark:foxhound";

		LootTableList.register(EntityFoxhound.FOXHOUND_LOOT_TABLE);
		EntityRegistry.registerModEntity(new ResourceLocation(foxName), EntityFoxhound.class, foxName, LibEntityIDs.FOXHOUND, Quark.instance, 80, 3, true, 0x890d0d, 0xf2af4b);
	}
	
	
	@Override
	public void init() {
		EntityRegistry.addSpawn(EntityFoxhound.class, weight, min, max, EnumCreatureType.MONSTER, BiomeDictionary.getBiomes(Type.NETHER).toArray(new Biome[0]));
	}
		
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityFoxhound.class, RenderFoxhound::new);
	}

}
