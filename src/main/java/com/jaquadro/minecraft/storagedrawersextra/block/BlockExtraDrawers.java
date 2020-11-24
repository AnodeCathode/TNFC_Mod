package com.jaquadro.minecraft.storagedrawersextra.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawersextra.StorageDrawersExtra;
import com.jaquadro.minecraft.storagedrawersextra.config.ConfigManagerExt;
import com.jaquadro.minecraft.storagedrawersextra.core.ModCreativeTabs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

import javax.annotation.Nonnull;

public class BlockExtraDrawers extends BlockStandardDrawers
{
    public static final IUnlistedProperty<EnumVariant> VARIANT = new Properties.PropertyAdapter<>(PropertyEnum.create("variant", EnumVariant.class));

    public BlockExtraDrawers (String registryName, String blockName) {
        super(registryName, blockName);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    @Nonnull
    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        ItemStack drop = super.getMainDrop(world, pos, state);

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return drop;

        NBTTagCompound data = drop.getTagCompound();
        if (data == null)
            data = new NBTTagCompound();

        IBlockState extended = getExtendedState(state, world, pos);
        if (extended instanceof IExtendedBlockState) {
            EnumVariant variant = ((IExtendedBlockState)extended).getValue(VARIANT);
            data.setString("material", variant.getName());
        }

        drop.setTagCompound(data);
        return drop;
    }

    @Override
    public void getSubBlocks (CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        ConfigManagerExt configExt = StorageDrawersExtra.config;

        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            for (EnumVariant material : EnumVariant.values()) {
                if (material == EnumVariant.DEFAULT)
                    continue;

                EnumMod mod = material.getMod();
                if (mod == null || !mod.isEnabled(configExt.getModToggleState(mod)))
                    continue;

                ItemStack stack = new ItemStack(this, 1, type.getMetadata());

                NBTTagCompound data = new NBTTagCompound();
                data.setString("material", material.getResource().toString());
                stack.setTagCompound(data);

                list.add(stack);
            }
        }
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (!(state instanceof IExtendedBlockState))
            return state;

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState)super.getExtendedState(state, world, pos))
            .withProperty(VARIANT, translateMaterial(tile.getMaterialOrDefault()));
    }

    @Override
    protected BlockStateContainer createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { BLOCK, FACING }, new IUnlistedProperty[] { VARIANT, STATE_MODEL });
    }

    private EnumVariant translateMaterial (String materal) {
        for (EnumVariant type : EnumVariant.values()) {
            if (materal.equals(type.getResource().toString()))
                return type;
        }

        return EnumVariant.DEFAULT;
    }
}
