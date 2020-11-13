package com.lumintorious.ambiental.damage;

import net.minecraft.util.DamageSource;

public class HeatDamageSource extends DamageSource
{
    public static final HeatDamageSource INSTANCE = new HeatDamageSource();

    public HeatDamageSource()
    {
        super("tfcambiental.heat");
    }

    @Override
    public boolean isDamageAbsolute()
    {
        return true;
    }

}
