package vazkii.quark.misc.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.misc.feature.EndermitesIntoShulkers;

import java.util.Random;

public class EntityAIFormShulker extends EntityAIWander
{
	private final EntityEndermite endermite;
	private EnumFacing facing;
	private boolean doMerge;

	public EntityAIFormShulker(EntityEndermite endermite) {
		super(endermite, 1.0D, 10);
		this.endermite = endermite;
		this.setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		if(!endermite.getEntityWorld().getGameRules().getBoolean("mobGriefing"))
			return false;
		else if(endermite.getAttackTarget() != null)
			return false;
		else if(!endermite.getNavigator().noPath())
			return false;
		else {
			Random random = endermite.getRNG();

			if(random.nextDouble() < EndermitesIntoShulkers.chance) {
				facing = EnumFacing.random(random);
				BlockPos blockpos = (new BlockPos(endermite.posX, endermite.posY + 0.5D, endermite.posZ)).offset(facing);
				IBlockState iblockstate = endermite.getEntityWorld().getBlockState(blockpos);

				if(iblockstate.getBlock() == Blocks.PURPUR_BLOCK) {
					doMerge = true;
					return true;
				}
			}

			doMerge = false;
			return super.shouldExecute();
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !doMerge && super.shouldContinueExecuting();
	}
	
	@Override
	public void startExecuting() {
		if(!doMerge)
			super.startExecuting();
		else {
			World world = endermite.getEntityWorld();
			BlockPos blockpos = (new BlockPos(endermite.posX, endermite.posY + 0.5D, endermite.posZ)).offset(facing);
			IBlockState iblockstate = world.getBlockState(blockpos);

			if(iblockstate.getBlock() == Blocks.PURPUR_BLOCK) {
				world.setBlockToAir(blockpos);
				endermite.spawnExplosionParticle();
				endermite.setDead();
				
				EntityShulker shulker = new EntityShulker(world);
				shulker.setAttachmentPos(blockpos);
				shulker.setPosition(blockpos.getX() + 0.5, blockpos.getY() + 0.5, blockpos.getZ() + 0.5);
				world.spawnEntity(shulker);
			}
		}
	}
	
}
