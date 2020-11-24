package com.jaquadro.minecraft.storagedrawersextra.client.model;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.CachedBuilderModel;
import com.jaquadro.minecraft.chameleon.model.ChamModel;
import com.jaquadro.minecraft.chameleon.model.ProxyBuilderModel;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.DrawerStateModelData;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import com.jaquadro.minecraft.storagedrawersextra.StorageDrawersExtra;
import com.jaquadro.minecraft.storagedrawersextra.block.BlockExtraDrawers;
import com.jaquadro.minecraft.storagedrawersextra.block.EnumMod;
import com.jaquadro.minecraft.storagedrawersextra.block.EnumVariant;
import com.jaquadro.minecraft.storagedrawersextra.config.ConfigManagerExt;
import com.jaquadro.minecraft.storagedrawersextra.core.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ExtraDrawerModel extends ChamModel
{
    public static class Register extends DefaultRegister
    {
        public Register () {
            super(ModBlocks.extraDrawers);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<IBlockState>();

            for (EnumBasicDrawer drawer : EnumBasicDrawer.values()) {
                for (EnumFacing dir : EnumFacing.HORIZONTALS)
                    states.add(ModBlocks.extraDrawers.getDefaultState().withProperty(BlockStandardDrawers.BLOCK, drawer).withProperty(BlockDrawers.FACING, dir));
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state, IBakedModel existingModel) {
            return new CachedBuilderModel(new Model());
        }

        @Override
        public IBakedModel getModel (ItemStack stack, IBakedModel existingModel) {
            return new Model();
        }

        @Override
        public List<ResourceLocation> getTextureResources () {
            ConfigManagerExt configExt = StorageDrawersExtra.config;
            List<ResourceLocation> resources = new ArrayList<>();

            for (EnumVariant variant : EnumVariant.values()) {
                if (variant == EnumVariant.DEFAULT)
                    continue;

                EnumMod mod = variant.getMod();
                if (mod == null || !mod.isEnabled(configExt.getModToggleState(mod)))
                    continue;

                for (TextureFace face : TextureFace.values())
                    resources.add(face.getLocation(variant));
            }

            return resources;
        }
    }

    public static IBakedModel fromBlock (IBlockState state) {
        if (!(state instanceof IExtendedBlockState))
            return new ExtraDrawerModel(state, false, EnumVariant.DEFAULT);

        IExtendedBlockState xstate = (IExtendedBlockState) state;
        return new ExtraDrawerModel(state, false, xstate.getValue(BlockExtraDrawers.VARIANT));
    }

    public static IBakedModel fromItem (@Nonnull ItemStack stack) {
        IBlockState state = ModBlocks.extraDrawers.getStateFromMeta(stack.getMetadata());
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("material"))
            return new ExtraDrawerModel(state, true, EnumVariant.DEFAULT);

        return new ExtraDrawerModel(state, true, EnumVariant.byResource(stack.getTagCompound().getString("material")));
    }

    private ExtraDrawerModel (IBlockState state, boolean mergeLayers, EnumVariant variant) {
        super(state, mergeLayers, variant);
    }

    @Override
    protected void renderMippedLayer (ChamRender renderer, IBlockState state, Object... args) {
        EnumVariant varient = (EnumVariant) args[0];

        SimpleDrawerRender drawerRender = new SimpleDrawerRender(renderer);
        drawerRender.render(null, state, BlockPos.ORIGIN, state.getValue(BlockDrawers.FACING), varient);
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        IBlockState state = getState();
        if (!(state instanceof IExtendedBlockState))
            return Chameleon.instance.iconRegistry.getIcon(TextureMap.LOCATION_MISSING_TEXTURE);

        IExtendedBlockState xstate = (IExtendedBlockState) state;
        EnumVariant variant = xstate.getValue(BlockExtraDrawers.VARIANT);
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
                IBakedModel mainModel = ExtraDrawerModel.fromBlock(state);
                if (!(state instanceof IExtendedBlockState))
                    return mainModel;

                IExtendedBlockState xstate = (IExtendedBlockState) state;
                DrawerStateModelData stateModel = xstate.getValue(BlockDrawers.STATE_MODEL);

                try {
                    if (!DrawerDecoratorModel.shouldHandleState(stateModel))
                        return mainModel;

                    EnumBasicDrawer drawer = state.getValue(BlockStandardDrawers.BLOCK);
                    EnumFacing dir = state.getValue(BlockDrawers.FACING);

                    return new DrawerDecoratorModel(mainModel, xstate, drawer, dir, stateModel);
                }
                catch (Throwable t) {
                    return mainModel;
                }
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
                IExtendedBlockState xstate = (IExtendedBlockState)state;
                key.add(xstate.getValue(BlockDrawers.STATE_MODEL));
                key.add(xstate.getValue(BlockExtraDrawers.VARIANT));

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
