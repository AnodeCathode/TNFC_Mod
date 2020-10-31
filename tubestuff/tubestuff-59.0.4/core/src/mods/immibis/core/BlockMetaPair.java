package mods.immibis.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockMetaPair {
	public String block;
	public int data;
	
	public BlockMetaPair(String block, int k) {
		if(block == null) throw new NullPointerException("block name is null");
		this.block = block;
		this.data = k;
	}

	public BlockMetaPair(Block block, int k) {
		this(Block.blockRegistry.getNameForObject(block), k);
	}

	public BlockMetaPair(ItemStack stack, int k) {
		this(Item.itemRegistry.getNameForObject(stack.getItem()), k);
	}
	
	@Override
	public int hashCode()
	{
		return (data << 16) + block.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		try
		{
			BlockMetaPair bmp = (BlockMetaPair)o;
			return bmp.block.equals(this.block) && bmp.data == data;
		}
		catch(ClassCastException e)
		{
			return false;
		}
	}
	
	public static BlockMetaPair parse(String s)
	{
		String[] a = s.split(":");
		if(a.length != 2)
			throw new NumberFormatException("Not a valid block ID/data value: " + s);
		return new BlockMetaPair((Block)Block.blockRegistry.getObject(a[0]), Integer.parseInt(a[1]));
	}
}
