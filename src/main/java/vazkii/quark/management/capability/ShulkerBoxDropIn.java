package vazkii.quark.management.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import vazkii.arl.util.AbstractDropIn;
import vazkii.arl.util.ItemNBTHelper;

public class ShulkerBoxDropIn extends AbstractDropIn {

	@Override
	public boolean canDropItemIn(EntityPlayer player, ItemStack stack, ItemStack incoming) {
		return tryAddToShulkerBox(player, stack, incoming, true);
	}

	@Override
	public ItemStack dropItemIn(EntityPlayer player, ItemStack stack, ItemStack incoming) {
		tryAddToShulkerBox(player, stack, incoming, false);
		return stack;
	}
	
	private boolean tryAddToShulkerBox(EntityPlayer player, ItemStack shulkerBox, ItemStack stack, boolean simulate) {
		if(stack.getItem() instanceof ItemShulkerBox || shulkerBox.getCount() > 1)
			return false;
		
		TileEntityShulkerBox tile = new TileEntityShulkerBox();
		tile.setWorld(player.world);
		NBTTagCompound stackCmp = shulkerBox.getTagCompound();
		NBTTagCompound blockCmp;
		
		if(stackCmp == null || !stackCmp.hasKey("BlockEntityTag"))
			blockCmp = new NBTTagCompound();
		else blockCmp = stackCmp.getCompoundTag("BlockEntityTag");
		
		tile.readFromNBT(blockCmp);
		IItemHandler handler = new InvWrapper(tile);
		ItemStack result = ItemHandlerHelper.insertItem(handler, stack, simulate);
		boolean did = result.isEmpty();
		
		if(!simulate && did) {
			tile.writeToNBT(blockCmp);
			ItemNBTHelper.setCompound(shulkerBox, "BlockEntityTag", blockCmp);
		}
		
		return did;
	}
	
}
