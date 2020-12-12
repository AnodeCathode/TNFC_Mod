package tnfcmod.qfc.features;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import tnfcmod.util.ConfigTNFCMod;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
//import vazkii.quark.base.Quark;
import tnfcmod.qfc.item.ItemQuarkFood;
import tnfcmod.qfc.base.LibEntityIDs;
import tnfcmod.qfc.module.Feature;
import tnfcmod.qfc.render.RenderFrog;
import tnfcmod.qfc.entity.EntityFrog;

public class Frogs extends Feature {

	public static boolean frogsDoTheFunny;
	public static int weight, min, max;

	public static boolean jumpBoost;

	public static ItemQuarkFood frogLeg;
	public static ItemQuarkFood cookedFrogLeg;
	public static ItemQuarkFood gildedFrogLeg;

	@Override
	public void setupConfig() {
		frogsDoTheFunny = loadPropBool("Frogs know what day it is", "", false);

		jumpBoost = loadPropBool("Frog legs can be made into jump boost reagent", "", true);

		weight = ConfigTNFCMod.GENERAL.frogweight;
		min = 1;
		max = 3;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		frogLeg = new ItemQuarkFood("frog_leg", 2, 0.3F, true);
		cookedFrogLeg = new ItemQuarkFood("cooked_frog_leg", 4, 1.25F, true);

		if (jumpBoost) {
			gildedFrogLeg = new ItemQuarkFood("golden_frog_leg", 4, 2.5F);
			gildedFrogLeg.setCreativeTab(CreativeTabs.BREWING);

			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(gildedFrogLeg),
					"GGG", "GLG", "GGG",
					'G', "nuggetGold",
					'L', ProxyRegistry.newStack(frogLeg));

			PotionHelper.addMix(PotionTypes.WATER, gildedFrogLeg, PotionTypes.MUNDANE);
			PotionHelper.addMix(PotionTypes.AWKWARD, gildedFrogLeg, PotionTypes.LEAPING);
		}

		String frogName = "quark:frog";
		LootTableList.register(EntityFrog.FROG_LOOT_TABLE);
		EntityRegistry.registerModEntity(new ResourceLocation(frogName), EntityFrog.class, frogName, LibEntityIDs.FROG, tnfcmod.tnfcmod.instance, 80, 3, true, 0xbc9869, 0xffe6ad);
	}
	
	@Override
	public void init() {
		EntityRegistry.addSpawn(EntityFrog.class, ConfigTNFCMod.GENERAL.frogfreq, min, max, EnumCreatureType.CREATURE, BiomesTFC.SWAMPLAND);
	}

	@Override
	public void postInit() {
		FurnaceRecipes.instance().addSmelting(frogLeg, new ItemStack(cookedFrogLeg), 0.35F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityFrog.class, RenderFrog.FACTORY);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
