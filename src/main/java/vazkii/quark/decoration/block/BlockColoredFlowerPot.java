package vazkii.quark.decoration.block;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IRecipeGrouped;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.decoration.client.state.ColoredFlowerPotStateMapper;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockColoredFlowerPot extends BlockCustomFlowerPot implements IQuarkBlock, IBlockColorProvider, IRecipeGrouped {

	private final String[] variants;
	private final String bareName;

	public BlockColoredFlowerPot(EnumDyeColor color) {
		String name = "colored_flowerpot_" + color.getName();
		variants = new String[] { name };
		bareName = name;

		setCreativeTab(CreativeTabs.DECORATIONS);
		setTranslationKey(name);
	}

	@Nonnull
	@Override
	public Block setTranslationKey(@Nonnull String name) {
		super.setTranslationKey(name);
		register(name);
		return this;
	}
	
	@Nonnull
	@Override
	public ItemStack getItem(World worldIn, @Nonnull BlockPos pos, IBlockState state) {
		ItemStack stack = super.getItem(worldIn, pos, state);
		if(stack.getItem() == Items.FLOWER_POT)
			stack = new ItemStack(Item.getItemFromBlock(this));

		return stack;
	}

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public String getLocalizedName() {
		return net.minecraft.util.text.translation.I18n.translateToLocal(getTranslationKey() + ".name");
	}
	
	@Override
	public String getBareName() {
		return bareName;
	}

	@Override
	public String[] getVariants() {
		return variants;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemMeshDefinition getCustomMeshDefinition() {
		return null;
	}

	@Override
	public EnumRarity getBlockRarity(ItemStack stack) {
		return EnumRarity.COMMON;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IProperty[] getIgnoredProperties() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IStateMapper getStateMapper() {
		return new ColoredFlowerPotStateMapper();
	}

	@Override
	public IProperty getVariantProp() {
		return null;
	}

	@Override
	public Class getVariantEnum() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return (stack, i) -> 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor() {
		return (state, world, pos, i) -> Minecraft.getMinecraft().getBlockColors().colorMultiplier(Blocks.FLOWER_POT.getDefaultState(), world, pos, i);
	}
	
	@Override
	public String getRecipeGroup() {
		return "colored_flower_pot";
	}

}
