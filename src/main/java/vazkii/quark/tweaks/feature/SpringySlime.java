/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 20, 2019, 01:58 AM (EST)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.automation.block.BlockColorSlime;
import vazkii.quark.base.handler.OverrideRegistryHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.util.MutableVectorHolder;
import vazkii.quark.tweaks.block.BlockSpringySlime;

public class SpringySlime extends Feature {

	public static BlockSpringySlime springySlime;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		springySlime = new BlockSpringySlime();
		springySlime.setTranslationKey("slime");
		OverrideRegistryHandler.registerBlock(springySlime, "slime");
	}

	private static final ThreadLocal<MutableVectorHolder> motionRecorder = ThreadLocal.withInitial(MutableVectorHolder::new);

	public static void recordMotion(Entity entity) {
		MutableVectorHolder value = motionRecorder.get();
		value.x = entity.motionX;
		value.y = entity.motionY;
		value.z = entity.motionZ;
	}

	public static void collideWithSlimeBlock(BlockPos pos, Entity entity) {
		if (entity instanceof EntityArrow) {
			EnumFacing sideHit = EnumFacing.getFacingFromVector(
					(float) (entity.posX + entity.motionX) - (pos.getX() + 0.5f),
					(float) (entity.posY + entity.motionY) - (pos.getY() + 0.5f),
					(float) (entity.posZ + entity.motionZ) - (pos.getZ() + 0.5f));

			switch (sideHit.getAxis()) {
				case X:
					if (Math.abs(entity.motionX) < 0.1)
						return;
					entity.motionX = 0.8 * Math.min(Math.abs(entity.motionX), 0.25) * sideHit.getXOffset();
					break;
				case Y:
					if (Math.abs(entity.motionY) < 0.1)
						return;
					entity.motionY = 0.8 * Math.min(Math.abs(entity.motionY), 0.25) * sideHit.getYOffset();
					break;
				case Z:
					if (Math.abs(entity.motionZ) < 0.1)
						return;
					entity.motionZ = 0.8 * Math.min(Math.abs(entity.motionZ), 0.25) * sideHit.getZOffset();
					break;
			}

			// inGround
			ObfuscationReflectionHelper.setPrivateValue(EntityArrow.class, (EntityArrow) entity, false, "field_70254_i");
		}
	}

	public static void onEntityCollision(Entity entity, double attemptedX, double attemptedY, double attemptedZ, double dX, double dY, double dZ) {
		if (!ModuleLoader.isFeatureEnabled(SpringySlime.class))
			return;

		if (entity.isSneaking() || (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying))
			return;

		double height = entity.height;
		double width = entity.width;

		double minX = entity.posX - width / 2;
		double minY = entity.posY;
		double minZ = entity.posZ - width / 2;
		double maxX = entity.posX + width / 2;
		double maxY = entity.posY + height;
		double maxZ = entity.posZ + width / 2;

		if (attemptedX != dX)
			applyForAxis(entity, Axis.X, minX, minY, minZ, maxX, maxY, maxZ, dX, attemptedX);

		if (attemptedY != dY)
			applyForAxis(entity, Axis.Y, minX, minY, minZ, maxX, maxY, maxZ, dY, attemptedY);

		if (attemptedZ != dZ)
			applyForAxis(entity, Axis.Z, minX, minY, minZ, maxX, maxY, maxZ, dZ, attemptedZ);
	}

	private static double axial(Axis axis, double we, double ud, double ns) {
		switch (axis) {
			case X:
				return we;
			case Y:
				return ud;
			default:
				return ns;
		}
	}

	private static void applyForAxis(Entity entity, Axis axis, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double dV, double attemptedV) {
		double baseValue = dV < 0 ? axial(axis, minX, minY, minZ) : axial(axis, maxX, maxY, maxZ);
		double clampedAttempt = attemptedV;
		if (Math.abs(attemptedV) > Math.abs(dV) + 1)
			clampedAttempt = dV + Math.signum(dV);
		double v1 = baseValue + dV;
		double v2 = baseValue + clampedAttempt;
		double minV = Math.min(v1, v2);
		double maxV = Math.max(v1, v2);
		EnumFacing impactedSide = EnumFacing.getFacingFromAxis(dV < 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, axis);

		int lowXBound = (int) Math.floor(axial(axis, minV, minX, minX));
		int highXBound = (int) Math.floor(axial(axis, maxV, maxX, maxX));
		int lowYBound = (int) Math.floor(axial(axis, minY, minV, minY));
		int highYBound = (int) Math.floor(axial(axis, maxY, maxV, maxY));
		int lowZBound = (int) Math.floor(axial(axis, minZ, minZ, minV));
		int highZBound = (int) Math.floor(axial(axis, maxZ, maxZ, maxV));

		boolean restoredZ = false;
		for (BlockPos position : BlockPos.getAllInBoxMutable(lowXBound, lowYBound, lowZBound,
				highXBound, highYBound, highZBound)) {
			restoredZ = applyCollision(entity, position, impactedSide, restoredZ);
		}
	}

	private static boolean applyCollision(Entity entity, BlockPos position, EnumFacing impacted, boolean restoredMotion) {
		IBlockState state = entity.world.getBlockState(position);
		if (isSlime(state)) {
			if (impacted == EnumFacing.UP && entity instanceof EntityItem)
				entity.onGround = false;

			switch (impacted.getAxis()) {
				case X:
					if (!restoredMotion) {
						restoredMotion = true;
						entity.motionX = motionRecorder.get().x;
					}
					entity.motionX = Math.abs(entity.motionX) * impacted.getXOffset();
					if (!(entity instanceof EntityLivingBase))
						entity.motionX *= 0.8;
					break;
				case Y:
					if (!restoredMotion) {
						restoredMotion = true;
						entity.motionY = motionRecorder.get().y;
					}
					entity.motionY = Math.abs(entity.motionY) * impacted.getYOffset();
					if (!(entity instanceof EntityLivingBase))
						entity.motionY *= 0.8;
					break;
				case Z:
					if (!restoredMotion) {
						restoredMotion = true;
						entity.motionZ = motionRecorder.get().z;
					}
					entity.motionZ = Math.abs(entity.motionZ) * impacted.getZOffset();
					if (!(entity instanceof EntityLivingBase))
						entity.motionZ *= 0.8;
					break;
			}
		}

		return restoredMotion;
	}
	
	private static boolean isSlime(IBlockState state) {
		Block block = state.getBlock();
		return block instanceof BlockSlime || block instanceof BlockColorSlime;
	}
}
