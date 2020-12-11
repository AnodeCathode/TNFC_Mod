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

    @Config.Comment("General configuration")
    @Config.LangKey("config." + MODID + ".general")
    public static ConfigTNFCMod.General GENERAL = new ConfigTNFCMod.General();


    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID))
        {
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
        }
    }

    public static class General
    {
        @Config.Comment({"Number of ticks for a chunk to be frozen?"})
        @Config.LangKey("config." + MODID + ".frozentime")
        @Config.RequiresMcRestart
        public int frozentime = 6912000;

        @Config.Comment({"Chance for sapling drop with elite shears?"})
        @Config.LangKey("config." + MODID + ".saplingdropchance")
        @Config.RangeDouble(
            min = 0.0D,
            max = 1.0D
        )
        public double  saplingdropchance = 0.1;

        @Config.Comment({"Smithing skill controls whether numeric temp display?"})
        @Config.LangKey("config." + MODID + ".skillbaseTempDisplay")
        public boolean skillbasedTempDisplay= true;


        @Config.Comment({"Weight for spawning of crabs?"})
        @Config.LangKey("config." + MODID + ".crabweight")
        @Config.RequiresMcRestart
        public int crabweight = 400;

        @Config.Comment({"Weight for spawning of crabs?"})
        @Config.LangKey("config." + MODID + ".crabfreq")
        @Config.RequiresMcRestart
        public int crabfreq = 40;


        @Config.Comment({"Weight for spawning of frogs?"})
        @Config.LangKey("config." + MODID + ".frogweight")
        @Config.RequiresMcRestart
        public int frogweight = 400;

        @Config.Comment({"Frequency for spawning of frogs?"})
        @Config.LangKey("config." + MODID + ".frogfreq")
        @Config.RequiresMcRestart
        public int frogfreq = 40;
    }


}
