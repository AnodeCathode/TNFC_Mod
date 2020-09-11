package com.lumintorious.ambiental.modifiers;


import com.lumintorious.ambiental.capability.TemperatureSystem;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.ItemArmorTFC;
import net.dries007.tfc.objects.items.metal.ItemMetalArmor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;


public class EquipmentModifier extends BaseModifier{
	
	public EquipmentModifier(String name) {
		super(name);
	}
	
	public EquipmentModifier(String unlocalizedName, float change, float potency) {
		super(unlocalizedName, change, potency);
	}
	
	public static void getModifiers(EntityPlayer player, ModifierStorage modifiers) {
		Iterable<ItemStack> armor = player.getArmorInventoryList();
		for(ItemStack stack : armor) {
			if(stack.getItem() instanceof ItemArmor) {
			    if(stack.getItem() instanceof ItemArmorTFC) {
			        //got tfc armour. So now we can do stuff :) Check for metal, check for tier
                    float divisor = 0f;
                    float potency = 0f;

                    ItemMetalArmor metalarmor = (ItemMetalArmor) stack.getItem();
                    Metal metal = metalarmor.getMetal(stack);

                    switch(metal.getTier()){

                        case TIER_I:
                            divisor = 6f;
                            potency = 0f;
                        case TIER_II:
                            divisor = 5f;
                            potency = 0.5f;
                        case TIER_III:
                            divisor = 4f;
                            potency = 0.6f;
                        case TIER_IV:
                            divisor = 3.0f;
                            potency = 0.7f;
                        case TIER_V:
                            divisor = 2.0f;
                            potency = 0.8f;
                        case TIER_VI:
                            divisor = 1.0f;
                            potency = 0.9f;

                    }
                    if (metalarmor.armorType != EntityEquipmentSlot.HEAD)
                    {
                        float envTemp = EnvironmentalModifier.getEnvironmentTemperature(player);
                        float targetTemp = TemperatureSystem.getTemperatureFor(player).savedTarget;
                        float playerTemp = TemperatureSystem.getTemperatureFor(player).bodyTemperature;

                        if(playerTemp > TemperatureSystem.AVERAGE)
                        {
                            divisor = - divisor;
                            potency = - potency;
                            float diff = Math.max(playerTemp - TemperatureSystem.AVERAGE, 0.1f) / divisor;
                            modifiers.add(new EquipmentModifier("armor" + metal.getTier().toString(), diff, potency));
                        }
                        else if(playerTemp <  TemperatureSystem.AVERAGE)
                        {
                            float diff = Math.max(playerTemp + TemperatureSystem.AVERAGE, 0.1f) / divisor;
                            modifiers.add(new EquipmentModifier("armor" + metal.getTier().toString(), diff, potency));
                        }

                    }


                }
				ItemArmor thing = (ItemArmor)stack.getItem();
				if(thing.armorType == EntityEquipmentSlot.HEAD) {
					if(player.world.getLight(player.getPosition()) > 14) {
                        float envTemp = EnvironmentalModifier.getEnvironmentTemperature(player);
						if(envTemp > TemperatureSystem.AVERAGE + 3) {
							modifiers.add(new EquipmentModifier("helmet", - envTemp / 3, -0.4f));
						}
					}
				}
			}
		}
	}

}






