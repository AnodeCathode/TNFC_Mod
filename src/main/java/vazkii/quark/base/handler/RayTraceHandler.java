package vazkii.quark.base.handler;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RayTraceHandler {

	public static RayTraceResult rayTrace(World world, EntityPlayer player, boolean stopOnLiquid) {
		return rayTrace(world, player, stopOnLiquid, getEntityRange(player));
	}
	
	public static RayTraceResult rayTrace(World world, Entity player, boolean stopOnLiquid, double range) {
		 Pair<Vec3d, Vec3d> params = getEntityParams(player);
		
		return rayTrace(world, params.getLeft(), params.getRight(), stopOnLiquid, range);
	}
	
	public static RayTraceResult rayTrace(World world, Vec3d startPos, Vec3d ray, boolean stopOnLiquid, double range) {
		return rayTrace(world, startPos, ray.scale(range), stopOnLiquid);
	}

	public static RayTraceResult rayTrace(World world, Vec3d startPos, Vec3d ray, boolean stopOnLiquid) {
		Vec3d end = startPos.add(ray);
		return world.rayTraceBlocks(startPos, end, stopOnLiquid);
	}
	
	public static double getEntityRange(EntityLivingBase player) {
		return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
	}
	
	public static Pair<Vec3d, Vec3d> getEntityParams(Entity player) {
		float scale = 1.0F;
		float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scale;
		float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scale;
		double posX = player.prevPosX + (player.posX - player.prevPosX) * scale;
		double posY = player.prevPosY + (player.posY - player.prevPosY) * scale;
		if (player instanceof EntityPlayer)
			posY += ((EntityPlayer) player).eyeHeight;
		double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * scale;
		Vec3d rayPos = new Vec3d(posX, posY, posZ);

		float zYaw = -MathHelper.cos(yaw * (float) Math.PI / 180);
		float xYaw = MathHelper.sin(yaw * (float) Math.PI / 180);
		float pitchMod = -MathHelper.cos(pitch * (float) Math.PI / 180);
		float azimuth = -MathHelper.sin(pitch * (float) Math.PI / 180);
		float xLen = xYaw * pitchMod;
		float yLen = zYaw * pitchMod;
		Vec3d ray = new Vec3d(xLen, azimuth, yLen);
		
		return Pair.of(rayPos, ray);
	}
	
}
