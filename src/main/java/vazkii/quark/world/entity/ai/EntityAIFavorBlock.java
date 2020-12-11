/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 27, 2019, 13:45 AM (EST)]
 */
package vazkii.quark.world.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public class EntityAIFavorBlock extends EntityAIBase {

	private final EntityCreature creature;
	private final double movementSpeed;
	private final Predicate<IBlockState> targetBlock;

	protected int runDelay;
	private int timeoutCounter;
	private int maxStayTicks;

	protected BlockPos destinationBlock = BlockPos.ORIGIN;

	public EntityAIFavorBlock(EntityCreature creature, double speed, Predicate<IBlockState> predicate) {
		this.creature = creature;
		this.movementSpeed = speed;
		this.targetBlock = predicate;
		setMutexBits(5);
	}

	public EntityAIFavorBlock(EntityCreature creature, double speed, Block block) {
		this(creature, speed, (state) -> state.getBlock() == block);
	}

	public EntityAIFavorBlock(EntityCreature creature, double speed, IBlockState targetState) {
		this(creature, speed, (state) -> state == targetState);
	}

	public boolean shouldExecute() {
		if (runDelay > 0) {
			--runDelay;
			return false;
		} else {
			runDelay = 200 + creature.getRNG().nextInt(200);
			return searchForDestination();
		}
	}

	public boolean shouldContinueExecuting() {
		return timeoutCounter >= -maxStayTicks && timeoutCounter <= 1200 && targetBlock.test(creature.world.getBlockState(destinationBlock));
	}

	public void startExecuting() {
		creature.getNavigator().tryMoveToXYZ(destinationBlock.getX() + 0.5, destinationBlock.getY() + 1, destinationBlock.getZ() + 0.5, movementSpeed);
		timeoutCounter = 0;
		maxStayTicks = creature.getRNG().nextInt(creature.getRNG().nextInt(1200) + 1200) + 1200;
	}

	public void updateTask() {
		if (creature.getDistanceSqToCenter(destinationBlock.up()) > 1.0D) {
			++timeoutCounter;

			if (timeoutCounter % 40 == 0)
				creature.getNavigator().tryMoveToXYZ(destinationBlock.getX() + 0.5D, destinationBlock.getY() + 1, destinationBlock.getZ() + 0.5D, movementSpeed);
		} else {
			--timeoutCounter;
		}
	}

	private boolean searchForDestination() {
		double followRange = creature.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
		double xBase = creature.posX;
		double yBase = creature.posY;
		double zBase = creature.posZ;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int yShift = 0;
			 yShift <= 1;
			 yShift = yShift > 0 ? -yShift : 1 - yShift) {

			for (int seekDist = 0; seekDist < followRange; ++seekDist) {
				for (int xShift = 0;
					 xShift <= seekDist;
					 xShift = xShift > 0 ? -xShift : 1 - xShift) {

					for (int zShift = xShift < seekDist && xShift > -seekDist ? seekDist : 0;
						 zShift <= seekDist;
						 zShift = zShift > 0 ? -zShift : 1 - zShift) {

						pos.setPos(xBase + xShift, yBase + yShift - 1, zBase + zShift);

						if (creature.isWithinHomeDistanceFromPosition(pos) &&
								targetBlock.test(creature.world.getBlockState(pos))) {
							destinationBlock = pos;
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
