package tnfcmod.objects.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerDetector extends BlockContainer
{

    public PlayerDetector()
    {
        super(Material.IRON);
        setHardness(2.0F);
        setTickRandomly(true);
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


    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i)
    {
        return null;
    }
}
