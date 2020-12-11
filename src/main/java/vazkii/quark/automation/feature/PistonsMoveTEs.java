package vazkii.quark.automation.feature;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.quark.api.IPistonCallback;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

import java.util.*;

public class PistonsMoveTEs extends Feature {

	private static final WeakHashMap<World, Map<BlockPos, NBTTagCompound>> movements = new WeakHashMap<>();
	private static final WeakHashMap<World, List<Pair<BlockPos, NBTTagCompound>>> delayedUpdates = new WeakHashMap<>();

	public static List<String> renderBlacklist;
	public static List<String> movementBlacklist;
	public static List<String> delayedUpdateList;

	@Override
	public void setupConfig() {
		String[] renderBlacklistArray = loadPropStringList("Tile Entity Render Blacklist", "Some mod blocks with complex renders will break everything if moved. Add them here if you find any.", 
				new String[] { "psi:programmer", "botania:starfield" });
		String[] movementBlacklistArray = loadPropStringList("Tile Entity Movement Blacklist", "Blocks with Tile Entities that pistons should not be able to move.\nYou can specify just mod names here, and all blocks from that mod will be disabled.", 
				new String[] { "minecraft:mob_spawner", "integrateddynamics:cable", "randomthings:blockbreaker", "minecraft:trapped_chest", "quark:custom_chest_trap" });
		String[] delayedUpdateListArray = loadPropStringList("Delayed Update List", "List of blocks whose tile entity update should be delayed by one tick after placed to prevent corruption.", 
				new String[] { "minecraft:dispenser", "minecraft:dropper" });
		
		renderBlacklist = Lists.newArrayList(renderBlacklistArray);
		movementBlacklist = Lists.newArrayList(movementBlacklistArray);
		delayedUpdateList = Lists.newArrayList(delayedUpdateListArray);
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if(!delayedUpdates.containsKey(event.world) || event.phase == Phase.START)
			return;
		
		List<Pair<BlockPos, NBTTagCompound>> delays = delayedUpdates.get(event.world);
		if(delays.isEmpty())
			return;
		
		for(Pair<BlockPos, NBTTagCompound> delay : delays) {
			TileEntity tile = TileEntity.create(event.world, delay.getRight());
			event.world.setTileEntity(delay.getLeft(), tile);
			if (tile != null)
				tile.updateContainingBlockInfo();
		}
		
		delays.clear();
	}
	
	// This is called from injected code and subsequently flipped, so to make it move, we return false
	public static boolean shouldMoveTE(boolean te, IBlockState state) {
		if(!ModuleLoader.isFeatureEnabled(PistonsMoveTEs.class))
			return te;
		
		return shouldMoveTE(state);
	}
	
	public static boolean shouldMoveTE(IBlockState state) {
		// Jukeboxes that are playing can't be moved so the music can be stopped
		if(state.getPropertyKeys().contains(BlockJukebox.HAS_RECORD) && state.getValue(BlockJukebox.HAS_RECORD))
			return true;

		if (state.getBlock() == Blocks.ENDER_CHEST) // They're obsidian!
			return true;
		
		ResourceLocation res = Block.REGISTRY.getNameForObject(state.getBlock());
		return PistonsMoveTEs.movementBlacklist.contains(res.toString()) || PistonsMoveTEs.movementBlacklist.contains(res.getNamespace());
	}
	
	public static void detachTileEntities(World world, BlockPistonStructureHelper helper, EnumFacing facing, boolean extending) {
		if(!ModuleLoader.isFeatureEnabled(PistonsMoveTEs.class))
			return;

		if (!extending)
			facing = facing.getOpposite();

		List<BlockPos> moveList = helper.getBlocksToMove();
		
		for(BlockPos pos : moveList) {
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock().hasTileEntity(state)) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile != null) {
					if (IPistonCallback.hasCallback(tile))
						IPistonCallback.getCallback(tile).onPistonMovementStarted();

					world.removeTileEntity(pos);

					registerMovement(world, pos.offset(facing), tile);
				}
			}
		}
	}
	
	public static boolean setPistonBlock(World world, BlockPos pos, IBlockState state, int flags) {
		if(!ModuleLoader.isFeatureEnabled(PistonsMoveTEs.class)) {
			world.setBlockState(pos, state, flags);
			return false;
		}
		
		Block block = state.getBlock();
		TileEntity tile = getAndClearMovement(world, pos);
		boolean destroyed = false;
		
		if(tile != null) {
			IBlockState currState = world.getBlockState(pos);
			TileEntity currTile = world.getTileEntity(pos);
			
			world.setBlockToAir(pos);
			if(!block.canPlaceBlockAt(world, pos)) {
				world.setBlockState(pos, state, flags);
				world.setTileEntity(pos, tile);
				block.dropBlockAsItem(world, pos, state, 0);
				world.setBlockToAir(pos);
				destroyed = true;
			}
			
			if(!destroyed) {
				world.setBlockState(pos, currState);
				world.setTileEntity(pos, currTile);
			}
		}
		
		if(!destroyed) {
			world.setBlockState(pos, state, flags);
			if(world.getTileEntity(pos) != null)
				world.setBlockState(pos, state, 0);
			
			if(tile != null && !world.isRemote) {
				if(delayedUpdateList.contains(Block.REGISTRY.getNameForObject(block).toString()))
					registerDelayedUpdate(world, pos, tile);
				else {
					world.setTileEntity(pos, tile);
					tile.updateContainingBlockInfo();

					if(block instanceof BlockChest)
						((BlockChest) block).checkForSurroundingChests(world, pos, state);
					
				}
			}
			world.notifyNeighborsOfStateChange(pos, block, true);
		}
		
		return false; // the value is popped, doesn't matter what we return
	}
	
	private static void registerMovement(World world, BlockPos pos, TileEntity tile) {
		if(!movements.containsKey(world))
			movements.put(world, new HashMap<>());
		
		movements.get(world).put(pos, tile.serializeNBT());
	}
	
	public static TileEntity getMovement(World world, BlockPos pos) {
		return getMovement(world, pos, false);
	}
	
	private static TileEntity getMovement(World world, BlockPos pos, boolean remove) {
		if(!movements.containsKey(world))
			return null;
		
		Map<BlockPos, NBTTagCompound> worldMovements = movements.get(world);
		if(!worldMovements.containsKey(pos))
			return null;
		
		NBTTagCompound ret = worldMovements.get(pos);
		if(remove)
			worldMovements.remove(pos);

		return TileEntity.create(world, ret);
	}
	
	private static TileEntity getAndClearMovement(World world, BlockPos pos) {
		TileEntity tile = getMovement(world, pos, true);

		if(tile != null) {
			if (IPistonCallback.hasCallback(tile))
				IPistonCallback.getCallback(tile).onPistonMovementFinished();

			tile.setPos(pos);

			tile.validate();
			
			if(tile instanceof TileEntityChest)
				((TileEntityChest) tile).numPlayersUsing = 0;
		}

		return tile;
	}
	
	private static void registerDelayedUpdate(World world, BlockPos pos, TileEntity tile) {
		if(!delayedUpdates.containsKey(world))
			delayedUpdates.put(world, new ArrayList<>());
		
		delayedUpdates.get(world).add(Pair.of(pos, tile.serializeNBT()));
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Pistons Move TEs";
	}
	
}

