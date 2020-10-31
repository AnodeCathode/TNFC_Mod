package mods.immibis.tubestuff;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStorage extends Block {
	
	private IIcon[] icons = new IIcon[16];
	
	public BlockStorage() {
		super(Material.iron);
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundTypeMetal);
		
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		icons[0] = reg.registerIcon(R.block.storageSilver);
		icons[1] = reg.registerIcon(R.block.storageTin);
		icons[2] = reg.registerIcon(R.block.storageCopper);
		icons[3] = reg.registerIcon(R.block.storageNikolite);
		icons[4] = reg.registerIcon(R.block.storageCoal);
		icons[5] = reg.registerIcon(R.block.storageRedstone);
		icons[6] = reg.registerIcon(R.block.storageBlueAlloy);
		icons[7] = reg.registerIcon(R.block.storageRedAlloy);
		icons[8] = reg.registerIcon(R.block.storageBrass);
		icons[9] = reg.registerIcon(R.block.storageCharcoal);
	}
	
	@Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
    	if(canFall(par1World.getBlockMetadata(par2, par3, par4)))
    		par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate());
    }

	@Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
    	if(canFall(par1World.getBlockMetadata(par2, par3, par4)))
        	par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate());
    }

    private boolean canFall(int meta) {
		return meta == 3 || meta == 5; // nikolite or redstone
	}

	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
		if(canFall(par1World.getBlockMetadata(par2, par3, par4)))
		   tryToFall(par1World, par2, par3, par4);
    }

    private void tryToFall(World par1World, int par2, int par3, int par4) {
        if (BlockFalling.func_149831_e(par1World, par2, par3 - 1, par4) && par3 >= 0)
        {
        	int meta = par1World.getBlockMetadata(par2, par3, par4);
        	
            if (TubeStuff.enableSlowFalling && !BlockSand.fallInstantly && par1World.checkChunksExist(par2 - 32, par3 - 32, par4 - 32, par2 + 32, par3 + 32, par4 + 32))
            {
                if (!par1World.isRemote)
                {
                    EntityFallingBlock var9 = new EntityFallingBlock(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), this, par1World.getBlockMetadata(par2, par3, par4));
                    par1World.spawnEntityInWorld(var9);
                }
            } else {
	
            	par1World.setBlock(par2, par3, par4, Blocks.air, 0, 2);
                
                while (BlockFalling.func_149831_e(par1World, par2, par3 - 1, par4) && par3 > 0)
	                --par3;
	
	            if (par3 > 0)
	                par1World.setBlock(par2, par3, par4, this, meta, 2);
            }
        }
    }

    private int tickRate() {
        return 3;
    }
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	@Override
	public IIcon getIcon(int side, int data) {
		return icons[data];
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public final void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List l) {
		for(int k = 0; k < 10; k++)
			l.add(new ItemStack(this, 1, k));
	}

}
