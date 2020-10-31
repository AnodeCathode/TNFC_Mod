package com.lumintorious.ambiental.damage;

import com.lumintorious.ambiental.Util;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ColdDamageSource extends DamageSource{
	public static final ColdDamageSource INSTANCE = new ColdDamageSource();

	public ColdDamageSource() {
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
	public boolean isDamageAbsolute() {
		return true;
	}

}

