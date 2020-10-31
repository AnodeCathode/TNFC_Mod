package mods.immibis.core;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemCombined extends ItemBlock {
	public BlockCombined block;
	
	private String[] names;
	
	public ItemCombined(Block block, String prefix, String[] unlocNames) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
		
		this.names = new String[16];
		for(int k = 0; k < unlocNames.length && k < 16; k++)
			this.names[k] = "tile." + prefix + "." + unlocNames[k]; 
	}
	
	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
    public String getUnlocalizedName(ItemStack stack)
    {
		int m = stack.getItemDamage();
		if(m >= 0 && m < 16 && names[m] != null)
			return names[m];
		return "";
    }
}
