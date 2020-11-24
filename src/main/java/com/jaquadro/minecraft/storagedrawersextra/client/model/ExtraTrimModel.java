package com.jaquadro.minecraft.storagedrawersextra.client.model;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.CachedBuilderModel;
import com.jaquadro.minecraft.chameleon.model.ChamModel;
import com.jaquadro.minecraft.chameleon.model.ProxyBuilderModel;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.render.helpers.ModularBoxRenderer;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawersextra.StorageDrawersExtra;
import com.jaquadro.minecraft.storagedrawersextra.block.BlockTrimExtra;
import com.jaquadro.minecraft.storagedrawersextra.block.EnumMod;
import com.jaquadro.minecraft.storagedrawersextra.block.EnumVariant;
import com.jaquadro.minecraft.storagedrawersextra.config.ConfigManagerExt;
import com.jaquadro.minecraft.storagedrawersextra.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ExtraTrimModel extends ChamModel
{
    public static class Register extends DefaultRegister<BlockTrimExtra>
    {
        public Register (BlockTrimExtra block) {
            super(block);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<>();

            for (int i = 0; i < 16; i++) {
                EnumVariant varient = EnumVariant.byGroupMeta(getBlock().getGroup(), i);
                if (varient != EnumVariant.DEFAULT)
                    states.add(getBlock().getDefaultState().withProperty(BlockTrimExtra.META, i));
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state, IBakedModel existingModel) {
            return new CachedBuilderModel(new Model(), state);
        }

        @Override
        public IBakedModel getModel (ItemStack stack, IBakedModel existingModel) {
            return new Model();
        }

        @Override
        public List<ResourceLocation> getTextureResources () {
            ConfigManagerExt configExt = StorageDrawersExtra.config;
            List<ResourceLocation> resources = new ArrayList<>();

            for (int i = 0; i < 16; i++) {
                EnumVariant variant = EnumVariant.byGroupMeta(getBlock().getGroup(), i);
                if (variant == EnumVariant.DEFAULT)
                    continue;

                EnumMod mod = variant.getMod();
                if (mod == null || !mod.isEnabled(configExt.getModToggleState(mod)))
                    continue;

                String path = "blocks/" + variant.getDomain() + "/drawers_" + variant.getPath() + "_side";
                resources.add(new ResourceLocation(StorageDrawersExtra.MOD_ID, path));
            }

            return resources;
        }
    }

    public static IBakedModel fromBlock (IBlockState state) {
        if (!(state.getBlock() instanceof BlockTrimExtra))
            return new ExtraTrimModel(state, false, EnumVariant.DEFAULT);

        BlockTrimExtra block = (BlockTrimExtra) state.getBlock();
        return new ExtraTrimModel(state, false, EnumVariant.byGroupMeta(block.getGroup(), state.getValue(BlockTrimExtra.META)));
    }

    public static IBakedModel fromItem (@Nonnull ItemStack stack) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof BlockTrimExtra))
            return new ExtraTrimModel(ModBlocks.extraTrim[0].getDefaultState(), true, EnumVariant.DEFAULT);

        BlockTrimExtra blockTrim = (BlockTrimExtra) block;
        IBlockState state = blockTrim.getStateFromMeta(stack.getMetadata());

        return new ExtraTrimModel(state, false, EnumVariant.byGroupMeta(blockTrim.getGroup(), state.getValue(BlockTrimExtra.META)));
    }

    public ExtraTrimModel (IBlockState state, boolean mergeLayers, Object... args) {
        super(state, mergeLayers, args);
    }

    @Override
    protected void renderSolidLayer (ChamRender renderer, IBlockState state, Object... args) {
        EnumVariant varient = (EnumVariant) args[0];

        ModularBoxRenderer boxRenderer = new ModularBoxRenderer(renderer);
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        boxRenderer.setExteriorIcon(Chameleon.instance.iconRegistry.getIcon(TextureFace.SIDE.getLocation(varient)));

        renderer.targetFaceGroup(true);
        boxRenderer.renderSolidBox(null, state, BlockPos.ORIGIN, 0, 0, 0, 1, 1, 1);
        renderer.targetFaceGroup(false);
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        IBlockState state = getState();
        if (!(state.getBlock() instanceof BlockTrimExtra))
            return Chameleon.instance.iconRegistry.getIcon(TextureMap.LOCATION_MISSING_TEXTURE);

        BlockTrimExtra block = (BlockTrimExtra) state.getBlock();
        EnumVariant variant = EnumVariant.byGroupMeta(block.getGroup(), state.getValue(BlockTrimExtra.META));
        return Chameleon.instance.iconRegistry.getIcon(TextureFace.SIDE.getLocation(variant));
    }

    private static class Model extends ProxyBuilderModel
    {
        public Model () {
            super(Chameleon.instance.iconRegistry.getIcon(new ResourceLocation(StorageDrawers.MOD_ID, "blocks/base/base_default")));
        }

        @Override
        protected IBakedModel buildModel (IBlockState state, IBakedModel parent) {
            try {
                return fromBlock(state);
            }
            catch (Throwable t) {
                return parent;
            }
        }

        @Override
        public ItemOverrideList getOverrides () {
            return itemHandler;
        }

        @Override
        public List<Object> getKey (IBlockState state) {
            try {
                List<Object> key = new ArrayList<>();
                key.add(state.getValue(BlockTrimExtra.META));

                return key;
            }
            catch (Throwable t) {
                return super.getKey(state);
            }
        }
    }

    private static class ItemHandler extends ItemOverrideList
    {
        public ItemHandler () {
            super(ImmutableList.<ItemOverride>of());
        }

        @Override
        public IBakedModel handleItemState (IBakedModel originalModel, @Nonnull ItemStack stack, World world, EntityLivingBase entity) {
            return fromItem(stack);
        }
    }

    private static final ItemHandler itemHandler = new ItemHandler();
}
