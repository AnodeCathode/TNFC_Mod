package com.lumintorious.ambiental.damage;

import com.lumintorious.ambiental.Util;

import net.dries007.tfc.util.DamageSourcesTFC;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class HeatDamageSource extends DamageSource{
	public static final HeatDamageSource INSTANCE = new HeatDamageSource();

	public HeatDamageSource() {
		super("tfcambiental.heat");
	}
	
	@Override
	public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
		if(entityLivingBaseIn.getName() != null) {
			return new TextComponentString(entityLivingBaseIn.getName() + Util.translate("tfcambiental.hyperthermia"));
		}else {
			return null;
		}
	}
	
	@Override
	public boolean isDamageAbsolute() {
		return true;
	}

}
