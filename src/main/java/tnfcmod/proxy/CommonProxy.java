package tnfcmod.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import tnfcmod.qfc.module.ModuleLoader;
import tnfcmod.qfc.sounds.QuarkSounds;

public class CommonProxy
{
    public void registerItemRenderer(Item item, int meta, String id) {
    }

    public void registerNormalItemRenderer(Item item, int meta, String id) {
    }

    public void preInit(FMLPreInitializationEvent event){
        QuarkSounds.init();
        ModuleLoader.preInit(event);
    }

    public void init(FMLInitializationEvent event)
    {
        ModuleLoader.init(event);
    }

    public void postInit(FMLPostInitializationEvent event){
        ModuleLoader.postInit(event);
    }

    public void serverStarting(FMLServerStartingEvent event)
    {
        ModuleLoader.serverStarting(event);
    }

}
