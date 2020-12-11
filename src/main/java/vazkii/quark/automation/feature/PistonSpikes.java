package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockIronRod;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

import java.util.ArrayList;
import java.util.List;

public class PistonSpikes extends Feature {

	public static Block iron_rod;

	public static boolean ezRecipe;
	
	@Override
	public void setupConfig() {
		ezRecipe = loadPropBool("Enable Easy Recipe", "Replace the End Rod in the recipe with an Iron Ingot", false);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		iron_rod = new BlockIronRod();
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(iron_rod), 
				"I", "I", "R",
				'I', "ingotIron",
				'R', (ezRecipe ? "ingotIron" : Blocks.END_ROD));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
