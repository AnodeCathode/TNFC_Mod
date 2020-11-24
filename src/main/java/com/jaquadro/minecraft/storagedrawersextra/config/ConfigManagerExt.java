package com.jaquadro.minecraft.storagedrawersextra.config;

import com.jaquadro.minecraft.chameleon.config.ConfigSection;
import com.jaquadro.minecraft.storagedrawersextra.StorageDrawersExtra;
import com.jaquadro.minecraft.storagedrawersextra.block.EnumMod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManagerExt
{
    private final Configuration config;

    private final ConfigSection.Common sectionMaster;
    private final ConfigSection sectionGeneral;
    private final ConfigSection sectionMods;

    private final Map<EnumMod, EnumToggle> modToggleCache = new HashMap<>();

    public ConfigManagerExt (File file) {
        config = new Configuration(file);

        sectionMaster = new ConfigSection.Common(config, StorageDrawersExtra.MOD_ID + ".config.");
        sectionGeneral = new ConfigSection(sectionMaster, "general", "general");
        sectionMods = new ConfigSection(sectionMaster, "mods", "mods");

        for (ConfigSection section : sectionMaster.getSections())
            section.getCategory();

        syncConfig();
    }

    public ConfigSection.Common getSectionMaster () {
        return sectionMaster;
    }

    public void syncConfig () {
        for (EnumMod mod : EnumMod.values()) {
            Property prop = config.get(sectionMods.getQualifiedName(), mod.getName(), "auto", null, new String[] { "auto", "enabled", "disabled" })
                .setLanguageKey(sectionMaster.getLangPrefix() + "mods." + mod.getName()).setRequiresMcRestart(true);
            modToggleCache.put(mod, EnumToggle.fromString(prop.getString()));
        }

        if (config.hasChanged())
            config.save();
    }

    public EnumToggle getModToggleState (EnumMod mod) {
        EnumToggle result = modToggleCache.get(mod);
        if (result == null)
            result = EnumToggle.AUTO;

        return result;
    }
}
