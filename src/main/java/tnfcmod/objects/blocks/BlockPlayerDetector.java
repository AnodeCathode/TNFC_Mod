package tnfcmod.objects.blocks;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.util.Helpers;
import tnfcmod.objects.tiles.TilePlayerDetector;
import tnfcmod.tnfcmod;

import static tnfcmod.tnfcmod.MODID;

public class BlockPlayerDetector extends Block
{

    protected String name;


    public static final PropertyBool ENABLED = PropertyBool.create("enabled");

    public BlockPlayerDetector()
    {
        super(Material.IRON);
        name = "player_detector";
        setHardness(2.0F);
        setTickRandomly(false);
        this.setRegistryName(MODID ,name);
        setTranslationKey(MODID + "." + name);

        setDefaultState(blockState.getBaseState().withProperty(ENABLED, false));

    }


    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return (IBlockState) state.withProperty(ENABLED, Helpers.getTE(world, pos, TilePlayerDetector.class).isEnabled());
    }


    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }


    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        TilePlayerDetector te = (TilePlayerDetector) world.getTileEntity(pos);
        if (te != null)
        {
            return te.redstone_output;
        }
        return 0;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TilePlayerDetector te = Helpers.getTE(worldIn, pos, TilePlayerDetector.class);
        if (!te.isInvalid())
        {
            te.onPlaced(placer);
        }


    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return world.getBlockState(pos).withProperty(ENABLED, true);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ENABLED);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TilePlayerDetector();
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

