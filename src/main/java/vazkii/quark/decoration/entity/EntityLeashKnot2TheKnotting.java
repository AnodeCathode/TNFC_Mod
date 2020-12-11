package vazkii.quark.decoration.entity;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityLeashKnot2TheKnotting extends EntityLiving {

	public EntityLeashKnot2TheKnotting(World worldIn) {
		super(worldIn);
		setNoAI(true);
		width = 6F / 16F;
		height = 0.5F;
	}

	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
		dismantle(!source.isCreativePlayer());
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		
		IBlockState state = world.getBlockState(new BlockPos(posX, posY, posZ));
		if(!(state.getBlock() instanceof BlockFence)) {
			dismantle(true);
		} else {
			Entity holder = getHolder();
			if(holder == null || holder.isDead)
				dismantle(true);
			else if(holder.posY < posY && holder instanceof EntityLeashKnot) {
				double targetX = holder.posX;
				double targetY = holder.posY;
				double targetZ = holder.posZ;
				holder.setPosition(posX, posY, posZ);
				setPosition(targetX, targetY, targetZ);
			}
		}
	}

	@Nullable
	@SuppressWarnings("ConstantConditions")
	private Entity getHolder() {
		return getLeashHolder();
	}
	
	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) {
			Entity holder = getLeashHolder();
			holder.setDead();
			dismantle(!player.isCreative());
		}
		
		return true;
	}
	
	private void dismantle(boolean drop) {
		world.playSound(null, getPosition(), SoundEvents.ENTITY_LEASHKNOT_BREAK, SoundCategory.BLOCKS, 1F, 1F);
		if(!isDead && getHolder() != null && drop && !world.isRemote)
			dropItem(Items.LEAD, 1);
		setDead();
		Entity holder = getHolder();
		if (holder instanceof EntityLeashKnot)
			holder.setDead();
	}

}
