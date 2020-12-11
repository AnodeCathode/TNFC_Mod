/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 08, 2019, 16:44 AM (EST)]
 */
package vazkii.quark.misc.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import vazkii.quark.misc.feature.HorseWhistle;

import java.util.Objects;
import java.util.UUID;

public class EntityAIHorseFollow extends EntityAIBase {
	private final AbstractHorse horse;
	private EntityLivingBase owner;
	private final World world;
	private final double followSpeed;
	private final PathNavigate petPathfinder;
	private int timeToRebuildPath;
	private float oldWaterCost;

	private static final UUID HORSE_SIGHT = UUID.fromString("f58c267b-67fb-4997-b2a0-27649fb0aa9a");

	public EntityAIHorseFollow(AbstractHorse horse, double followSpeedIn) {
		this.horse = horse;
		this.world = horse.world;
		this.followSpeed = followSpeedIn;
		this.petPathfinder = horse.getNavigator();
		this.owner = null;

		this.horse.getAttributeMap()
				.getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE)
				.removeModifier(HORSE_SIGHT);

		this.setMutexBits(3);

		if (!(horse.getNavigator() instanceof PathNavigateGround) && !(horse.getNavigator() instanceof PathNavigateFlying)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	public void setOwner(EntityLivingBase player) {
		if (Objects.equals(player.getUniqueID(), horse.getOwnerUniqueId()) && !horse.getLeashed() && !horse.isBeingRidden())
			owner = player;
	}

	public boolean shouldExecute() {
		return owner != null && !horse.getLeashed() && Objects.equals(owner.getUniqueID(), horse.getOwnerUniqueId()) && !horse.isBeingRidden();
	}

	public boolean shouldContinueExecuting() {
		return !this.petPathfinder.noPath() && owner != null;
	}

	public void startExecuting() {
		this.timeToRebuildPath = 0;
		this.oldWaterCost = this.horse.getPathPriority(PathNodeType.WATER);
		this.horse.setPathPriority(PathNodeType.WATER, 0.0F);
		this.horse.getAttributeMap()
				.getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE)
				.applyModifier(new AttributeModifier(HORSE_SIGHT, "Whistle range bonus", HorseWhistle.horseSummonRange, 0));
	}

	public void resetTask() {
		this.owner = null;
		this.petPathfinder.clearPath();
		this.horse.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
		this.horse.getAttributeMap()
				.getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE)
				.removeModifier(HORSE_SIGHT);
	}

	public void updateTask() {
		this.horse.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, this.horse.getVerticalFaceSpeed());

		if (--this.timeToRebuildPath <= 0) {
			this.timeToRebuildPath = 10;

			if (!this.petPathfinder.tryMoveToEntityLiving(this.owner, this.followSpeed)) {
				if (!this.horse.getLeashed() && !this.horse.isRiding()) {
					if (this.horse.getDistanceSq(this.owner) >= HorseWhistle.horseSummonRange * HorseWhistle.horseSummonRange / 4) {
						if (!HorseWhistle.horsesAreMagical)
							return;

						int ownerX = MathHelper.floor(this.owner.posX) - 2;
						int ownerZ = MathHelper.floor(this.owner.posZ) - 2;
						int ownerY = MathHelper.floor(this.owner.getEntityBoundingBox().minY);

						for (int xOffset = 0; xOffset <= 4; ++xOffset) {
							for (int zOffset = 0; zOffset <= 4; ++zOffset) {
								if ((xOffset < 1 || zOffset < 1 || xOffset > 3 || zOffset > 3) &&
										this.isTeleportFriendlyBlock(ownerX, ownerZ, ownerY, xOffset, zOffset)) {
									this.horse.setLocationAndAngles(((ownerX + xOffset) + 0.5F), ownerY, ((ownerZ + zOffset) + 0.5F), this.horse.rotationYaw, this.horse.rotationPitch);
									this.petPathfinder.clearPath();
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset) {
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		IBlockState iblockstate = this.world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.horse) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
	}
}
