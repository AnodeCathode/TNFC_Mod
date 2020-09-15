package com.lumintorious.ambiental.modifiers;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityArcFurnace;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityElectricLantern;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFurnaceHeater;
import com.lumintorious.ambiental.capability.TemperatureSystem;

import net.dries007.tfc.objects.te.TEBloomery;
import net.dries007.tfc.objects.te.TECharcoalForge;
import net.dries007.tfc.objects.te.TEFirePit;
import net.dries007.tfc.objects.te.TELamp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityModifier extends BlockModifier{
	
	public TileEntityModifier(String unlocalizedName) {
		super(unlocalizedName);
	}
	
	public TileEntityModifier(String unlocalizedName, float change, float potency) {
		super(unlocalizedName, change, potency);
	}
	
	public TileEntityModifier(String unlocalizedName, float change, float potency, boolean affectedByDistance) {
		super(unlocalizedName, change, potency, affectedByDistance);
	}
	

	public static TileEntityModifier handleCharcoalForge(TileEntity tile, EntityPlayer player) {
		if(tile instanceof TECharcoalForge) {
			TECharcoalForge forge = (TECharcoalForge)tile;
			float temp = forge.getField(TECharcoalForge.FIELD_TEMPERATURE);
			float change =  temp / 140f;
			float potency = temp / 350f;
			return new TileEntityModifier("charcoal_forge", change, potency);
		}else {
			return null;
		}
	}
	
	public static TileEntityModifier handleFirePit(TileEntity tile, EntityPlayer player) {
		if(tile instanceof TEFirePit) {
			TEFirePit pit = (TEFirePit)tile;
			float temp = pit.getField(TEFirePit.FIELD_TEMPERATURE);
			float change =  temp / 100f;
			float potency = temp / 350f;
			return new TileEntityModifier("fire_pit", Math.min(6f, change), potency);
		}else {
			return null;
		}
	}

	public static TileEntityModifier handleFurnace(TileEntity tile, EntityPlayer player) {
        if(tile instanceof TileEntityFurnace)
        {
            TileEntityFurnace furnace = (TileEntityFurnace) tile;
            if (furnace.isBurning())
            {
                float change = 10f;
                float potency = 5f;
                return new TileEntityModifier("furnace", change, potency);
            }
        }
        return null;
    }

    public static TileEntityModifier handleExternalHeater(TileEntity tile, EntityPlayer player) {
        if(tile instanceof TileEntityFurnaceHeater) {
            TileEntityFurnaceHeater heater = (TileEntityFurnaceHeater)tile;
            if (heater.active)
            {
                float change = 20f;
                float potency = 8f;
                return new TileEntityModifier("externalheater", change, potency);
            }
        }
        return null;
    }

    public static TileEntityModifier handleElecLantern(TileEntity tile, EntityPlayer player) {
        if(tile instanceof TileEntityElectricLantern) {
            TileEntityElectricLantern latern = (TileEntityElectricLantern) tile;
            if (latern.active)
            {
                float change = 2f;
                float potency = 0.2f;
                return new TileEntityModifier("electriclight", change, potency);
            }
        }
        return null;
    }


    public static TileEntityModifier handleBloomery(TileEntity tile, EntityPlayer player) {
		if(tile instanceof TEBloomery) {
			TEBloomery bloomery = (TEBloomery)tile;
			float change = bloomery.getRemainingTicks() > 0 ? 4f : 0f;
			float potency = change;
			return new TileEntityModifier("bloomery", change, potency);
		}else {
			return null;
		}
	}
	
	public static TileEntityModifier handleLamps(TileEntity tile, EntityPlayer player) {
		if(tile instanceof TELamp) {
			TELamp lamp = (TELamp)tile;
			if(EnvironmentalModifier.getEnvironmentTemperature(player) < TemperatureSystem.AVERAGE) {
				float change = (lamp.isPowered() && lamp.getFuel() > 0) ? 2f : 0f;
				float potency = 0f;
				return new TileEntityModifier("lamp", change, potency, false);
			}
		}
		return null;
	}
}
