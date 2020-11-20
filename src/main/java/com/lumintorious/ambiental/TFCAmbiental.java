package com.lumintorious.ambiental;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.lumintorious.ambiental.capability.ITemperatureCapability;
import com.lumintorious.ambiental.capability.TemperaturePacket;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.DumbStorage;

@Mod(modid = TFCAmbiental.MODID, name = TFCAmbiental.NAME, version = TFCAmbiental.VERSION)
public class TFCAmbiental
{
    public static final String MODID = "tfcambiental";
    public static final String NAME = "TFC Ambiental";
    public static final String VERSION = "1.0";
    
    @Mod.Instance
    public static TFCAmbiental INSTANCE;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new AmbientalHandler());
    	if (event.getSide() == Side.CLIENT)
        {
    		MinecraftForge.EVENT_BUS.register(new GuiRenderer());
        }
    	CapabilityManager.INSTANCE.register(ITemperatureCapability.class, new DumbStorage(), () -> null);
//    	CapabilityManager.INSTANCE.register(TimeExtensionCapability.class, new DumbStorage(), () -> null);
    	
    	
    	TerraFirmaCraft.getNetwork().registerMessage(new TemperaturePacket.Handler(), TemperaturePacket.class, 0, Side.CLIENT);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	
    }
    
}
