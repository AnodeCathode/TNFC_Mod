/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/04/2016, 17:45:33 (GMT)]
 */
package vazkii.quark.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.lib.LibMisc;

import javax.annotation.Nonnull;

public class BlockBiotite extends BlockQuartz implements IQuarkBlock {

	private final String[] variants;
	private final String bareName;

	public BlockBiotite() {
		setSoundType(SoundType.STONE);
		setHardness(0.8F);

		String name = "biotite_block";
		variants = new String[] { name, "chiseled_biotite_block", "pillar_biotite_block" };
		bareName = name;

		setTranslationKey(name);
	}

	@Nonnull
	@Override
	public Block setTranslationKey(@Nonnull String name) {
		super.setTranslationKey(name);
		register(name);
		return this;
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
	public IProperty[] getIgnoredProperties() {
		return new IProperty[0];
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
	public IStateMapper getStateMapper() {
		return new StateMapperBase() {

			@Nonnull
			@Override
			public ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
				BlockQuartz.EnumType type = state.getValue(BlockQuartz.VARIANT);
				ResourceLocation baseLocation = new ResourceLocation(LibMisc.MOD_ID.toLowerCase(), "biotite_block");

				switch (type)  {
					case CHISELED: return new ModelResourceLocation(baseLocation, "chiseled");
					case LINES_Y: return new ModelResourceLocation(baseLocation, "axis=y");
					case LINES_X: return new ModelResourceLocation(baseLocation, "axis=x");
					case LINES_Z: return new ModelResourceLocation(baseLocation, "axis=z");
					default: return new ModelResourceLocation(baseLocation, "normal");
				}
			}
		};
	}

}
