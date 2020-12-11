package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockRope;

import javax.annotation.Nonnull;

public class Rope extends Feature {

	public static Block rope;
	
	public static boolean forceEnableMoveTEs;
	public static int recipeCount;
	public static boolean enableDispenser;
	
	@Override
	public void setupConfig() {
		forceEnableMoveTEs = loadPropBool("Force Enable Move TEs", "Set to true to allow ropes to move Tile Entities even if Pistons Push TEs is disabled.\nNote that ropes will still use the same blacklist.", false);
		recipeCount = loadPropInt("Recipe Output", "", 2);
		enableDispenser = loadPropBool("Enable Dispenser", "", true);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		rope = new BlockRope();
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(rope, recipeCount), 
				"SSS", "S S", "SSS",
				'S', "string");
	}
	
	@Override
	public void init() {
		if(enableDispenser)		
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Item.getItemFromBlock(rope), new BehaviourRope());
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	public static class BehaviourRope extends Bootstrap.BehaviorDispenseOptional {
		
		@Nonnull
		@Override
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
			BlockPos pos = source.getBlockPos().offset(facing);
			World world = source.getWorld();
			this.successful = false;
			
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() == rope) {
				if(((BlockRope) rope).pullDown(world, pos)) {
					this.successful = true;
					stack.shrink(1);
					return stack;
				}
			} else if(world.isAirBlock(pos) && rope.canPlaceBlockAt(world, pos)) {
				SoundType soundtype = rope.getSoundType(state, world, pos, null);
				world.setBlockState(pos, rope.getDefaultState());
				world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				this.successful = true;
				stack.shrink(1);
				
				return stack;
			}
			
			return stack;
		}
		
	}
	
}
