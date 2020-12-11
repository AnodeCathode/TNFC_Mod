package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.feature.LockDirectionHotkey;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class DispensersPlaceBlocks extends Feature {

	public static List<String> blacklist;
	
	@Override
	public void setupConfig() {
		String[] blacklistArray = loadPropStringList("Blacklist", "Blocks that dispensers should not be able to place", new String[] {
				"minecraft:water",
				"minecraft:flowing_water",
				"minecraft:lava",
				"minecraft:flowing_lava",
				"minecraft:fire",
				"botania:specialflower"
		});
		
		blacklist = Arrays.asList(blacklistArray);
	}
	
	@Override
	public void postInit() {
		for(ResourceLocation r : Block.REGISTRY.getKeys()) {
			Block block = Block.REGISTRY.getObject(r);
			Item item = Item.getItemFromBlock(block);

			if(!(item instanceof ItemBlock) || blacklist.contains(r.toString()))
				continue;

			if(!BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.containsKey(item))
				BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, new BehaviourBlock((ItemBlock) item, block));
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "blockdispenser" };
	} 
	
	public class BehaviourBlock extends Bootstrap.BehaviorDispenseOptional {

		private final ItemBlock item;
		private final Block block;

		public BehaviourBlock(ItemBlock item, Block block) {
			this.item = item;
			this.block = block;
		}

		@Nonnull
		@Override
		@SuppressWarnings("deprecation")
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			this.successful = false;

			EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);

			BlockPos pos = source.getBlockPos().offset(facing);
			World world = source.getWorld();

			if(world.isAirBlock(pos) && block.canPlaceBlockAt(world, pos)) {
				int meta = item.getMetadata(stack.getItemDamage());
				IBlockState state;
				if(!(block instanceof BlockPistonBase))
					state = block.getStateFromMeta(meta);
				else state = block.getDefaultState();

				this.successful = true;

				LockDirectionHotkey.setBlockRotated(world, state, pos, facing);
				
				SoundType soundtype = block.getSoundType(state, world, pos, null);
				world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				stack.shrink(1);
				return stack;
			}

			return stack;
		}

	}
	
}
