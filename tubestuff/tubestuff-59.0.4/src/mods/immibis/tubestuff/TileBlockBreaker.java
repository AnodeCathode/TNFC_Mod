package mods.immibis.tubestuff;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.MainThreadTaskQueue;
import mods.immibis.core.TileCombined;
import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

public class TileBlockBreaker extends TileCombined implements IInventory {

	// used by renderer if it crashed
	public boolean disableCustomRender, disableAllItemRender;
	
	public ItemStack tool;
	public byte facing;
	
	public EntityPlayerFakeTS player;
	
	@Override
	public List<ItemStack> getInventoryDrops() {
		if(tool == null)
			return Collections.emptyList();
		else
			return Collections.singletonList(tool);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return APILocator.getNetManager().wrap(new PacketBlockBreakerDescription(xCoord, yCoord, zCoord, tool, facing, isBreaking || forceAnimation), true);
	}

	public ItemStack getTool() {
		return tool;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return var1 == 0 ? tool : null;
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		if(var1 == 0 && var2 >= 1 && tool != null) {
			ItemStack rv = tool;
			tool = null;
			if(player != null)
				player.inventory.setInventorySlotContents(0, null);
			resendDescriptionPacket();
			return rv;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		if(var1 == 0) {
			tool = var2;
			if(player != null)
				player.inventory.setInventorySlotContents(0, var2);
			resendDescriptionPacket();
			notifyComparators();
		}
	}

	@Override
	public String getInventoryName() {
		return "Block breaker";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return false;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		// no isRemote check
		ItemStack h = player.inventory.getCurrentItem();
		if(h == null && tool == null) {
			return false;
		} else if(h != null && h.stackSize > 1) {
			if(tool != null)
				return false;
			setInventorySlotContents(0, player.inventory.decrStackSize(player.inventory.currentItem, 1));
		} else {
			// swap held item and tool
			player.inventory.setInventorySlotContents(player.inventory.currentItem, tool);
			setInventorySlotContents(0, h);
		}
		return true;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		if(tool != null) {
			NBTTagCompound tooltag = new NBTTagCompound();
			tool.writeToNBT(tooltag);
			tag.setTag("tool", tooltag);
		}
		tag.setByte("facing", facing);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if(tag.hasKey("tool"))
			tool = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("tool"));
		else
			tool = null;
		facing = tag.getByte("facing");
	}
	
	public int swingTime = 0; // used for rendering only
	public static final int SWING_PERIOD = 10; // ditto
	
	public boolean isBreaking = false;
	private int breakingX, breakingY, breakingZ;
	private boolean forceAnimation = false; // if true, description packets will have isBreaking=true even if not breaking
	
	private int initialDamage, curblockDamage = 0;
	private int durabilityRemainingOnBlock;
	
	private static final int COOLDOWN_MAX = 5;
	private int cooldown = 0;
	
	@Override
	public void updateEntity() {
		
		if(isBreaking || (swingTime % SWING_PERIOD) != 0)
			swingTime++;
		else
			swingTime = 0;
		
		if(worldObj.isRemote)
			return;
		
		if(forceAnimation) {
			forceAnimation = false;
			resendDescriptionPacket();
		}
		
		int x = xCoord, y = yCoord, z = zCoord;
		switch(facing) {
		case Dir.PX: x++; break;
		case Dir.PY: y++; break;
		case Dir.PZ: z++; break;
		case Dir.NX: x--; break;
		case Dir.NY: y--; break;
		case Dir.NZ: z--; break;
		}
		
		if(cooldown > 0)
			cooldown--;
		
		if(tool == null) {
			if(isBreaking)
				stopBreaking();
			return;
		}
		
		if(!isBreaking) {
			if(cooldown > 0)
				return;
			
			Block block = worldObj.getBlock(x, y, z);
			int meta = worldObj.getBlockMetadata(x, y, z);
			
			if(block == null || block.isAir(worldObj, x, y, z))
				return;
			
			if(player == null) {
				player = new EntityPlayerFakeTS(worldObj);
				player.posX = xCoord + 0.5;
				player.posY = yCoord + 0.5;
				player.posZ = zCoord + 0.5;
			}
			
			updateFakePlayer();
			
			isBreaking = true;
			resendDescriptionPacket();
			breakingX = x;
			breakingY = y;
			breakingZ = z;
			cooldown = COOLDOWN_MAX;
			
			startBreaking(x, y, z, block, meta);
			
		} else {
			updateFakePlayer();
			if(breakingX != x || breakingY != y || breakingZ != z) {
				stopBreaking();
				return;
			}
			continueBreaking();
		}
	}
	
