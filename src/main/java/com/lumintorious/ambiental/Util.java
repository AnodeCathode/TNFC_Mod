package com.lumintorious.ambiental;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public abstract class Util {
	public static final float bound(float num, float min, float max){
		return Math.min(max, Math.max(min, num));
	}
	
	public static final String translate(String key) {
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			return I18n.format(key);
		}else {
			return key;
		}
	}

}
