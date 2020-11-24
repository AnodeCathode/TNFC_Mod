package com.jaquadro.minecraft.storagedrawersextra.core;

import com.jaquadro.minecraft.storagedrawersextra.config.ModConfigGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class ModGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize (Minecraft minecraftInstance) { }

    @Override
    public boolean hasConfigGui () {
        return true;
    }

    @Override
    public GuiScreen createConfigGui (GuiScreen parentScreen) {
        return new ModConfigGui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories () {
        return null;
    }
}
