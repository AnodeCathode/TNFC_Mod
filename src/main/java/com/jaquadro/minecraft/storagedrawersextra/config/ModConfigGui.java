package com.jaquadro.minecraft.storagedrawersextra.config;

import com.jaquadro.minecraft.chameleon.config.ConfigSection;
import com.jaquadro.minecraft.storagedrawersextra.StorageDrawersExtra;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ModConfigGui extends GuiConfig
{
    public ModConfigGui (GuiScreen parent) {
        super(parent, getConfigElements(), StorageDrawersExtra.MOD_ID, false, false, "Storage Drawers Configuration");
    }

    private static List<IConfigElement> getConfigElements () {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        for (ConfigSection section : StorageDrawersExtra.config.getSectionMaster().getSections())
            list.add(new ConfigElement(section.getCategory()));

        return list;
    }
}
