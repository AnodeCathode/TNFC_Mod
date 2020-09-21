package com.lumintorious.ambiental.modifiers;


import baubles.api.BaublesApi;
import com.lumintorious.ambiental.capability.TemperatureSystem;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.ItemArmorTFC;
import net.dries007.tfc.objects.items.metal.ItemMetalArmor;
import tnfcmod.objects.items.TNFCItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
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
                ItemArmor thing = (ItemArmor)stack.getItem();
                float modifier = 2f;
                float potency = -0.1f;
                if(stack.getItem() instanceof ItemMetalArmor) {
                    ItemMetalArmor metalarmor = (ItemMetalArmor) stack.getItem();
                    Metal metal = metalarmor.getMetal(stack);
                    if (metal != null){
                        switch(metal.toString()){
                            case "copper":
                                modifier = 2f;
                                potency = -0.1f;
                                break;
                            case "bronze":
                                modifier = 2.5f;
                                potency = 0.2f;
                                break;
                            case "black_bronze":
                                modifier = -2.5f;
                                potency = 0.2f;
                                break;
                            case "bismuth_bronze":
                                modifier = 2.5f;
                                potency = 0.2f;
                                break;
                            case "wrought_iron":
                                modifier = 3.0f;
                                potency = 0.3f;
                                break;
                            case "steel":
                                modifier = 4.0f;
                                potency = 0.4f;
                                break;
                            case "black_steel":
                                modifier = -5.0f;
                                potency = 0.5f;
                                break;
                            case "blue_steel":
                                modifier = 7.0f;
                                potency = 0.5f;
                                break;
                            case "red_steel":
                                modifier = -7.0f;
                                potency = 0.5f;
                                break;

                        }
                    }

                    if (metalarmor.armorType != EntityEquipmentSlot.HEAD)
                    {
                        modifiers.add(new EquipmentModifier("armor_" + metal.toString(), modifier, potency));
                    }
			    } else if(thing.armorType != EntityEquipmentSlot.HEAD) {
                    modifiers.add(new EquipmentModifier("armor_other", modifier, potency));
                }
				if(thing.armorType == EntityEquipmentSlot.HEAD) {
					if(player.world.getLight(player.getPosition()) > 14) {
                        float envTemp = EnvironmentalModifier.getEnvironmentTemperature(player);
						if(envTemp > TemperatureSystem.AVERAGE + 3) {
							modifiers.add(new EquipmentModifier("helmet", - envTemp / 3, -0.4f));
						}
					}
				}
			}

			if(BaublesApi.isBaubleEquipped(player, TNFCItems.leather_tunic) > 0){
                if (modifiers.contains("charcoal_forge")){
                    BaseModifier mod = modifiers.get("charcoal_forge");
                    float temp = mod.getChange();
                    float potency = mod.getPotency();
                    temp = temp / 3;
                    mod.setChange(temp);
                }
            }
		}
	}

}






