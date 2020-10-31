package mods.immibis.core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import mods.immibis.core.api.util.Dir;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockCombined extends BlockContainer {
	
	private static Random random = new Random();
	
	public ItemCombined item;
	//public final String texfile;

	@Override
    public int damageDropped(int meta) {
		return meta;
	}
	
	public BlockCombined(Material m) {
		super(m);
		
		setCreativeTab(CreativeTabs.tabMisc);
		setHardness(2.0F);
	}
	
	@Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase player, ItemStack stack)
    {
		TileCombined te = (TileCombined)w.getTileEntity(x, y, z);
        
		Vec3 look = player.getLook(1.0f);
		
        double absx = Math.abs(look.xCoord);
        double absy = Math.abs(look.yCoord);
        double absz = Math.abs(look.zCoord);
        
        if(absx > absy && absx > absz) {
        	if(look.xCoord < 0)
        		te.onPlaced(player, Dir.NX);
        	else
        		te.onPlaced(player, Dir.PX);
        } else if(absy > absz) {
        	if(look.yCoord < 0)
        		te.onPlaced(player, Dir.NY);
        	else
        		te.onPlaced(player, Dir.PY);
        } else {
        	if(look.zCoord < 0)
        		te.onPlaced(player, Dir.NZ);
        	else
        		te.onPlaced(player, Dir.PZ);
        }
    }
	
	@Override
	public boolean canProvidePower() {
        return true;
    }
	
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int i) {
		TileCombined te = (TileCombined)world.getTileEntity(x, y, z);
		if(te != null)
			return te.redstone_output;
		return 0;
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int i) {
		return isProvidingStrongPower(world, x, y, z, i);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int l, int i1)
	{
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		list.add(new ItemStack(this, 1, l));
		return list;
	}
	
	public List<ItemStack> getInventoryDrops(World world, int x, int y, int z) {
		TileCombined te = (TileCombined)world.getTileEntity(x, y, z);
		if(te == null)
			return Collections.emptyList();
		return te.getInventoryDrops();
	}
	
	@Override
	public int getRenderType() {
		return 0;
	}
	
	@Override
	public void onNeighborBlockChange(World w, int x, int y, int z, Block block)
	{
		if(w.isRemote)
			return;
		TileCombined te = (TileCombined)w.getTileEntity(x, y, z);
		if(te != null)
			te.onBlockNeighbourChange();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if(player.isSneaking())
			return false;
		TileCombined te = (TileCombined)world.getTileEntity(x, y, z);
		if(te != null)
			return te.onBlockActivated(player);
		return false;
    }
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int par6)
	{
		TileCombined te = (TileCombined)world.getTileEntity(x, y, z);
		if(te != null)
			te.onBlockRemoval();
		
		List<ItemStack> drops = getInventoryDrops(world, x, y, z);
		for(ItemStack stack : drops) {
			float xpos = x + random.nextFloat() * 0.8f + 0.1f;
			float ypos = y + random.nextFloat() * 0.8f + 0.1f;
			float zpos = z + random.nextFloat() * 0.8f + 0.1f;
			
			//System.out.println("drop "+stack + " at "+xpos+","+ypos+","+zpos);
			
			// chests do this (multiple drops per stack, 10-30 items at a time)
			int left = stack.stackSize;
			while(left > 0) {
				int removeCount = Math.min(random.nextInt(21) + 10, left);
				left -= removeCount;
				
				EntityItem ent = new EntityItem(world, xpos, ypos, zpos, new ItemStack(stack.getItem(), removeCount, stack.getItemDamage()));
				
				ent.motionX = random.nextGaussian() * 0.05f;
				ent.motionY = random.nextGaussian() * 0.05f + 0.2f;
				ent.motionZ = random.nextGaussian() * 0.05f;
				
				if(stack.hasTagCompound())
					ent.getEntityItem().setTagCompound(stack.getTagCompound());
				
				world.spawnEntityInWorld(ent);
	        }
		}
		super.breakBlock(world, x, y, z, block, par6);
    }
	
	public abstract TileEntity getBlockEntity(int data);
	
	@Override
	public final TileEntity createNewTileEntity(World world, int meta) {
		return getBlockEntity(meta);
	}
	
	public abstract void getCreativeItems(List<ItemStack> is);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public final void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List) {
		getCreativeItems(par3List);
	}
}
