/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 20, 2019, 15:50 AM (EST)]
 */
package vazkii.quark.base.handler;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.api.ICollateralMover;
import vazkii.quark.api.ICollateralMover.MoveResult;
import vazkii.quark.api.INonSticky;
import vazkii.quark.base.module.GlobalConfig;

public class QuarkPistonStructureHelper extends BlockPistonStructureHelper {

	private final BlockPistonStructureHelper parent;

	private final World world;
	private final BlockPos pistonPos;
	private final BlockPos blockToMove;
	private final EnumFacing moveDirection;
	private final boolean extending;
	private final List<BlockPos> toMove = Lists.<BlockPos>newArrayList();
	private final List<BlockPos> toDestroy = Lists.<BlockPos>newArrayList();

	public QuarkPistonStructureHelper(BlockPistonStructureHelper parent, World worldIn, BlockPos posIn, EnumFacing pistonFacing, boolean extending) {
		super(worldIn, posIn, pistonFacing, extending);
		this.parent = parent;

		this.world = worldIn;
		this.pistonPos = posIn;
		if(extending) {
			this.moveDirection = pistonFacing;
			this.blockToMove = posIn.offset(pistonFacing);
		} else {
			this.moveDirection = pistonFacing.getOpposite();
			this.blockToMove = posIn.offset(pistonFacing, 2);
		}
		this.extending = extending;
	}

	@Override
	public boolean canMove() {
		if(!GlobalConfig.usePistonLogicRepl)
			return parent.canMove();

		toMove.clear();
		toDestroy.clear();
		IBlockState iblockstate = world.getBlockState(blockToMove);

		if(!BlockPistonBase.canPush(iblockstate, world, blockToMove, moveDirection, false, moveDirection)) {
			if(iblockstate.getPushReaction() == EnumPushReaction.DESTROY) {
				toDestroy.add(blockToMove);
				return true;
			} else return false;
		}
		else if(!addBlockLine(blockToMove, moveDirection))
			return false;
		else {
			for(int i = 0; i < toMove.size(); ++i) {
				BlockPos blockpos = toMove.get(i);

				if(isBlockBranching(world, blockpos) && addBranchingBlocks(world, blockpos) == MoveResult.PREVENT)
					return false;
			}

			return true;
		}
	}
	
	private boolean addBlockLine(BlockPos origin, EnumFacing face) {
		final int max = GlobalConfig.pistonPushLimit;

		BlockPos target = origin;
		IBlockState iblockstate = world.getBlockState(target);
		Block block = iblockstate.getBlock();

		if(iblockstate.getBlock().isAir(iblockstate, world, origin) 
				|| !BlockPistonBase.canPush(iblockstate, world, origin, moveDirection, false, face)
				|| origin.equals(pistonPos)
				|| toMove.contains(origin))
			return true;

		else {
			int lineLen = 1;

			if(lineLen + toMove.size() > max) 
				return false;
			else {
				BlockPos oldPos = origin;
				IBlockState oldState = world.getBlockState(origin); 
				
				boolean skippingNext = false;
				while(true) {
					if(!isBlockBranching(world, target))
						break;
					
					MoveResult res = getBranchResult(world, target);
					if(res == MoveResult.PREVENT)
						return false;
					else if(res != MoveResult.MOVE) {
						skippingNext = true;
						break;
					}
					
					target = origin.offset(moveDirection.getOpposite(), lineLen);
					iblockstate = world.getBlockState(target);
					block = iblockstate.getBlock();
					
					if(iblockstate.getBlock().isAir(iblockstate, world, target) || !BlockPistonBase.canPush(iblockstate, world, target, moveDirection, false, moveDirection.getOpposite()) || target.equals(pistonPos))
						break;
					
					if(getStickCompatibility(world, iblockstate, oldState, target, oldPos, moveDirection) != MoveResult.MOVE)
						break;
					
					oldState = iblockstate;
					oldPos = target;

					lineLen++;
					
					if(lineLen + toMove.size() > max)
						return false;
				}

				int i1 = 0;

				for(int j = lineLen - 1; j >= 0; --j) {
					BlockPos movePos = origin.offset(moveDirection.getOpposite(), j);
					if(toDestroy.contains(movePos))
						break;
					
					toMove.add(movePos);
					i1++;
				}
				
				if(skippingNext)
					return true;
				
				int j1 = 1;

				while(true) {
					BlockPos blockpos1 = origin.offset(moveDirection, j1);
					int k = toMove.indexOf(blockpos1);
					
					MoveResult res = MoveResult.MOVE;

					if(k > -1) {
						reorderListAtCollision(i1, k);

						for(int l = 0; l <= k + i1; ++l) {
							BlockPos blockpos2 = toMove.get(l);

							if(isBlockBranching(world, blockpos2)) {
								res = addBranchingBlocks(world, blockpos2);
								
								if(res == MoveResult.PREVENT)
									return false;
							}
						}

						return true;
					}
					
					if(res == MoveResult.MOVE) {
						iblockstate = world.getBlockState(blockpos1);

						if(iblockstate.getBlock().isAir(iblockstate, world, blockpos1))
							return true;

						if(!BlockPistonBase.canPush(iblockstate, world, blockpos1, moveDirection, true, moveDirection) || blockpos1.equals(pistonPos))
							return false;

						if(iblockstate.getPushReaction() == EnumPushReaction.DESTROY) {
							toDestroy.add(blockpos1);
							toMove.remove(blockpos1);
							return true;
						}

						boolean doneFinding = false;
						if(isBlockBranching(world, blockpos1)) {
							res = getBranchResult(world, blockpos1);
							if(res == MoveResult.PREVENT)
								return false;
							
							if(res != MoveResult.MOVE)
								doneFinding = true;
						}
						
						if(toMove.size() >= max)
							return false;
						
						toMove.add(blockpos1);

						++i1;
						++j1;
						
						if(doneFinding)
							return true;
					}
				}
			}
		}
	}

