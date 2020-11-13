package com.lumintorious.ambiental.damage;

import net.minecraft.util.DamageSource;

public class ColdDamageSource extends DamageSource
{
    public static final ColdDamageSource INSTANCE = new ColdDamageSource();

    public ColdDamageSource()
    {
        super("tfcambiental.cold");
    }

//	@Override
//	public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
//		if(entityLivingBaseIn.getName() != null) {
//			return new TextComponentString(entityLivingBaseIn.getName() + Util.translate("tfcambiental.hypothermia"));
//		}else {
//			return null;
//		}
//	}

    @Override
    public boolean isDamageAbsolute()
    {
        return true;
    }

}

