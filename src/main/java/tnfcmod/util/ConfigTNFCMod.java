package tnfcmod.util;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
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
    }


}