	private void reorderListAtCollision(int p_177255_1_, int p_177255_2_) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		List<BlockPos> list1 = Lists.<BlockPos>newArrayList();
		List<BlockPos> list2 = Lists.<BlockPos>newArrayList();
		list.addAll(toMove.subList(0, p_177255_2_));
		list1.addAll(toMove.subList(toMove.size() - p_177255_1_, toMove.size()));
		list2.addAll(toMove.subList(p_177255_2_, toMove.size() - p_177255_1_));
		toMove.clear();
		toMove.addAll(list);
		toMove.addAll(list1);
		toMove.addAll(list2);
	}

	@SuppressWarnings("incomplete-switch")
	private MoveResult addBranchingBlocks(World world, BlockPos fromPos) {
		IBlockState state = world.getBlockState(fromPos);
		Block block = state.getBlock();
		
		EnumFacing opposite = moveDirection.getOpposite();
		MoveResult retResult = MoveResult.SKIP;
		for(EnumFacing face : EnumFacing.values()) {
				MoveResult res = MoveResult.MOVE;
				BlockPos targetPos = fromPos.offset(face);
				IBlockState targetState = world.getBlockState(targetPos);
				
				if(block instanceof ICollateralMover)
					res = ((ICollateralMover) block).getCollateralMovement(world, pistonPos, moveDirection, face, fromPos);
				else res = getStickCompatibility(world, state, targetState, fromPos, targetPos, face);
				
				switch(res) {
				case PREVENT:
					return MoveResult.PREVENT;
				case MOVE:
					if(!addBlockLine(targetPos, face))
						return MoveResult.PREVENT;
					break;
				case BREAK:
					if(BlockPistonBase.canPush(targetState, world, targetPos, moveDirection, true, moveDirection)) {
						toDestroy.add(targetPos);
						toMove.remove(targetPos);
						return MoveResult.BREAK;
					}
					
					return MoveResult.PREVENT;
				}
				
				if(face == opposite)
					retResult = res;
			}
		
		return retResult;
	}

	private boolean isBlockBranching(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		return block instanceof ICollateralMover ? ((ICollateralMover) block).isCollateralMover(world, pistonPos, moveDirection, pos) : state.getBlock().isStickyBlock(state);
	}
	
	private MoveResult getBranchResult(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if(block instanceof ICollateralMover)
			return ((ICollateralMover) block).getCollateralMovement(world, pistonPos, moveDirection, moveDirection, pos);
		
		return MoveResult.MOVE;
	}
	
	private MoveResult getStickCompatibility(World world, IBlockState state1, IBlockState state2, BlockPos pos1, BlockPos pos2, EnumFacing face) {
		Block block = state1.getBlock();
		if(block instanceof INonSticky && !((INonSticky) block).canStickToBlock(world, pistonPos, pos1, pos2, state1, state2, moveDirection))
			return MoveResult.SKIP;
		
		block = state2.getBlock();
		if(block instanceof INonSticky && !((INonSticky) block).canStickToBlock(world, pistonPos, pos2, pos1, state2, state1, moveDirection))
			return MoveResult.SKIP;
		
		return MoveResult.MOVE;
	}

	@Nonnull
	@Override
	public List<BlockPos> getBlocksToMove() {
		if(!GlobalConfig.usePistonLogicRepl)
			return parent.getBlocksToMove();

		return toMove;
	}

	@Nonnull
	@Override
	public List<BlockPos> getBlocksToDestroy() {
		if(!GlobalConfig.usePistonLogicRepl)
			return parent.getBlocksToDestroy();

		return toDestroy;
	}

}
