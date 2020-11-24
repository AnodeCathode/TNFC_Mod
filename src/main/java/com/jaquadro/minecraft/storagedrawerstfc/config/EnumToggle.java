package com.jaquadro.minecraft.storagedrawerstfc.config;

public enum EnumToggle
{
    AUTO, ENABLED, DISABLED;

    public static EnumToggle fromString (String value) {
        if ("enabled".equals(value))
            return ENABLED;
        if ("disabled".equals(value))
            return DISABLED;

        return AUTO;
    }
}
