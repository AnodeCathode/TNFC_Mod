package com.jaquadro.minecraft.storagedrawerstfc.block;

import com.jaquadro.minecraft.storagedrawerstfc.config.EnumToggle;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;

public enum EnumMod implements IStringSerializable
{
    TFC ("tfc", EnumVariant.TFC_WILLOW),
    IMMENG("immersiveengineering", EnumVariant.IMMENG_TREATED)
    ;

    private String id;
    private EnumVariant defaultMaterial;

    EnumMod (String modId, EnumVariant defaultMaterial) {
        this.id = modId;
        this.defaultMaterial = defaultMaterial;
    }

    @Override
    @Nonnull
    public String getName () {
        return id;
    }

    public EnumVariant getDefaultMaterial () {
        return defaultMaterial;
    }

    public boolean isLoaded () {
        return Loader.isModLoaded(id);
    }

    public boolean isEnabled (EnumToggle toggle) {
        switch (toggle) {
            case ENABLED:
                return true;
            case DISABLED:
                return false;
            case AUTO:
            default:
                return isLoaded();
        }
    }

    public static EnumMod byId (String id) {
        for (EnumMod mod : values()) {
            if (mod.getName().equals(id))
                return mod;
        }

        return null;
    }
}
