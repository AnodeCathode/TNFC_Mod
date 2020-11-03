package tnfcmod.util;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tnfcmod.tnfcmod.MODID;

@SuppressWarnings("unused")
@Config(modid = MODID, category = "")
@Config.LangKey("config." + MODID)
@Mod.EventBusSubscriber(modid = MODID)
public class ConfigTNFCMod
{
    @Config.Comment("Recipes configuration")
    @Config.LangKey("config." + MODID + ".recipes")
    public static ConfigTNFCMod.Recipes RECIPES = new ConfigTNFCMod.Recipes();


    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID))
        {
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
        }
    }

    public static class Recipes
    {
        @Config.Comment({"Register ardite recipe via bloomery?"})
        @Config.LangKey("config." + MODID + ".recipes.ardite")
        @Config.RequiresMcRestart
        public boolean ardite = true;

        @Config.Comment({"Register aluminum recipe via bloomery?"})
        @Config.LangKey("config." + MODID + ".recipes.aluminum")
        @Config.RequiresMcRestart
        public boolean aluminum = true;

        @Config.Comment({"Register cobalt recipe via bloomery?"})
        @Config.LangKey("config." + MODID + ".recipes.cobalt")
        @Config.RequiresMcRestart
        public boolean cobalt = true;

        @Config.Comment({"Register osmium recipe via blast furnace?"})
        @Config.LangKey("config." + MODID + ".recipes.osmium")
        @Config.RequiresMcRestart
        public boolean osmium = true;

        @Config.Comment({"Register titanium recipe via blast furnace?"})
        @Config.LangKey("config." + MODID + ".recipes.titanium")
        @Config.RequiresMcRestart
        public boolean titanium = true;

        @Config.Comment({"Register tungsten recipe via blast furnace?"})
        @Config.LangKey("config." + MODID + ".recipes.tungsten")
        @Config.RequiresMcRestart
        public boolean tungsten = true;
    }
}
