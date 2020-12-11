package vazkii.quark.base.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class BlockQuarkPressurePlate extends BlockPressurePlate implements IQuarkBlock {

	private final String[] variants;
	private final String bareName;

	public BlockQuarkPressurePlate(String name, Material material, Sensitivity sensitivity) {
		super(material, sensitivity);

		bareName = name;
		variants = new String[] { bareName };

		setTranslationKey(bareName);
	}

	@Override
	protected int computeRedstoneStrength(@Nonnull World worldIn, @Nonnull BlockPos pos) {
		AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(pos);
		List<? extends Entity> list = getValidEntities(worldIn, axisalignedbb);

		if(!list.isEmpty())
			for(Entity entity : list)
				if(!entity.doesEntityNotTriggerPressurePlate())
					return 15;

		return 0;
	}
	
	protected abstract List<Entity> getValidEntities(World world, AxisAlignedBB aabb);

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
	
}
