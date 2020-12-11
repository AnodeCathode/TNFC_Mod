/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 12:17 AM (EST)]
 */
package vazkii.quark.world.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.world.entity.EntityFoxhound;

import javax.annotation.Nonnull;

public class EntityAIFoxhoundSleep extends EntityAIMoveToBlock {
	private final EntityFoxhound foxhound;

	private final boolean furnaceOnly;

	private boolean hadSlept = false;

	public EntityAIFoxhoundSleep(EntityFoxhound foxhound, double speed, boolean furnaceOnly) {
		super(foxhound, speed, 8);
		this.foxhound = foxhound;
		this.furnaceOnly = furnaceOnly;
	}

	@Override
	public boolean shouldExecute() {
		return this.foxhound.isTamed() && !this.foxhound.isSitting() && super.shouldExecute();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (!hadSlept || this.foxhound.isSleeping()) && super.shouldContinueExecuting();
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		hadSlept = false;
		this.foxhound.getAISit().setSitting(false);
		this.foxhound.getAISleep().setSleeping(false);
		this.foxhound.setSitting(false);
		this.foxhound.setSleeping(false);
	}

	@Override
	public void resetTask() {
		super.resetTask();
		hadSlept = false;
		this.foxhound.getAISit().setSitting(false);
		this.foxhound.getAISleep().setSleeping(false);
		this.foxhound.setSitting(false);
		this.foxhound.setSleeping(false);
	}

	@Override
	public void updateTask() {
		super.updateTask();

		if (!this.getIsAboveDestination() || foxhound.motionX > 0 || foxhound.motionZ > 0) {
			this.foxhound.getAISit().setSitting(false);
			this.foxhound.getAISleep().setSleeping(false);
			this.foxhound.setSitting(false);
			this.foxhound.setSleeping(false);
		} else if (!this.foxhound.isSitting()) {
			this.foxhound.getAISit().setSitting(true);
			this.foxhound.getAISleep().setSleeping(true);
			this.foxhound.setSitting(true);
			this.foxhound.setSleeping(true);
			hadSlept = true;
		}
	}

	@Override
	protected boolean shouldMoveTo(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isAirBlock(pos.up())) {
			return false;
		} else {
			IBlockState state = world.getBlockState(pos);
			TileEntity tileentity = world.getTileEntity(pos);

			if(furnaceOnly)
				return tileentity instanceof TileEntityFurnace;

			return state.getLightValue(world, pos) > 2;
		}
	}
}
