package mods.immibis.tubestuff;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemStorage extends ItemBlock {
	public ItemStorage(Block block) {
		super(block);
		setMaxDamage(0);
	    setHasSubtypes(true);
	}
	
	@Override
    public int getMetadata(int meta) {
        return meta;
    }
	
	@Override
	public String getUnlocalizedName(ItemStack s) {
		return "tile.tubestuff.storage."+s.getItemDamage();
	}
}
