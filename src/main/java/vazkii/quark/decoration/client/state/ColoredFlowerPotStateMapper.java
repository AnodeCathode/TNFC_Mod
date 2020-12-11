package vazkii.quark.decoration.client.state;

import com.google.common.collect.Maps;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.decoration.block.BlockCustomFlowerPot;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

@SideOnly(Side.CLIENT)
public class ColoredFlowerPotStateMapper extends StateMapperBase {
	
	@Nonnull
	@Override
	protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
		ResourceLocation loc = state.getBlock().getRegistryName();

		assert loc != null;

		if(state.getValue(BlockCustomFlowerPot.CUSTOM))
			return new ModelResourceLocation(loc, "contents=custom");

		LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
		map.remove(BlockCustomFlowerPot.CUSTOM);
		map.remove(BlockCustomFlowerPot.LEGACY_DATA);

		return new ModelResourceLocation(loc, this.getPropertyString(map));
	}
}
