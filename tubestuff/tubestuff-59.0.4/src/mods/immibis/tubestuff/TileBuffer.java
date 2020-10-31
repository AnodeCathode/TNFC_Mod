package mods.immibis.tubestuff;


import mods.immibis.core.TileBasicInventory;
import mods.immibis.core.api.APILocator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileBuffer extends TileBasicInventory {

	public static final int INVSIZE = 18;
	
	public TileBuffer() {
		super(INVSIZE, "Buffer");
	}

	protected int update_ticks = 0;
	private boolean inv_empty = false;
	private boolean find_pipe = true;
	private TileEntity out_pipe = null;
	protected int delay = 20;
	
	private static final int MIN_DELAY = 10;
	private static final int MAX_DELAY = 100;
	
	private boolean enterPipe(ItemStack stack) {
		return APILocator.getCrossModBC().emitItem(stack, out_pipe, this);
	}
	
	private void checkPipe(TileEntity te)
	{
		if(out_pipe == null)
		{
			String pcn = APILocator.getCrossModBC().getPipeClass(te);
			if(pcn != null && (pcn.contains("PipeItemsWood") || pcn.contains("PipeItemsGold")))
				out_pipe = te;
		}
	}
	
	protected int chooseNextSlotToEmit() {
		ItemStack[] contents = inv.contents;
		int best = 0;
		int best_size = 0;
		int worst = 0;
		int worst_size = 64;
		int used = 0;
		for(int k = 0; k < INVSIZE; k++)
		{
			if(contents[k] == null)
				continue;
			++used;
			if(contents[k].stackSize > best_size)
			{
				best_size = contents[k].stackSize;
				best = k;
			}
			if(contents[k].stackSize < worst_size)
			{
				worst_size = contents[k].stackSize;
				worst = k;
			}
		}
		
		if(best_size == 0 || used == 0)
		{
			inv_empty = true;
			return -1;
		}
		
		delay = MAX_DELAY + (int)((MIN_DELAY - MAX_DELAY) * (used / (double)INVSIZE));
		
		if(best_size < contents[best].getMaxStackSize() / 2)
		{
			best_size = worst_size;
			best = worst;
		}
		return best;
	}

	@Override
	public void updateEntity() {
		if(inv_empty || worldObj.isRemote)
			return;
		if(++update_ticks < delay)
		{
			if(update_ticks > 2 && redstone_output != 0)
			{
				redstone_output = 0;
				notifyNeighbouringBlocks();
			}
			return;
		}
		update_ticks = 0;
		
		// find largest stack
		int best = chooseNextSlotToEmit();
		ItemStack[] contents = inv.contents;
		if(best < 0)
			return;
		
		if(best != 0)
		{
			// put it in the first slot so filters will grab it first
			ItemStack temp = contents[best];
			contents[best] = contents[0];
			contents[0] = temp;
			markDirty();
		}
		
		if(find_pipe)
		{
			out_pipe = null;
			
			checkPipe(worldObj.getTileEntity(xCoord-1, yCoord, zCoord));
			checkPipe(worldObj.getTileEntity(xCoord+1, yCoord, zCoord));
			checkPipe(worldObj.getTileEntity(xCoord, yCoord-1, zCoord));
			checkPipe(worldObj.getTileEntity(xCoord, yCoord+1, zCoord));
			checkPipe(worldObj.getTileEntity(xCoord, yCoord, zCoord-1));
			checkPipe(worldObj.getTileEntity(xCoord, yCoord, zCoord+1));
			
			find_pipe = false;
		}
		
		if(out_pipe != null)
		{
			if(contents[0] != null)
			{
				if(enterPipe(contents[0]))
				{
					contents[0] = null;
					markDirty();
				}
				else
					out_pipe = null;
			}
		}
		else if(redstone_output == 0)
		{
			redstone_output = 15;
			notifyNeighbouringBlocks();
		}
	}
	
	@Override
	public void onBlockNeighbourChange() {
		find_pipe = true;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(TubeStuff.instance, TubeStuff.GUI_BUFFER, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        super.setInventorySlotContents(i, itemstack);
        inv_empty = false;
    }
}
