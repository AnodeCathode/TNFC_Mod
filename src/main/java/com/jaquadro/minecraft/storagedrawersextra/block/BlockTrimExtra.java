package com.jaquadro.minecraft.storagedrawersextra.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawersextra.StorageDrawersExtra;
import com.jaquadro.minecraft.storagedrawersextra.config.ConfigManagerExt;
import com.jaquadro.minecraft.storagedrawersextra.core.ModCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BlockTrimExtra extends Block implements INetworked
{
    public static final PropertyInteger META = PropertyInteger.create("meta", 0, 15);
    public static final IUnlistedProperty<EnumVariant> VARIANT = new Properties.PropertyAdapter<>(PropertyEnum.create("variant", EnumVariant.class));

    private final int group;

    public BlockTrimExtra (String registryName, String blockName, int group) {
        super(Material.WOOD);

        this.group = group;

        setUnlocalizedName(blockName);
        setRegistryName(registryName);
        setHardness(5f);
        setSoundType(SoundType.WOOD);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);

        setDefaultState(blockState.getBaseState().withProperty(META, 0));
    }

    public int getGroup () {
        return group;
    }

    @Override
    public boolean removedByPlayer (IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, true);
    }

    @Override
    public void harvestBlock (World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, @Nonnull ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
    }

    @Override
    public List<ItemStack> getDrops (IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack dropStack = new ItemStack(Item.getItemFromBlock(this), 1, state.getBlock().getMetaFromState(state));

        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(dropStack);

        return drops;
    }

    @Override
    public void getSubBlocks (CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        ConfigManagerExt configExt = StorageDrawersExtra.config;

        for (EnumVariant variant : EnumVariant.values()) {
            if (variant == EnumVariant.DEFAULT)
                continue;

            EnumMod mod = variant.getMod();
            if (mod == null || !mod.isEnabled(configExt.getModToggleState(mod)))
                continue;

            if (group == variant.getGroupIndex())
                list.add(new ItemStack(this, 1, variant.getGroupMeta()));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta (int meta) {
        return getDefaultState().withProperty(META, meta);
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return state.getValue(META);
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (!(state instanceof IExtendedBlockState))
            return state;

        return ((IExtendedBlockState)state).withProperty(VARIANT, EnumVariant.byGroupMeta(group, state.getValue(META)));
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { META }, new IUnlistedProperty[] { VARIANT });
    }
}
