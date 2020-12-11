/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 17, 2019, 13:16 AM (EST)]
 */
package vazkii.quark.world.world.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import vazkii.quark.world.entity.EntityCrab;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RaveEventListener implements IWorldEventListener {

	private final World world;

	public RaveEventListener(World world) {
		this.world = world;
	}

	@Override
	public void playEvent(@Nullable EntityPlayer player, int type, @Nonnull BlockPos pos, int data) {
		if (type == 1010) {
			for (EntityCrab living : world.getEntitiesWithinAABB(EntityCrab.class,
					new AxisAlignedBB(pos).grow(3.0D))) {
				living.party(pos, data != 0);
			}
		}
	}

	@Override
	public void notifyBlockUpdate(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState, int flags) {
		// NO-OP
	}

	@Override
	public void notifyLightSet(@Nonnull BlockPos pos) {
		// NO-OP
	}

	@Override
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
		// NO-OP
	}

	@Override
	public void playSoundToAllNearExcept(@Nullable EntityPlayer player, @Nonnull SoundEvent soundIn, @Nonnull SoundCategory category, double x, double y, double z, float volume, float pitch) {
		// NO-OP
	}

	@Override
	public void playRecord(@Nonnull SoundEvent soundIn, @Nonnull BlockPos pos) {
		// NO-OP
		// This event is fired inconsistently. It is better to use event 1010.
	}

	@Override
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, @Nonnull int... parameters) {
		// NO-OP
	}

	@Override
	public void spawnParticle(int id, boolean ignoreRange, boolean minimiseParticleLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nonnull int... parameters) {
		// NO-OP
	}

	@Override
	public void onEntityAdded(@Nonnull Entity entityIn) {
		// NO-OP
	}

	@Override
	public void onEntityRemoved(@Nonnull Entity entityIn) {
		// NO-OP
	}

	@Override
	public void broadcastSound(int soundID, @Nonnull BlockPos pos, int data) {
		// NO-OP
	}

	@Override
	public void sendBlockBreakProgress(int breakerId, @Nonnull BlockPos pos, int progress) {
		// NO-OP
	}
}
