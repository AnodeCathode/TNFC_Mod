package com.jaquadro.minecraft.storagedrawersextra.client.model;

import com.jaquadro.minecraft.storagedrawersextra.StorageDrawersExtra;
import com.jaquadro.minecraft.storagedrawersextra.block.EnumVariant;
import net.minecraft.util.ResourceLocation;

public enum TextureFace {
    FRONT_1("_front_1"),
    FRONT_2("_front_2"),
    FRONT_4("_front_4"),
    SIDE("_side"),
    SIDE_H("_side_h"),
    SIDE_V("_side_v"),
    TRIM("_trim");

    private final String suffix;

    TextureFace (String suffix) {
        this.suffix = suffix;
    }

    public ResourceLocation getLocation (EnumVariant variant) {
        StringBuilder builder = new StringBuilder("blocks/")
            .append(variant.getDomain())
            .append("/drawers_")
            .append(variant.getPath())
            .append(suffix);

        return new ResourceLocation(StorageDrawersExtra.MOD_ID, builder.toString());
    }
}
