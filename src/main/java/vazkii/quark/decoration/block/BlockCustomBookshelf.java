/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [Aug 14, 2016, 9:31:58 PM (GMT)]
 */
package vazkii.quark.decoration.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

public class BlockCustomBookshelf extends BlockMetaVariants<BlockCustomBookshelf.Variants> implements IQuarkBlock {

	public BlockCustomBookshelf() {
		super("custom_bookshelf", Material.WOOD, Variants.class);
		setHardness(1.5F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 5;
	}
	
	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos) {
		return 1;
	}

	@Override
	public int quantityDropped(Random random) {
		return 3;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}
	
	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.BOOK;
	}
	
	public enum Variants implements IStringSerializable {
		BOOKSHELF_SPRUCE,
		BOOKSHELF_BIRCH,
		BOOKSHELF_JUNGLE,
		BOOKSHELF_ACACIA,
		BOOKSHELF_DARK_OAK;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
