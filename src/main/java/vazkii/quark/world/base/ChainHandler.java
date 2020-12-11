/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 17, 2019, 20:09 AM (EST)]
 */
package vazkii.quark.world.base;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.automation.feature.ChainLinkage;
import vazkii.quark.base.network.message.MessageSyncChain;

import java.util.UUID;

public class ChainHandler {
	public static final String LINKED_TO = "Quark:VehicleLink";
	public static final double DRAG = 0.95;
	public static final float CHAIN_SLACK = 2F;
	public static final float MAX_DISTANCE = 8F;
	private static final float STIFFNESS = 0.4F;
	private static final float DAMPING = 0.2F;
	private static final float MIN_FORCE = 0.05F;
	private static final float MAX_FORCE = 6F;

	private static <T extends Entity> void adjustVelocity(T master, Entity follower) {
		if (master == follower || master.world.isRemote)
			return;

		double dist = master.getDistance(follower);

		Vec3d masterPosition = master.getPositionVector();
		Vec3d followerPosition = follower.getPositionVector();

		Vec3d direction = followerPosition.subtract(masterPosition);
		direction = direction.subtract(0, direction.y, 0).normalize();

		double stretch = dist - CHAIN_SLACK;

		double springX = STIFFNESS * stretch * direction.x;
		double springZ = STIFFNESS * stretch * direction.z;

		springX = MathHelper.clamp(springX, -MAX_FORCE, MAX_FORCE);
		springZ = MathHelper.clamp(springZ, -MAX_FORCE, MAX_FORCE);

		double totalSpringSq = springX * springX + springZ * springZ;

		if (totalSpringSq > MIN_FORCE * MIN_FORCE) {
			master.motionX += springX;
			master.motionZ += springZ;

			follower.motionX -= springX;
			follower.motionZ -= springZ;

			Vec3d newMasterVelocity = new Vec3d(master.motionX, 0, master.motionZ);
			Vec3d newFollowerVelocity = new Vec3d(follower.motionX, 0, follower.motionZ);

			double deviation = newFollowerVelocity.subtract(newMasterVelocity).dotProduct(direction);

			double dampX = DAMPING * deviation * direction.x;
			double dampZ = DAMPING * deviation * direction.z;

			dampX = MathHelper.clamp(dampX, -MAX_FORCE, MAX_FORCE);
			dampZ = MathHelper.clamp(dampZ, -MAX_FORCE, MAX_FORCE);

			master.motionX += dampX;
			master.motionZ += dampZ;

			follower.motionX -= dampX;
			follower.motionZ -= dampZ;
		}
	}

	public static UUID getLink(Entity vehicle) {
		if (!canBeLinked(vehicle))
			return null;
		if (!vehicle.getEntityData().hasUniqueId(LINKED_TO))
			return null;

		return vehicle.getEntityData().getUniqueId(LINKED_TO);
	}

	public static boolean canBeLinkedTo(Entity entity) {
		return entity instanceof EntityBoat || entity instanceof EntityMinecart || entity instanceof EntityPlayer;
	}

	public static boolean canBeLinked(Entity entity) {
		return entity instanceof EntityBoat || entity instanceof EntityMinecart;
	}

	public static <T extends Entity> Entity getLinked(T vehicle) {
		UUID uuid = getLink(vehicle);
		if (uuid == null)
			return null;

		for (Entity entity : vehicle.world.getEntitiesWithinAABB(Entity.class,
				vehicle.getEntityBoundingBox().grow(MAX_DISTANCE), ChainHandler::canBeLinkedTo)) {

			if (entity.getUniqueID().equals(uuid)) {
				return entity;
			}
		}

		return null;
	}

	public static <T extends Entity> void adjustVehicle(T cart) {
		Entity other = getLinked(cart);

		if (other == null) {
			if (getLink(cart) != null)
				breakChain(cart);
			return;
		}

		if(!(other instanceof EntityPlayer))
			adjustVelocity(other, cart);

		cart.motionX *= DRAG;
		cart.motionZ *= DRAG;
	}

	private static <T extends Entity> void breakChain(T cart) {
		setLink(cart, null, true);

		if (!cart.world.isRemote && cart.world.getGameRules().getBoolean("doEntityDrops"))
			cart.entityDropItem(new ItemStack(ChainLinkage.chain), 0f);
	}

	public static void updateLinkState(Entity entity, UUID uuid, EntityPlayer player) {
		if (player instanceof EntityPlayerMP)
			NetworkHandler.INSTANCE.sendTo(new MessageSyncChain(entity.getEntityId(), uuid), (EntityPlayerMP) player);
	}

	public static void setLink(Entity entity, UUID uuid, boolean sync) {
		if (canBeLinked(entity)) {
			if (entity.getUniqueID().equals(uuid))
				return;

			if (uuid != null)
				entity.getEntityData().setUniqueId(LINKED_TO, uuid);
			else {
				entity.getEntityData().removeTag(LINKED_TO + "Most");
				entity.getEntityData().removeTag(LINKED_TO + "Least");
			}

			if (sync) {
				if (entity.world instanceof WorldServer) {
					WorldServer world = (WorldServer) entity.world;
					for (EntityPlayer target : world.getEntityTracker().getTrackingPlayers(entity))
						updateLinkState(entity, uuid, target);
				}
			}
		}
	}
}