	private void updateFakePlayer() {
		player.inventory.currentItem = 0;
		player.inventory.mainInventory[0] = tool;
		player.onGround = true;
	}
	
	private void stopBreaking() {
		isBreaking = false;
		worldObj.destroyBlockInWorldPartially(player.getEntityId(), breakingX, breakingY, breakingZ, -1);
		setInventorySlotContents(0, player.inventory.mainInventory[0]);
	}
	
	private void startBreaking(int x, int y, int z, Block block, int meta) {
		int side = Dir.PY; // TODO
		
		PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(player, Action.LEFT_CLICK_BLOCK, x, y, z, side, worldObj);
        if (event.isCanceled())
        {
        	stopBreaking();
            return;
        }
        
		this.initialDamage = this.curblockDamage;
        float var5 = 1.0F;
        

        if (block != null)
        {
            if (event.useBlock != Event.Result.DENY)
            {
                block.onBlockClicked(worldObj, x, y, z, player);
                // worldObj.extinguishFire(player, x, y, z, side);
            }
            var5 = block.getPlayerRelativeBlockHardness(player, worldObj, x, y, z);
        }

        if (event.useItem == Event.Result.DENY) {
        	stopBreaking();
            return;
        }

        if(var5 >= 1.0F)
        {
            this.tryHarvestBlock(x, y, z);
            stopBreaking();
            
            // make sure to animate
            forceAnimation = true;
            resendDescriptionPacket();
        }
        else
        {
            int var7 = (int)(var5 * 10.0F);
            worldObj.destroyBlockInWorldPartially(player.getEntityId(), x, y, z, var7);
            this.durabilityRemainingOnBlock = var7;
        }
	}
	
	private void continueBreaking() {
		++this.curblockDamage;
        int var1;
        float var4;
        int var5;

        var1 = this.curblockDamage - this.initialDamage;
        Block var2 = worldObj.getBlock(breakingX, breakingY, breakingZ);

        if (var2.isAir(worldObj, breakingX, breakingY, breakingZ))
        {
        	stopBreaking();
        }
        else
        {
            Block var3 = var2;
            var4 = var3.getPlayerRelativeBlockHardness(player, worldObj, breakingX, breakingY, breakingZ) * (float)(var1 + 1);
            var5 = (int)(var4 * 10.0F);

            if (var5 != this.durabilityRemainingOnBlock)
            {
                worldObj.destroyBlockInWorldPartially(player.getEntityId(), breakingX, breakingY, breakingZ, var5);
                this.durabilityRemainingOnBlock = var5;
            }
            
            if (var4 >= 1.0F)
            {
                this.tryHarvestBlock(breakingX, breakingY, breakingZ);
                stopBreaking();
            }
        }
	}
	
	public static class NewEntPositionUpdater {
		static {MinecraftForge.EVENT_BUS.register(new NewEntPositionUpdater());}
		
		public static List<Entity> newEnts = new ArrayList<Entity>();
		public static TileEntity itemOutTE, itemSenderTE;
		
		private ItemStack outputStack(ItemStack droppedStack) {
			if(itemOutTE instanceof IInventory && !(itemOutTE instanceof ISidedInventory))
				droppedStack = BasicInventory.mergeStackIntoRange(droppedStack, (IInventory)itemOutTE, 0, ((IInventory)itemOutTE).getSizeInventory());
			if(droppedStack != null)
				if(APILocator.getCrossModBC().emitItem(droppedStack, itemOutTE, itemSenderTE))
					droppedStack = null;
			
			return droppedStack;
		}
		
