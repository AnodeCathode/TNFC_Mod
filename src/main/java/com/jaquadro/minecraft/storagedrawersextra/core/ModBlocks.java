package com.jaquadro.minecraft.storagedrawersextra.core;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.resources.ModelRegistry;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawersextra.StorageDrawersExtra;
import com.jaquadro.minecraft.storagedrawersextra.block.BlockExtraDrawers;
import com.jaquadro.minecraft.storagedrawersextra.block.BlockTrimExtra;
import com.jaquadro.minecraft.storagedrawersextra.block.EnumMod;
import com.jaquadro.minecraft.storagedrawersextra.block.EnumVariant;
import com.jaquadro.minecraft.storagedrawersextra.client.model.ExtraDrawerModel;
import com.jaquadro.minecraft.storagedrawersextra.client.model.ExtraTrimModel;
import com.jaquadro.minecraft.storagedrawersextra.config.ConfigManagerExt;
import com.jaquadro.minecraft.storagedrawersextra.item.ItemExtraDrawers;
import com.jaquadro.minecraft.storagedrawersextra.item.ItemTrimExtra;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

public class ModBlocks
{
    public static BlockExtraDrawers extraDrawers;
    public static BlockTrimExtra[] extraTrim;

    @Mod.EventBusSubscriber(modid = StorageDrawersExtra.MOD_ID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks (RegistryEvent.Register<Block> event) {
            IForgeRegistry<Block> registry = event.getRegistry();

            extraDrawers = new BlockExtraDrawers("extra_drawers", "extraDrawers");
            registry.register(extraDrawers);

            extraTrim = new BlockTrimExtra[EnumVariant.groupCount()];
            for (int i = 0; i < extraTrim.length; i++) {
                extraTrim[i] = new BlockTrimExtra("extra_trim_" + i, "extraTrim" + i, i);
                registry.register(extraTrim[i]);
            }
        }

        @SubscribeEvent
        public static void registerItems (RegistryEvent.Register<Item> event) {
            IForgeRegistry<Item> registry = event.getRegistry();

            registry.register(new ItemExtraDrawers(extraDrawers).setRegistryName(extraDrawers.getRegistryName()));
            for (BlockTrimExtra trim : extraTrim)
                registry.register(new ItemTrimExtra(trim).setRegistryName(trim.getRegistryName()));

            for (String key : new String[] { "drawerBasic" })
                OreDictionary.registerOre(key, new ItemStack(extraDrawers, 1, OreDictionary.WILDCARD_VALUE));
            for (String key : new String[] { "drawerTrim" }) {
                for (BlockTrimExtra block : extraTrim)
                    OreDictionary.registerOre(key, new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
            }
        }

        private static final ResourceLocation EMPTY_GROUP = new ResourceLocation("", "");

        @Nonnull
        public static ItemStack makeBasicDrawerItemStack (EnumBasicDrawer info, String material, int count) {
            @Nonnull ItemStack stack = new ItemStack(ModBlocks.extraDrawers, count, info.getMetadata());

            NBTTagCompound data = new NBTTagCompound();
            data.setString("material", material);
            stack.setTagCompound(data);

            return stack;
        }

        @SubscribeEvent
        public static void registerRecipes (RegistryEvent.Register<IRecipe> event) {
            IForgeRegistry<IRecipe> registry = event.getRegistry();
            ConfigManager config = StorageDrawers.config;
            ConfigManagerExt configExt = StorageDrawersExtra.config;

            for (EnumVariant variant : EnumVariant.values()) {
                if (variant == EnumVariant.DEFAULT)
                    continue;

                EnumMod mod = variant.getMod();
                if (mod == null || !mod.isEnabled(configExt.getModToggleState(mod)))
                    continue;

                @Nonnull ItemStack plankStack = ItemStack.EMPTY;
                if (variant.getPlankResource() != null) {
                    Block block = Block.getBlockFromName(variant.getPlankResource().toString());
                    if (block != null)
                        plankStack = new ItemStack(block, 1, variant.getPlankMeta());
                }

                @Nonnull ItemStack slabStack = ItemStack.EMPTY;
                if (variant.getSlabResource() != null) {
                    Block block = Block.getBlockFromName(variant.getSlabResource().toString());
                    if (block != null)
                        slabStack = new ItemStack(block, 1, variant.getSlabMeta());
                }

                String material = variant.getResource().toString();

                if (config.isBlockEnabled(EnumBasicDrawer.FULL1.getUnlocalizedName()) && !plankStack.isEmpty()) {
                    @Nonnull ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL1, material, config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xxx", " y ", "xxx", 'x', plankStack, 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.FULL1.getUnlocalizedName() + "_" + variant.toString()));
                }
                if (config.isBlockEnabled(EnumBasicDrawer.FULL2.getUnlocalizedName()) && !plankStack.isEmpty()) {
                    @Nonnull ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL2, material, config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xyx", "xxx", "xyx", 'x', plankStack, 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.FULL2.getUnlocalizedName() + "_" + variant.toString()));
                }
                if (config.isBlockEnabled(EnumBasicDrawer.FULL4.getUnlocalizedName()) && !plankStack.isEmpty()) {
                    @Nonnull ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL4, material, config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "yxy", "xxx", "yxy", 'x', plankStack, 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.FULL4.getUnlocalizedName() + "_" + variant.toString()));
                }
                if (config.isBlockEnabled(EnumBasicDrawer.HALF2.getUnlocalizedName()) && !slabStack.isEmpty()) {
                    @Nonnull ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF2, material, config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xyx", "xxx", "xyx", 'x', slabStack, 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.HALF2.getUnlocalizedName() + "_" + variant.toString()));
                }
                if (config.isBlockEnabled(EnumBasicDrawer.HALF4.getUnlocalizedName()) && !slabStack.isEmpty()) {
                    @Nonnull ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF4, material, config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "yxy", "xxx", "yxy", 'x', slabStack, 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.HALF4.getUnlocalizedName() + "_" + variant.toString()));
                }
                if (config.isBlockEnabled("trim") && !plankStack.isEmpty()) {
                    @Nonnull ItemStack result = new ItemStack(ModBlocks.extraTrim[variant.getGroupIndex()], config.getBlockRecipeOutput("trim"), variant.getGroupMeta());
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xyx", "yyy", "xyx", 'x', "stickWood", 'y', plankStack)
                        .setRegistryName(result.getItem().getRegistryName() + "_" + variant.toString()));
                }
            }
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void registerModels (ModelRegistryEvent event) {
            extraDrawers.initDynamic();

            ModelRegistry modelRegistry = Chameleon.instance.modelRegistry;

            modelRegistry.registerModel(new ExtraDrawerModel.Register());
            for (BlockTrimExtra block : extraTrim)
                modelRegistry.registerModel(new ExtraTrimModel.Register(block));
        }
    }
}
