package vazkii.quark.decoration.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.quark.decoration.feature.GlassItemFrame;

import javax.annotation.Nonnull;

public class EntityGlassItemFrame extends EntityFlatItemFrame {

	public EntityGlassItemFrame(World worldIn) {
		super(worldIn);
	}
	
	public EntityGlassItemFrame(World worldIn, BlockPos blockPos, EnumFacing face) {
		super(worldIn, blockPos, face);
	}

	@Override
	protected void dropFrame() {
		entityDropItem(new ItemStack(GlassItemFrame.glass_item_frame, 1, 0), 0.0F);
	}
	
	@Nonnull
	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		ItemStack held = getDisplayedItem();
		if (held.isEmpty())
			return new ItemStack(GlassItemFrame.glass_item_frame);
		else
			return held.copy();
	}
}
