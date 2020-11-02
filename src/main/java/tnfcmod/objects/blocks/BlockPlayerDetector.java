package tnfcmod.objects.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tnfcmod.tnfcmod;

import static tnfcmod.tnfcmod.MODID;

public class BlockPlayerDetector extends BlockContainer
{
    protected String name;


    public static final PropertyBool ENABLED = PropertyBool.create("enabled");

    public BlockPlayerDetector()
    {

        super(Material.IRON);
        name = "player_detector";
        setHardness(2.0F);
        setTickRandomly(true);
        this.setRegistryName(MODID ,name);
        setTranslationKey(MODID + "." + name);

        setDefaultState(blockState.getBaseState().withProperty(ENABLED, false));

    }

    @Override
    public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state,EntityLivingBase player, ItemStack stack)
    {

    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return null;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }




    @Override
    public void breakBlock(World world, BlockPos pos , IBlockState state)
    {

    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ENABLED);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i)
    {
        return null;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()

            .withProperty(ENABLED, meta == 1);
    }
    @Override

    public int getMetaFromState(IBlockState state) {
        return (state.getValue(ENABLED) ? 1: 0);
    }


    public void registerItemModel() {

        tnfcmod.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, name);
    }
}
