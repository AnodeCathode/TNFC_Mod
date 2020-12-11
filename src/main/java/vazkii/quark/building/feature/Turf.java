package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockTurf;
import vazkii.quark.building.block.slab.BlockTurfSlab;
import vazkii.quark.building.block.stairs.BlockTurfStairs;

public class Turf extends Feature {

	private static final ItemStack GRASS_STACK = new ItemStack(Blocks.GRASS);
	private static final IBlockState GRASS_STATE = Blocks.GRASS.getDefaultState();
	
	public static Block turf;

	public static boolean enableStairsAndSlabs;
	
	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		turf = new BlockTurf();
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(turf, 1),
				"GG", "GG",
				'G', ProxyRegistry.newStack(Blocks.TALLGRASS, 1, 1));
		
		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(turf, 0, new BlockTurfStairs());
			BlockModSlab.initSlab(turf, 0, new BlockTurfSlab(false), new BlockTurfSlab( true));
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	public interface ITurfBlock extends IQuarkBlock, IBlockColorProvider {
		
		@Override
		default IItemColor getItemColor() {
			return (stack, tintIndex) ->
				Minecraft.getMinecraft().getItemColors().colorMultiplier(GRASS_STACK, 1);
		}

		@Override
		default IBlockColor getBlockColor() {
			return (state, worldIn, pos, tintIndex) -> 
				Minecraft.getMinecraft().getBlockColors().colorMultiplier(GRASS_STATE, worldIn, pos, 1);
		}
	}
	
}
