package com.jaquadro.minecraft.storagedrawerstfc.block;

import com.jaquadro.minecraft.storagedrawerstfc.StorageDrawersTFC;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

public enum EnumVariant implements IStringSerializable
{
    DEFAULT(StorageDrawersTFC.MOD_ID, "default", 0, null, 0),

    IMMENG_TREATED(ID.IMMENG, "immeng_treated", 1, "treatedWood", 0, "treatedWoodSlab", 0),
    TFC_ACACIA(ID.TFC, "acacia", 2, "wood/planks/acacia", 0, "slab/wood/acacia", 0),
    TFC_ASH(ID.TFC, "ash", 3,  "wood/planks/ash", 0, "slab/wood/ash", 0),
    TFC_ASPEN(ID.TFC, "aspen", 4,  "wood/planks/aspen", 0, "slab/wood/aspen", 0),
    TFC_BIRCH(ID.TFC, "birch", 5,  "wood/planks/birch", 0, "slab/wood/birch", 0),
    TFC_BLACKWOOD(ID.TFC, "blackwood", 6,  "wood/planks/blackwood", 0, "slab/wood/blackwood", 0),
    TFC_CHESTNUT(ID.TFC, "chestnut", 7,  "wood/planks/chestnut", 0, "slab/wood/chestnut", 0),
    TFC_DOUGLAS_FIR(ID.TFC, "douglas_fir", 8,  "wood/planks/douglas_fir", 0, "slab/wood/douglas_fir", 0),
    TFC_HICKORY(ID.TFC, "hickory", 9,  "wood/planks/hickory", 0, "slab/wood/hickory", 0),
    TFC_KAPOK(ID.TFC, "kapok", 10,  "wood/planks/kapok", 0, "slab/wood/kapok", 0),
    TFC_MAPLE(ID.TFC, "maple", 11,  "wood/planks/maple", 0, "slab/wood/maple", 0),
    TFC_OAK (ID.TFC, "oak", 12,  "wood/planks/oak", 0, "slab/wood/oak", 0),
    TFC_PALM(ID.TFC, "palm", 13,  "wood/planks/palm", 0, "slab/wood/palm", 0),
    TFC_PINE(ID.TFC, "pine", 14,  "wood/planks/pine", 0, "slab/wood/pine", 0),
    TFC_ROSEWOOD(ID.TFC, "rosewood", 15,  "wood/planks/rosewood", 0, "slab/wood/rosewood", 0),
    TFC_SEQUOIA(ID.TFC, "sequoia", 16,  "wood/planks/sequoia", 0, "slab/wood/sequoia", 0),
    TFC_SPRUCE(ID.TFC, "spruce", 17,  "wood/planks/spruce", 0, "slab/wood/spruce", 0),
    TFC_SYCAMORE(ID.TFC, "sycamore", 18,  "wood/planks/sycamore", 0, "slab/wood/sycamore", 0),
    TFC_WHITE_CEDAR(ID.TFC, "white_cedar", 19,  "wood/planks/white_cedar", 0, "slab/wood/white_cedar", 0),
    TFC_WILLOW(ID.TFC, "willow", 20,  "wood/planks/willow", 0, "slab/wood/willow", 0),
    TFC_HEVEA(ID.TFC, "hevea", 21,  "wood/planks/hevea", 0, "slab/wood/hevea", 0)
    ;

    private static final Map<ResourceLocation, EnumVariant> RESOURCE_LOOKUP;
    private static final Map<Integer, EnumVariant> INDEX_LOOKUP;

    private final String domain;
    private final ResourceLocation resource;
    private final ResourceLocation plankResource;
    private final ResourceLocation slabResource;
    private final int index;
    private final int plankMeta;
    private final int slabMeta;

    EnumVariant (String domain, String name, int index, String blockId, int blockMeta) {
        this(domain, name, index, blockId, blockMeta, null, 0);
    }

    EnumVariant (String domain, String name, int index, String plankId, int plankMeta, String slabId, int slabMeta) {
        this.domain = domain;
        this.plankResource = plankId != null ? new ResourceLocation(domain, plankId) : null;
        this.slabResource = slabId != null ? new ResourceLocation(domain, slabId) : null;
        this.plankMeta = plankMeta;
        this.slabMeta = slabMeta;
        this.resource = new ResourceLocation(domain, name);
        this.index = index;
    }

    @Nonnull
    public String getDomain () {
        return resource.getNamespace();
    }

    @Nonnull
    public String getPath () {
        return resource.getPath();
    }

    @Override
    @Nonnull
    public String getName () {
        return resource.toString();
    }

    public EnumMod getMod () {
        return EnumMod.byId(domain);
    }

    @Nonnull
    public ResourceLocation getResource () {
        return resource;
    }

    public ResourceLocation getPlankResource () {
        return plankResource;
    }

    public int getPlankMeta () {
        return plankMeta;
    }

    public ResourceLocation getSlabResource () {
        return slabResource;
    }

    public int getSlabMeta () {
        return slabMeta;
    }

    public int getIndex () {
        return index;
    }

    public int getGroupIndex () {
        return index / 16;
    }

    public int getGroupMeta () {
        return index % 16;
    }

    @Nonnull
    public static EnumVariant byResource (String resource) {
        EnumVariant varient = RESOURCE_LOOKUP.get(new ResourceLocation(resource));
        return varient != null ? varient : DEFAULT;
    }

    @Nonnull
    public static EnumVariant byGroupMeta (int group, int meta) {
        EnumVariant variant = INDEX_LOOKUP.get(group * 16 + meta);
        return variant != null ? variant : DEFAULT;
    }

    public static int groupCount () {
        return (values().length - 1) / 16 + 1;
    }

    static {
        RESOURCE_LOOKUP = new HashMap<>();
        INDEX_LOOKUP = new HashMap<>();

        for (EnumVariant variant : values()) {
            RESOURCE_LOOKUP.put(variant.getResource(), variant);
            INDEX_LOOKUP.put(variant.getIndex(), variant);
        }
    }

    private static class ID {
        public static final String IMMENG = "immersiveengineering";
        public static final String TFC = "tfc";
    }
}
