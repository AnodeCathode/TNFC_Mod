/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 11, 2019, 17:44 AM (EST)]
 */
package vazkii.quark.world.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.player.EntityPlayer;
import vazkii.quark.base.util.MutableVectorHolder;
import vazkii.quark.world.entity.EntityStoneling;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class EntityAIActWary extends EntityAIWanderAvoidWater {

	private final EntityStoneling stoneling;

	private final boolean scaredBySuddenMovement;

	private final double range;

	private boolean startled;

	private final Map<EntityPlayer, MutableVectorHolder> lastPositions = new WeakHashMap<>();
	private final Map<EntityPlayer, MutableVectorHolder> lastSpeeds = new WeakHashMap<>();

	public EntityAIActWary(EntityStoneling stoneling, double speed, double range, boolean scaredBySuddenMovement) {
		super(stoneling, speed, 1F);
		this.stoneling = stoneling;
		this.range = range;
		this.scaredBySuddenMovement = scaredBySuddenMovement;
	}

	private static void updateMotion(MutableVectorHolder holder, double x, double y, double z) {
		holder.x = x;
		holder.y = y;
		holder.z = z;
	}

	private static void updatePos(MutableVectorHolder holder, Entity entity) {
		holder.x = entity.posX;
		holder.y = entity.posY;
		holder.z = entity.posZ;
	}

	private static MutableVectorHolder initPos(EntityPlayer p) {
		MutableVectorHolder holder = new MutableVectorHolder();
		updatePos(holder, p);
		return holder;
	}

	public void startle() {
		startled = true;
	}

	protected boolean shouldApplyPath() {
		return super.shouldExecute();
	}

	@Override
	public void updateTask() {
		if (stoneling.getNavigator().noPath() && shouldApplyPath())
			startExecuting();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return shouldExecute();
	}

	@Override
	public void resetTask() {
		stoneling.getNavigator().clearPath();
	}

	@Override
	public boolean shouldExecute() {
		if (startled || stoneling.isPlayerMade())
			return false;

		List<EntityPlayer> playersAround = stoneling.world.getEntitiesWithinAABB(EntityPlayer.class, stoneling.getEntityBoundingBox().grow(range),
				(player) -> player != null && !player.capabilities.isCreativeMode && player.getDistanceSq(stoneling) < range * range);

		if (playersAround.isEmpty())
			return false;

		for (EntityPlayer player : playersAround) {
			if (player.isSneaking()) {
				if (scaredBySuddenMovement) {
					MutableVectorHolder lastSpeed = lastSpeeds.computeIfAbsent(player, p -> new MutableVectorHolder());
					MutableVectorHolder lastPos = lastPositions.computeIfAbsent(player, EntityAIActWary::initPos);
					double dX = player.posX - lastPos.x;
					double dY = player.posY - lastPos.y;
					double dZ = player.posZ - lastPos.z;

					double xDisplacement = dX - lastSpeed.x;
					double yDisplacement = dY - lastSpeed.y;
					double zDisplacement = dZ - lastSpeed.z;

					updateMotion(lastSpeed, dX, dY, dZ);
					updatePos(lastPos, player);

					double displacementSq = xDisplacement * xDisplacement +
							yDisplacement * yDisplacement +
							zDisplacement * zDisplacement;

					if (displacementSq < 0.01)
						return true;

					startled = true;
					return false;
				}
			} else {
				startled = true;
				return false;
			}
		}

		return true;
	}
}