		@SubscribeEvent
		public void onEntSpawn(EntityJoinWorldEvent evt) {
			if(!evt.world.isRemote && itemSenderTE != null) {
				if(evt.entity instanceof EntityItem) {
					ItemStack stack = outputStack(((EntityItem)evt.entity).getEntityItem().copy());
					if(stack == null) {
						evt.setCanceled(true);
						evt.entity.setDead();
						((EntityItem)evt.entity).setEntityItemStack(new ItemStack(Blocks.cobblestone, 1, 0));
						return;
					}
					((EntityItem)evt.entity).setEntityItemStack(stack);
				}
				newEnts.add(evt.entity);
			}
		}
	}
	
	// copied from ForgeHooks, adapted to fake players
	private static BlockEvent.BreakEvent onBlockBreakEvent(World world, EntityPlayer entityPlayer, int x, int y, int z)
    {
        // Post the block break event
        Block block = world.getBlock(x, y, z);
        int blockMetadata = world.getBlockMetadata(x, y, z);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, block, blockMetadata, entityPlayer);
        MinecraftForge.EVENT_BUS.post(event);

        return event;
    }
	
	private static boolean foundLXP = false;
	private static Fluid LXP = null;
	private static synchronized Fluid getLXP() {
		if(foundLXP)
			return LXP;
		foundLXP = true;
		LXP = FluidRegistry.getFluid("immibis.liquidxp");
		return LXP;
	}
	
    public boolean tryHarvestBlock(int par1, int par2, int par3)
    {
    	BlockEvent.BreakEvent event = onBlockBreakEvent(worldObj, player, par1, par2, par3);
    	if(event.isCanceled())
    		return false;
    	else
        {
            ItemStack stack = tool;
            if (stack != null && stack.getItem().onBlockStartBreak(stack, par1, par2, par3, player))
            {
            	return false;
            }
            Block var4 = worldObj.getBlock(par1, par2, par3);
            int var5 = worldObj.getBlockMetadata(par1, par2, par3);
            worldObj.playAuxSFXAtEntity(player, 2001, par1, par2, par3, Block.getIdFromBlock(var4) + (var5 << 12));
            boolean var6 = false;

            //if (this.isCreative())
            //{
            //    var6 = this.removeBlock(par1, par2, par3);
            //    this.thisPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(par1, par2, par3, this.theWorld));
            //}
            //else
            {
                ItemStack var7 = tool;
                boolean var8 = false;
                Block block = var4;
                if (block != null)
                {
                    var8 = block.canHarvestBlock(player, var5);
                }

                double x = xCoord + 0.5;
                double y = yCoord + 0.5;
                double z = zCoord + 0.5;
                
                switch(facing) {
            	case Dir.NX: x += 0.7; break;
            	case Dir.NY: y += 0.7; break;
            	case Dir.NZ: z += 0.7; break;
            	case Dir.PX: x -= 0.7; break;
            	case Dir.PY: y -= 0.7; break;
            	case Dir.PZ: z -= 0.7; break;
            	}
                
                final double px = x, py = y, pz = z;
                final double mx = (facing == Dir.NX ? 0.3 : facing == Dir.PX ? -0.3 : 0);
                final double my = (facing == Dir.NY ? 0.3 : facing == Dir.PY ? -0.3 : 0);
                final double mz = (facing == Dir.NZ ? 0.3 : facing == Dir.PZ ? -0.3 : 0);
                
                TileEntity out = null;
    			switch(facing) {
    			case Dir.NX: out = worldObj.getTileEntity(xCoord+1, yCoord, zCoord); break;
    			case Dir.NY: out = worldObj.getTileEntity(xCoord, yCoord+1, zCoord); break;
    			case Dir.NZ: out = worldObj.getTileEntity(xCoord, yCoord, zCoord+1); break;
    			case Dir.PX: out = worldObj.getTileEntity(xCoord-1, yCoord, zCoord); break;
    			case Dir.PY: out = worldObj.getTileEntity(xCoord, yCoord-1, zCoord); break;
    			case Dir.PZ: out = worldObj.getTileEntity(xCoord, yCoord, zCoord-1); break;
    			}
    			NewEntPositionUpdater.itemOutTE = out;
    			NewEntPositionUpdater.itemSenderTE = this;
    			
    			try {
                	if (var7 != null)
	                {
	                    var7.func_150999_a(worldObj, var4, par1, par2, par3, player);
	
	                    if (var7.stackSize == 0)
	                    {
	                        this.player.destroyCurrentEquippedItem();
	                        //MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, var7));
	                    }
	                    
	                    notifyComparators();
	                }
	
	                var6 = this.removeBlock(par1, par2, par3);
	                if (var6 && var8)
	                {
                		var4.harvestBlock(worldObj, player, par1, par2, par3, var5);
	                }
	                
	                // Drop experience
	    	        if (var6)
	    	        {
	    	        	int xp = event.getExpToDrop();
	    	        	if(xp > 0) {
	    	        		Fluid lxp = getLXP();
	    	        		if(lxp != null) {
	    	        			TileEntity liquidContainer = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
	    	        			if(liquidContainer instanceof IFluidHandler) {
	    	        				IFluidHandler ifh = ((IFluidHandler)liquidContainer);
	    	        				FluidStack lxpStack = new FluidStack(lxp, TubeStuff.convertXPtoLXP_MB(xp));
	    	        				if(ifh.fill(ForgeDirection.UP, lxpStack, false) >= lxpStack.amount) {
	    	        					ifh.fill(ForgeDirection.UP, lxpStack, true);
	    	        					xp = 0;
	    	        				}
	    	        			}
	    	        		}
		    	        	
	    	        		if(xp > 0)
	    	        			var4.dropXpOnBlockBreak(this.worldObj, par1, par2, par3, xp);
	    	        	}
	    	        }
	    	        
	    	        for(final Entity e : NewEntPositionUpdater.newEnts) {
	    	        	MainThreadTaskQueue.enqueue(new Runnable() {
	    	        		@Override
	    	        		public void run() {
	    	        			e.setPosition(px, py, pz);
			    	        	e.motionX = mx;
			    				e.motionY = my;
			    				e.motionZ = mz;
	    	        		}
	    	        	}, Side.SERVER);
	    	        }
	    	        
                } finally {
                	NewEntPositionUpdater.newEnts.clear();
                	NewEntPositionUpdater.itemOutTE = null;
                	NewEntPositionUpdater.itemSenderTE = null;
                }
            }

            return var6;
        }
    }
    
    private boolean removeBlock(int par1, int par2, int par3)
    {
        Block var4 = worldObj.getBlock(par1, par2, par3);
        int var5 = worldObj.getBlockMetadata(par1, par2, par3);

        if (var4 != null)
        {
            var4.onBlockHarvested(worldObj, par1, par2, par3, var5, player);
        }

        boolean var6 = (var4 != null && var4.removedByPlayer(worldObj, player, par1, par2, par3));

        if (var4 != null && var6)
        {
            var4.onBlockDestroyedByPlayer(worldObj, par1, par2, par3, var5);
        }

        return var6;
    }
	
	@Override
	public void onPlaced(EntityLivingBase player, int look2) {
		super.onPlaced(player, look2);
		
		Vec3 look = player.getLook(1.0f);
		
        if(Math.abs(look.xCoord) > Math.abs(look.zCoord)) {
        	if(look.xCoord < 0)
        		look2 = Dir.NX;
        	else
        		look2 = Dir.PX;
        } else {
        	if(look.zCoord < 0)
        		look2 = Dir.NZ;
        	else
        		look2 = Dir.PZ;
        }
		
		facing = (byte)look2;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	public int getComparatorOutput() {
		if(tool == null || tool.getMaxDamage() == 0 || !tool.isItemStackDamageable())
			return 0;
		
		return 15 - (tool.getItemDamage() * 14 / tool.getMaxDamage());
	}
}