/**
 * This class was created by <SanAndreasP>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 22:33:44 (GMT)]
 */
package vazkii.quark.decoration.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IExtraVariantHolder;
import vazkii.arl.item.ItemModBlock;
import vazkii.arl.util.ModelHandler;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.decoration.block.BlockCustomChest;
import vazkii.quark.decoration.feature.VariedChests;
import vazkii.quark.decoration.feature.VariedChests.ChestType;
import vazkii.quark.decoration.tile.TileCustomChest;

import javax.annotation.Nonnull;

public class ItemChestBlock extends ItemModBlock implements IExtraVariantHolder {

	public ItemChestBlock(Block block, ResourceLocation res) {
		super(block, res);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemMeshDefinition getCustomMeshDefinition() {
		return stack -> ModelHandler.resourceLocations.get(getVariants()[0]);
	}

	@Override
	public String[] getExtraVariants() {
		return getVariants();
	}

	@Nonnull
	@Override
	public String getTranslationKey(ItemStack stack) {
		ChestType type = VariedChests.custom_chest.getCustomType(stack);
		String name = type.name + (getBlock() == VariedChests.custom_chest_trap ? "_trap" : "");
		return "tile." + LibMisc.PREFIX_MOD + "custom_chest_" + name;
	}

	@Override
	public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull IBlockState newState) {
		int typeCnt = 0;

		BlockPos posN = pos.north();
		BlockPos posS = pos.south();
		BlockPos posW = pos.west();
		BlockPos posE = pos.east();

		BlockCustomChest cChest = (BlockCustomChest) getBlock();
		VariedChests.ChestType myType = cChest.getCustomType(stack);

		if(world.getBlockState(posN).getBlock() == block && cChest.getCustomType(world, posN) == myType)
			typeCnt += cChest.isDoubleChest(world, posN, myType) ? 2 : 1;
		if(world.getBlockState(posS).getBlock() == block && cChest.getCustomType(world, posS) == myType)
			typeCnt += cChest.isDoubleChest(world, posS, myType) ? 2 : 1;
		if(world.getBlockState(posW).getBlock() == block && cChest.getCustomType(world, posW) == myType)
			typeCnt += cChest.isDoubleChest(world, posW, myType) ? 2 : 1;
		if(world.getBlockState(posE).getBlock() == block && cChest.getCustomType(world, posE) == myType)
			typeCnt += cChest.isDoubleChest(world, posE, myType) ? 2 : 1;

		if(typeCnt <= 1 && super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileCustomChest) {
				((TileCustomChest) te).chestType = myType;
				return true;
			}
		}

		return false;
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		BlockCustomChest chest = (BlockCustomChest) Block.getBlockFromItem(this);
		if(isInCreativeTab(tab))
			for(ChestType type : VariedChests.ChestType.VALID_TYPES)
				subItems.add(chest.setCustomType(new ItemStack(this, 1), type));
	}

}
