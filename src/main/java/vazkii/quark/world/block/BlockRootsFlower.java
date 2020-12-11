package vazkii.quark.world.block;

import net.minecraft.item.ItemStack;
import vazkii.quark.world.feature.CaveRoots;

public class BlockRootsFlower extends BlockRoots {

	private final int meta;
	
	public BlockRootsFlower(String name, int meta) {	
		super(name);
		this.meta = meta;
	}
	
	@Override
	protected ItemStack getRootDrop() {
		return new ItemStack(CaveRoots.root_flower, 1, meta);
	}
	
	@Override
	protected float getDropChance() {
		return CaveRoots.rootFlowerDropChance;
	}
	
}
