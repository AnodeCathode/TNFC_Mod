package mods.immibis.core;

import mods.immibis.core.api.FMLModInfo;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(version=ImmibisCore.VERSION, modid=ImmibisCore.MODID, name=ImmibisCore.NAME, dependencies="before:IC2"/*, certificateFingerprint="<none>"*/)
@FMLModInfo(
		url="http://www.minecraftforum.net/topic/1001131-110-immibiss-mods-smp/",
		description="",
		authors="immibis"
		)
public class ICNonCoreMod {
	public boolean disabled = false;
	
	public ICNonCoreMod() {
		if(ImmibisCore.instance != null)
			disabled = true;
		else {
			ImmibisCore.instance = new ImmibisCore();
		}
	}
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent evt) {
		if(!disabled) {
			ImmibisCore.instance.preInit(evt);
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		if(!disabled)
			ImmibisCore.instance.init(evt);
	}
	
	@EventHandler
	public void postinit(FMLPostInitializationEvent evt) {
		if(!disabled)
			ImmibisCore.instance.postInit(evt);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent evt) {
		if(!disabled)
			ImmibisCore.instance.serverStarting(evt);
	}
}
