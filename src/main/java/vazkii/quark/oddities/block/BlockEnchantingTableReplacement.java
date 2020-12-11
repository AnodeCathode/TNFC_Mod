package vazkii.quark.oddities.block;

import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibGuiIDs;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;
import vazkii.quark.oddities.tile.TileMatrixEnchanterBase;

import javax.annotation.Nonnull;

public class BlockEnchantingTableReplacement extends BlockEnchantmentTable {

	public BlockEnchantingTableReplacement() {
		setHardness(5.0F);
		setResistance(2000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMatrixEnchanter();
	}

	@Override
	public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, IBlockState state, @Nonnull EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.getTileEntity(pos) instanceof TileEntityEnchantmentTable)
			worldIn.setTileEntity(pos, createNewTileEntity(worldIn, 0));
		
		playerIn.openGui(Quark.instance, LibGuiIDs.MATRIX_ENCHANTING, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		if(stack.hasDisplayName()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if(tileentity instanceof TileMatrixEnchanterBase)
				((TileMatrixEnchanterBase) tileentity).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if(tileentity instanceof TileMatrixEnchanterBase) {
			TileMatrixEnchanterBase enchanter = (TileMatrixEnchanterBase) tileentity;
			enchanter.dropItem(0);
			enchanter.dropItem(1);
		}
	}

}
