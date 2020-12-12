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

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import tnfcmod.util.ConfigTNFCMod;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
//import vazkii.quark.base.Quark;
import tnfcmod.qfc.item.ItemQfcFood;
import tnfcmod.qfc.base.LibEntityIDs;
import tnfcmod.qfc.module.Feature;
import tnfcmod.qfc.render.RenderFrog;
import tnfcmod.qfc.entity.EntityFrog;

public class Frogs extends Feature {

	public static boolean frogsDoTheFunny;
	public static int weight, min, max;

	public static boolean jumpBoost;

	public static ItemQfcFood frogLeg;
	public static ItemQfcFood cookedFrogLeg;
	public static ItemQfcFood gildedFrogLeg;

    public static final FoodData FROG_LEG = new FoodData(4, 0, 0, 0, 0, 0, 0.5f, 0, 1.3f);
    public static final FoodData COOKED_FROG_LEG = new FoodData(4, 0, 2f, 0, 0, 0, 2.5f, 0, 2.25f);

	@Override
	public void setupConfig() {
		frogsDoTheFunny = loadPropBool("Frogs know what day it is", "", false);


		weight = ConfigTNFCMod.GENERAL.frogweight;
		min = 1;
		max = 3;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		frogLeg = new ItemQfcFood("frog_leg", 2, 0.3F, true);
		cookedFrogLeg = new ItemQfcFood("cooked_frog_leg", 4, 1.25F, true);

		String frogName = "tnfcmod:frog";
		LootTableList.register(EntityFrog.FROG_LOOT_TABLE);
		EntityRegistry.registerModEntity(new ResourceLocation(frogName), EntityFrog.class, frogName, LibEntityIDs.FROG, tnfcmod.tnfcmod.instance, 80, 3, true, 0xbc9869, 0xffe6ad);
	}
	
	@Override
	public void init() {
        CapabilityFood.CUSTOM_FOODS.put(IIngredient.of(frogLeg), () -> new FoodHandler(null, FROG_LEG));
        CapabilityFood.CUSTOM_FOODS.put(IIngredient.of(cookedFrogLeg), () -> new FoodHandler(null, COOKED_FROG_LEG));
		EntityRegistry.addSpawn(EntityFrog.class, ConfigTNFCMod.GENERAL.frogfreq, min, max, EnumCreatureType.CREATURE, BiomesTFC.SWAMPLAND);
	}

	@Override
	public void postInit(){	}

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
