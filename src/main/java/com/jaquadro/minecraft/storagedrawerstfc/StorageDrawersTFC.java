package com.jaquadro.minecraft.storagedrawerstfc;

import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import com.jaquadro.minecraft.storagedrawerstfc.config.ConfigManagerExt;
import com.jaquadro.minecraft.storagedrawerstfc.core.CommonProxy;
import com.jaquadro.minecraft.storagedrawerstfc.core.ModBlocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.File;

@Mod(modid = StorageDrawersTFC.MOD_ID, name = StorageDrawersTFC.MOD_NAME, version = StorageDrawersTFC.MOD_VERSION,
    dependencies = "required-after:storagedrawers;required-after:chameleon;after:waila;",
    guiFactory = StorageDrawersTFC.SOURCE_PATH + "core.ModGuiFactory")
public class StorageDrawersTFC
{
    public static final String MOD_ID = "storagedrawerstfc";
    public static final String MOD_NAME = "Storage Drawers TFC";
    public static final String MOD_VERSION = "12.2.48";
    public static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawerstfc.";

    public static final ModBlocks blocks = new ModBlocks();

    public static ConfigManagerExt config;

    @Mod.Instance(MOD_ID)
    public static StorageDrawersTFC instance;

    @SidedProxy(clientSide = SOURCE_PATH + "core.ClientProxy", serverSide = SOURCE_PATH + "core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        config = new ConfigManagerExt(new File(event.getModConfigurationDirectory(), MOD_ID + ".cfg"));
    }

    public void init (FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(instance);
    }

    @SubscribeEvent
    public void onConfigChanged (ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MOD_ID))
            config.syncConfig();
    }
}
