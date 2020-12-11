/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 23, 2019, 16:18 AM (EST)]
 */
package vazkii.quark.world.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;

import java.util.function.Predicate;
import java.util.function.Supplier;

public enum EnumStonelingVariant implements IEntityLivingData {
	STONE("stoneling", "stone", stoneState(EnumType.STONE),
			isBlock(Blocks.STONE)
					.and(hasProp(BlockStone.VARIANT, EnumType.STONE))
					.or(isBlock(Blocks.COBBLESTONE))),
	ANDESITE("stoneling_andesite", "stoneAndesite", stoneState(EnumType.ANDESITE),
			isBlock(Blocks.STONE)
					.and(hasProp(BlockStone.VARIANT, EnumType.ANDESITE, EnumType.ANDESITE_SMOOTH))),
	DIORITE("stoneling_diorite", "stoneDiorite", stoneState(EnumType.DIORITE),
			isBlock(Blocks.STONE)
					.and(hasProp(BlockStone.VARIANT, EnumType.DIORITE, EnumType.DIORITE_SMOOTH))),
	GRANITE("stoneling_granite", "stoneGranite", stoneState(EnumType.GRANITE),
			isBlock(Blocks.STONE)
					.and(hasProp(BlockStone.VARIANT, EnumType.GRANITE, EnumType.GRANITE_SMOOTH))),
	LIMESTONE("stoneling_limestone", "stoneLimestone", fromNullableBlock(() -> RevampStoneGen.limestone),
			isBlock(() -> RevampStoneGen.limestone)),
	BASALT("stoneling_basalt", "stoneBasalt", fromNullableBlock(() -> Basalt.basalt),
			isBlock(() -> Basalt.basalt)),
	MARBLE("stoneling_marble", "stoneMarble", fromNullableBlock(() -> RevampStoneGen.marble),
			isBlock(() -> RevampStoneGen.marble));


	private static Supplier<IBlockState> fromNullableBlock(Supplier<Block> block) {
		return () -> {
			Block inst = block.get();
			if (inst == null)
				return null;
			return inst.getDefaultState();
		};
	}

	private static Supplier<IBlockState> stoneState(EnumType type) {
		return () -> Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, type);
	}

	private static Predicate<IBlockState> isBlock(Block type) {
		return (state) -> state.getBlock() == type;
	}

	private static Predicate<IBlockState> isBlock(Supplier<Block> type) {
		return (state) -> state.getBlock() == type.get();
	}

	@SafeVarargs
	private static <T extends Comparable<T>> Predicate<IBlockState> hasProp(IProperty<T> prop, T... valuesAllowed) {
		return (state) -> {
			T value = state.getValue(prop);
			for (T test : valuesAllowed) {
				if (value == test)
					return true;
			}
			return false;
		};
	}

	private final ResourceLocation texture;
	private final String oreKey;

	private final Supplier<IBlockState> stateSupplier;
	private IBlockState state;
	private final Predicate<IBlockState> allowedStates;

	EnumStonelingVariant(String variantPath, String oreKey, Supplier<IBlockState> displayState, Predicate<IBlockState> accepts) {
		this.texture = new ResourceLocation(LibMisc.MOD_ID, "textures/entity/" + variantPath + ".png");
		this.oreKey = oreKey;
		this.stateSupplier = displayState;
		this.allowedStates = accepts;
	}

	public static EnumStonelingVariant byIndex(byte index) {
		EnumStonelingVariant[] values = values();
		return values[MathHelper.clamp(index, 0, values.length - 1)];
	}

	public byte getIndex() {
		return (byte) ordinal();
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public String getOreKey() {
		return oreKey;
	}

	public IBlockState getDisplayState() {
		if (state == null)
			state = stateSupplier.get();
		return state;
	}

	public boolean acceptsState(IBlockState state) {
		return allowedStates.test(state);
	}
}
