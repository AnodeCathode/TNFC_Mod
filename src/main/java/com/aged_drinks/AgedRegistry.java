package com.aged_drinks;

import java.util.ArrayList;

import betterwithmods.common.BWRegistry;
import blusunrize.immersiveengineering.common.items.ItemPowerpack;
import com.eerussianguy.firmalife.registry.ItemsFL;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.properties.DrinkableProperty;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.ItemPowder;

import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class AgedRegistry {

	public static Fluid
		RUM,
		BEER,
		WHISKEY,
		CORN_WHISKEY,
		RYE_WHISKEY,
		VODKA,
		CIDER,
		SAKE;
	
    private static final ResourceLocation STILL = new ResourceLocation("tfc", "blocks/fluid_still");
    private static final ResourceLocation FLOW = new ResourceLocation("tfc", "blocks/fluid_flow");

	public static Fluid createAgedDrink(String name, Potion potion, int duration, int amplifier) {
		Fluid youngFluid = FluidRegistry.getFluid(name);
		DrinkableProperty property = (player) -> {
	    		player.addPotionEffect(new PotionEffect(potion, duration, amplifier, false, false));
	    		if(AgedDrinksConfig.General.enableDrunkness) {
	    			if(Math.random() < 0.25) {
	    				PotionEffect effect = new PotionEffect(MobEffects.NAUSEA, 1500, 0, false, false);
						effect.setCurativeItems(new ArrayList<ItemStack>());
	    				player.addPotionEffect(effect);
	    			}
	    		}
    	};
    	
		Fluid agedFluid = new Fluid("aged_" + name, STILL, FLOW, youngFluid.getColor()).setRarity(EnumRarity.UNCOMMON);
    	FluidWrapper agedFluidWrapper = registerFluid(agedFluid).with(DrinkableProperty.DRINKABLE, property);
    	
    	return agedFluid;
	}
	
	public static BarrelRecipe getRecipeFor(String name, ItemStack item) {
		int len = 8 * 24000 * 3;
    	BarrelRecipe br = new BarrelRecipe(
    			IIngredient.of(FluidRegistry.getFluid(name), 1000),
    			IIngredient.of(item),
    	    	new FluidStack(FluidRegistry.getFluid("aged_" + name), 100),
    	    	new ItemStack(Items.AIR),
    	    	len
    	    	);
    	br.setRegistryName("aged_" + name);
    	return br;
	}
	
    
    public static FluidWrapper registerFluid(Fluid newFluid) {
    	boolean isDefault = FluidRegistry.registerFluid(newFluid);
        if (!isDefault){
            newFluid = FluidRegistry.getFluid(newFluid.getName());
        }
        FluidRegistry.addBucketForFluid(newFluid);
        FluidWrapper properties = FluidsTFC.getWrapper(newFluid);
        return properties;
    }

	public static void registerAgedDrinks(RegistryEvent.Register<BarrelRecipe> event) {
		RUM = createAgedDrink("rum", MobEffects.JUMP_BOOST, AgedDrinksConfig.General.jumpBoostTicks, AgedDrinksConfig.General.jumpBoostLevel);
		event.getRegistry().register(getRecipeFor("rum", new ItemStack(ItemsFL.GROUND_CINNAMON, 4)));
		
		WHISKEY = createAgedDrink("whiskey", MobEffects.HASTE, AgedDrinksConfig.General.hasteTicks, AgedDrinksConfig.General.hasteLevel);
		event.getRegistry().register(getRecipeFor("whiskey", new ItemStack(ItemPowder.get(Powder.CHARCOAL), 4)));
		
		RYE_WHISKEY = createAgedDrink("rye_whiskey", MobEffects.HASTE, AgedDrinksConfig.General.hasteTicks, AgedDrinksConfig.General.hasteLevel);
		event.getRegistry().register(getRecipeFor("rye_whiskey", new ItemStack(ItemPowder.get(Powder.LIMONITE),4 )));
		
		CORN_WHISKEY = createAgedDrink("corn_whiskey", MobEffects.HASTE, AgedDrinksConfig.General.hasteTicks, AgedDrinksConfig.General.hasteLevel);
		event.getRegistry().register(getRecipeFor("corn_whiskey", new ItemStack(ItemPowder.get(Powder.SULFUR),4)));
		
		VODKA = createAgedDrink("vodka", MobEffects.RESISTANCE, AgedDrinksConfig.General.resistanceTicks, AgedDrinksConfig.General.resistanceLevel);
		event.getRegistry().register(getRecipeFor("vodka", new ItemStack(ItemPowder.get(Powder.FLUX),4)));
		
		CIDER = createAgedDrink("cider", MobEffects.SPEED, AgedDrinksConfig.General.speedTicks, AgedDrinksConfig.General.speedLevel);
		event.getRegistry().register(getRecipeFor("cider", new ItemStack(ItemPowder.get(Powder.SALT),4)));

		BEER = createAgedDrink("beer", MobEffects.SPEED, AgedDrinksConfig.General.speedTicks, AgedDrinksConfig.General.speedLevel);
		event.getRegistry().register(getRecipeFor("beer", new ItemStack(ItemPowder.get(Powder.MALACHITE),4)));
		
		SAKE = createAgedDrink("sake", MobEffects.STRENGTH, AgedDrinksConfig.General.strengthTicks, AgedDrinksConfig.General.strengthLevel);
		event.getRegistry().register(getRecipeFor("sake", new ItemStack(ItemPowder.get(Powder.LAPIS_LAZULI),4)));
		
	}
}

