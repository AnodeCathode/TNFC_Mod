package mods.immibis.tubestuff;


import java.util.List;
import java.util.Random;

import mods.immibis.core.BlockCombined;
import mods.immibis.core.TileCombined;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTubestuff extends BlockCombined {
	public BlockTubestuff() {
		super(Material.iron);
		setHardness(2.0F);
		setTickRandomly(true);
	}
	
	public static final int META_BUFFER = 0;
	public static final int META_ACT2 = 1;
	public static final int META_BHC = 2;
	public static final int META_INCINERATOR = 3;
	public static final int META_DUPLICATOR = 4;
	// 5 unused (was retrievulator)
	public static final int META_BLOCK_BREAKER = 6;
	public static final int META_LIQUID_INCINERATOR = 7;
	public static final int META_LIQUID_DUPLICATOR = 8;
	public static final int META_ONLINE_DETECTOR = 9;
	public static final int META_MCT2 = 10;
	public static final int META_DEPLOYER = 11;
	
	public static int model;
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		int meta = world.getBlockMetadata(x, y, z);
		if(meta == 6) return false; // block breaker
		return true;
	}
	
	@Override
	public int getRenderType() {
		return model;
	}
	
	@Override @SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
	    int meta = world.getBlockMetadata(x, y, z);
	    
	    if(meta == 2)
	    {
	    	if(SharedProxy.enableBHCParticles)
	    	{
		    	for(int k = 0; k < 1 + world.rand.nextInt(3); k++)
		    	{
		    		EntityFX fx = new EntityBlackHoleFX(world, x + 0.5, y + 0.5, z + 0.5); 
		    		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		    	}
	    	}
	    }
	}
	
	@Override
	public TileEntity getBlockEntity(int data)
	{
		switch(data) {
		case META_BUFFER: return new TileBuffer();
		case META_ACT2: return new TileAutoCraftingMk2();
		case META_BHC: return new TileBlackHoleChest();
		case META_INCINERATOR: return new TileIncinerator();
		case META_DUPLICATOR: return new TileDuplicator();
		case META_BLOCK_BREAKER: return new TileBlockBreaker();
		case META_LIQUID_INCINERATOR: return new TileLiquidIncinerator();
		case META_LIQUID_DUPLICATOR: return new TileLiquidDuplicator();
		case META_ONLINE_DETECTOR: return new TileOnlineDetector();
		case META_MCT2: return new TileMCT2();
		case META_DEPLOYER: return new TileDeployer();
		default: return null;
		}
	}
	
	private IIcon iBuffer, iACTTop, iACTBottom, iACTSide, iBHC, iIncineratorTop, iIncineratorSide;
	private IIcon iDuplicator;
	public static IIcon iBreakerFrame, iBreakerFrameX; // used in BlockRenderer
	private IIcon iDetectorOff, iDetectorOn;
	private IIcon iDepTop, iDepBottom, iDepSide;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		iBuffer = reg.registerIcon(R.block.buffer);
		iACTTop = reg.registerIcon(R.block.acttop);
		iACTBottom = reg.registerIcon(R.block.actbottom);
		iACTSide = reg.registerIcon(R.block.actside);
		iBHC = reg.registerIcon(R.block.bhc);
		iIncineratorSide = reg.registerIcon(R.block.incineratorSide);
		iIncineratorTop = reg.registerIcon(R.block.incineratorTop);
		iDuplicator = reg.registerIcon(R.block.duplicator);
		iBreakerFrame = reg.registerIcon(R.block.breakerFrame);
		iBreakerFrameX = reg.registerIcon(R.block.breakerFrameX);
		iDetectorOff = reg.registerIcon(R.block.detectorOff);
		iDetectorOn = reg.registerIcon(R.block.detectorOn);
		iDepTop = reg.registerIcon("tubestuff:dep-top");
		iDepBottom = reg.registerIcon("tubestuff:dep-bottom");
		iDepSide = reg.registerIcon("tubestuff:dep-side");
	}
	
	@Override
	public IIcon getIcon(int side, int data)
	{
		if(data == 0) {
			// buffer
			return iBuffer;
		} else if(data == 1) {
			// auto crafting table mk2
			if(side == 0)
				return iACTBottom;
			else if(side == 1)
				return iACTTop;
			else
				return iACTSide;
		} else if(data == META_DEPLOYER) {
			if(side == 0)
				return iDepBottom;
			else if(side == 1)
				return iDepTop;
			else
				return iDepSide;
		} else if(data == 2) {
			// black hole chest
			return iBHC;
		/*} else if(data == 3) {
			// logic crafter
			return 5;*/
		} else if(data == 3 || data == 7) {
			// incinerator/liquid incinerator
			return (side == Dir.PY || side == Dir.NY ? iIncineratorTop : iIncineratorSide);
		} else if(data == 4 || data == 8) {
			// duplicator/liquid duplicator
			return iDuplicator;
		} else if(data == 6) {
			// block breaker; this is only used for particles
			return iBreakerFrame;
		} else if(data == META_ONLINE_DETECTOR) {
			return iDetectorOff;
		}
		return null; // unknown
	}
	
	@Override
	public IIcon getIcon(IBlockAccess w, int x, int y, int z, int side) {
		int meta = w.getBlockMetadata(x, y, z);
		if(meta == 1) {
			// ACT2
			TileAutoCraftingMk2 te = (TileAutoCraftingMk2)w.getTileEntity(x, y, z);
			if(side == te.outputFace)
				return iACTTop;
			else if(side == (te.outputFace ^ 1))
				return iACTBottom;
			else
				return iACTSide;
			
		} else if(meta == META_DEPLOYER) {
			int facing = ((TileDeployer)w.getTileEntity(x, y, z)).facing;
			if(side == facing)
				return iDepTop;
			else if(side == (facing ^ 1))
				return iDepBottom;
			else
				return iDepSide;
			
		} else if(meta == META_ONLINE_DETECTOR) {
			TileOnlineDetector tod = (TileOnlineDetector)w.getTileEntity(x, y, z);
			return tod.redstone_output != 0 ? iDetectorOn : iDetectorOff;
			
		} else {
			return getIcon(side, meta);
		}
	}
	
	@Override
    public void getCreativeItems(List<ItemStack> arraylist)
    {
		arraylist.add(new ItemStack(this, 1, 0));
		arraylist.add(new ItemStack(this, 1, 1));
		arraylist.add(new ItemStack(this, 1, 2));
		arraylist.add(new ItemStack(this, 1, 3));
		arraylist.add(new ItemStack(this, 1, 4));
		//arraylist.add(new ItemStack(this, 1, 5));
		arraylist.add(new ItemStack(this, 1, 6));
		arraylist.add(new ItemStack(this, 1, 7));
		arraylist.add(new ItemStack(this, 1, 8));
		arraylist.add(new ItemStack(this, 1, 9));
		//arraylist.add(new ItemStack(this, 1, META_MCT2));
		arraylist.add(new ItemStack(this, 1, META_DEPLOYER));
    }
	
	
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
		TileCombined tile = ((TileCombined)par1World.getTileEntity(par2, par3, par4));
		
		switch(par1World.getBlockMetadata(par2, par3, par4)) {
		case META_ACT2:
			return ((TileAutoCraftingMk2)tile).getComparatorOutput();
			
		case META_BUFFER:
			return Container.calcRedstoneFromInventory((IInventory)tile);
			
		case META_BLOCK_BREAKER:
			return ((TileBlockBreaker)tile).getComparatorOutput();
			
		case META_DEPLOYER:
			return ((TileDeployer)tile).getComparatorOutput();
		
		case META_DUPLICATOR:
		case META_INCINERATOR:
		case META_LIQUID_DUPLICATOR:
		case META_LIQUID_INCINERATOR:
		case META_MCT2:
		case META_ONLINE_DETECTOR:
		case META_BHC:
		default: return tile.redstone_output;
		}
	}
}
