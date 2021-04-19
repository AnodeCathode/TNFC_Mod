package com.lumintorious.ambiental.modifiers;


import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import baubles.api.BaublesApi;
import com.lumintorious.ambiental.capability.TemperatureCapability;
import me.desht.pneumaticcraft.common.item.ItemPneumaticArmor;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.metal.ItemMetalArmor;
import tnfcmod.objects.items.TNFCItems;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;


public class EquipmentModifier extends BaseModifier
{

    public EquipmentModifier(String name)
    {
        super(name);
    }

    public EquipmentModifier(String unlocalizedName, float change, float potency)
    {
        super(unlocalizedName, change, potency);
    }

    public static void getModifiers(EntityPlayer player, ModifierStorage modifiers)
    {
        int superArmourCount = 0;
        Iterable<ItemStack> armor = player.getArmorInventoryList();
        for (ItemStack stack : armor)
        {

            if (stack.getItem() instanceof ItemArmor)
            {
                if (stack.getItem() instanceof ItemSpaceArmor || stack.getItem() instanceof ItemPneumaticArmor)
                {

                    superArmourCount += 1;
                }

                ItemArmor thing = (ItemArmor) stack.getItem();
                float modifier = 2f;
                float potency = -0.1f;
                if (stack.getItem() instanceof ItemMetalArmor)
                {
                    ItemMetalArmor metalarmor = (ItemMetalArmor) stack.getItem();
                    Metal metal = metalarmor.getMetal(stack);
                    if (metal != null)
                    {
                        switch (metal.toString())
                        {
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
                            case "tungsten_steel":
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
                }
                else if (thing.armorType != EntityEquipmentSlot.HEAD)
                {
                    modifiers.add(new EquipmentModifier("armor_other", modifier, potency));
                }
                if (thing.armorType == EntityEquipmentSlot.HEAD)
                {
                    if (player.world.getLight(player.getPosition()) > 14)
                    {
                        float envTemp = EnvironmentalModifier.getEnvironmentTemperature(player);
                        if (envTemp > TemperatureCapability.AVERAGE + 3)
                        {
                            modifiers.add(new EquipmentModifier("helmet", -envTemp / 3, -0.4f));
                        }
                    }
                }
            }
            if (stack.hasTagCompound())
            {
                if (stack.getTagCompound().hasKey("ncRadiationResistance"))
                {
                    modifiers.add(new EquipmentModifier("radshielding", 2, 0.1f));
                }
            }
        }
        if (superArmourCount == 4)
        {
            //Player wearing complete space suit. Remove all the nasty modifiers
            // Should check for aircon addon for PnC Armour and some oxygen for Spacesuit
            Iterator<BaseModifier> it = modifiers.iterator();
            while (it.hasNext())
            {
                BaseModifier mod = it.next();
                if (mod.getUnlocalizedName() == "environment")
                {
                    mod.setChange(15f);
                }
                else
                {
                   mod.setChange(0f);
                }

            }
        }

        if (BaublesApi.isBaubleEquipped(player, TNFCItems.leather_tunic) > 0)
        {
            if (modifiers.contains("charcoal_forge"))
            {
                BaseModifier mod = modifiers.get("charcoal_forge");
                float temp = mod.getChange();
                temp = temp / 3;
                mod.setChange(temp);
            }
        }
    }

}






