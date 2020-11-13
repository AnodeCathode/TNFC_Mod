package com.lumintorious.ambiental.modifiers;

import com.lumintorious.ambiental.TFCAmbiental;
import com.lumintorious.ambiental.TFCAmbientalConfig;
import com.lumintorious.ambiental.Util;
import com.lumintorious.ambiental.capability.TemperatureSystem;

public class BaseModifier
{
    private String unlocalizedName;
    private float change = 0f;
    private float potency = 0f;
    private int count = 1;
    private float multiplier = 1f;

    public float getMultiplier()
    {
        return multiplier;
    }

    public void setMultiplier(float multiplier)
    {
        this.multiplier = multiplier;
    }

    public void addMultiplier(float multiplier)
    {
        this.setMultiplier(this.getMultiplier() * multiplier);
    }

    public float getChange()
    {
        return change * multiplier * (count == 1 ? 1f : TFCAmbientalConfig.GENERAL.diminishedModifierMultiplier);
    }

    public void setChange(float change)
    {
        this.change = change;
    }

    public float getPotency()
    {
        return potency * multiplier * (count == 1 ? 1f : TFCAmbientalConfig.GENERAL.diminishedModifierMultiplier);
    }

    public void setPotency(float potency)
    {
        this.potency = potency;
    }

    public void addCount()
    {
        count++;
    }

    public void absorb(BaseModifier modifier)
    {
        if (count >= TFCAmbientalConfig.GENERAL.modifierCap)
        {
            return;
        }
        this.count += modifier.count;
        this.change += modifier.change;
        this.potency += modifier.potency;
        this.addMultiplier(modifier.getMultiplier());
    }

    public int getCount()
    {
        return count;
    }

    public String getUnlocalizedName()
    {
        return unlocalizedName;
    }

    public BaseModifier(String unlocalizedName)
    {
        this.unlocalizedName = unlocalizedName;
    }

    public BaseModifier(String unlocalizedName, float change, float potency)
    {
        this.unlocalizedName = unlocalizedName;
        this.change = change;
        this.potency = potency;
    }

    public String getDisplayName()
    {
        return Util.translate(TFCAmbiental.MODID + ".modifier." + this.unlocalizedName);
    }

    public void apply(TemperatureSystem temp)
    {
        // nothing;
    }

    public void cancel(TemperatureSystem temp)
    {
        // nothing;
    }
}
