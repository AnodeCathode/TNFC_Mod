/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 12:04 AM (EST)]
 */
package vazkii.quark.world.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.util.EntityOpacityHandler;
import vazkii.quark.oddities.feature.TinyPotato;
import vazkii.quark.tweaks.ai.EntityAIWantLove;
import vazkii.quark.world.entity.ai.EntityAIFoxhoundSleep;
import vazkii.quark.world.entity.ai.EntityAISleep;
import vazkii.quark.world.feature.Foxhounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityFoxhound extends EntityWolf implements IMob {

	public static final ResourceLocation FOXHOUND_LOOT_TABLE = new ResourceLocation("quark", "entities/foxhound");

	private static final DataParameter<Boolean> TEMPTATION = EntityDataManager.createKey(EntityFoxhound.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(EntityFoxhound.class, DataSerializers.BOOLEAN);

	private int timeUntilPotatoEmerges = 0;

	public EntityFoxhound(World worldIn) {
		super(worldIn);
		this.setSize(0.8F, 0.8F);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.LAVA, 1.0F);
		this.setPathPriority(PathNodeType.DANGER_FIRE, 1.0F);
		this.setPathPriority(PathNodeType.DAMAGE_FIRE, 1.0F);
		this.isImmuneToFire = true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		setCollarColor(EnumDyeColor.ORANGE);
		dataManager.register(TEMPTATION, false);
		dataManager.register(SLEEPING, false);
	}

	@Override
	public boolean isNoDespawnRequired() {
		return isTamed() || super.isNoDespawnRequired();
	}

	@Override
	protected boolean canDespawn() {
		return !isTamed();
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		return EntityOpacityHandler.isEntityInsideOpaqueBlock(this);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL && !isTamed()) {
			setDead();
			return;
		}

		if (!world.isRemote && TinyPotato.tiny_potato != null) {
			if (timeUntilPotatoEmerges == 1) {
				timeUntilPotatoEmerges = 0;
				ItemStack stack = new ItemStack(TinyPotato.tiny_potato);
				ItemNBTHelper.setBoolean(stack, "angery", true);
				entityDropItem(stack, 0f);
				playSound(SoundEvents.ENTITY_GENERIC_HURT, 1f, 1f);
			} else if (timeUntilPotatoEmerges > 1) {
				timeUntilPotatoEmerges--;
			}
		}

		if (EntityAIWantLove.needsPets(this)) {
			Entity owner = getOwner();
			if (owner != null && owner.getDistanceSq(this) < 1 && !owner.isInWater() && !owner.isImmuneToFire() && (!(owner instanceof EntityPlayer) || !((EntityPlayer) owner).isCreative()))
				owner.setFire(5);
		}

		if(this.world.isRemote)
			this.world.spawnParticle(isSleeping() ? EnumParticleTypes.SMOKE_NORMAL : EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * this.width, this.posY + (this.rand.nextDouble() - 0.5D) * this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * this.width, 0.0D, 0.0D, 0.0D);

		if(isTamed()) {
			BlockPos below = getPosition().down();
			TileEntity tile = world.getTileEntity(below);
			if (tile instanceof TileEntityFurnace) {
				int cookTime = ((TileEntityFurnace) tile).getField(2);
				if (cookTime > 0 && cookTime % 3 == 0) {
					List<EntityFoxhound> foxhounds = world.getEntitiesWithinAABB(EntityFoxhound.class, new AxisAlignedBB(getPosition()),
							(fox) -> fox != null && fox.isTamed());
					if(!foxhounds.isEmpty() && foxhounds.get(0) == this)
						((TileEntityFurnace) tile).setField(2, Math.min(199, cookTime + 1));
				}
			}
		}
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return FOXHOUND_LOOT_TABLE;
	}

	protected EntityAISleep aiSleep;

	@Override
	protected void initEntityAI() {
		this.aiSit = new EntityAISit(this);
		this.aiSleep = new EntityAISleep(this);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiSleep);
		this.tasks.addTask(3, this.aiSit);
		this.tasks.addTask(4, new EntityAILeapAtTarget(this, 0.4F));
		this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(6, new EntityAIFoxhoundSleep(this, 0.8D, true));
		this.tasks.addTask(7, new EntityAIFoxhoundSleep(this, 0.8D, false));
		this.tasks.addTask(8, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(9, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(10, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(11, new EntityAIBeg(this, 8.0F));
		this.tasks.addTask(12, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(12, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(4, new EntityAITargetNonTamed<>(this, EntityAnimal.class, false,
				target -> target instanceof EntitySheep || target instanceof EntityRabbit));
		this.targetTasks.addTask(4, new EntityAITargetNonTamed<>(this, EntityPlayer.class, false,
				target -> !isTamed()));
		this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, AbstractSkeleton.class, false));
	}

	@Override
	public boolean isAngry() {
		return (!isTamed() && world.getDifficulty() != EnumDifficulty.PEACEFUL) || super.isAngry();
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (entityIn.isImmuneToFire()) {
			if (entityIn instanceof EntityPlayer)
				return false;
			return super.attackEntityAsMob(entityIn);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this).setFireDamage(),
				((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag) {
			entityIn.setFire(5);
			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
		setWoke();
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);

		if(itemstack.getItem() == Items.BONE && !isTamed())
			return false;

		if (!this.isTamed() && !itemstack.isEmpty()) {
			if (itemstack.getItem() == Items.COAL && (world.getDifficulty() == EnumDifficulty.PEACEFUL || player.isCreative() || player.getActivePotionEffect(MobEffects.FIRE_RESISTANCE) != null) && !world.isRemote) {
				if (rand.nextDouble() < Foxhounds.tameChance) {
					this.setTamedBy(player);
					this.navigator.clearPath();
					this.setAttackTarget(null);
					this.aiSit.setSitting(true);
					this.setHealth(20.0F);
					this.playTameEffect(true);
					this.world.setEntityState(this, (byte)7);
				} else {
					this.playTameEffect(false);
					this.world.setEntityState(this, (byte)6);
				}

				if (!player.isCreative())
					itemstack.shrink(1);
				return true;
			}
		}

		if (itemstack.getItem() == Item.getItemFromBlock(TinyPotato.tiny_potato)) {
			this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1F, 0.5F + (float) Math.random() * 0.5F);
			if (!player.isCreative())
				itemstack.shrink(1);

			this.timeUntilPotatoEmerges = 1201;

			return true;
		}

		if (!world.isRemote) {
			setWoke();
		}

		return super.processInteract(player, hand);
	}

	@Override
	public boolean canMateWith(EntityAnimal otherAnimal) {
		return super.canMateWith(otherAnimal) && otherAnimal instanceof EntityFoxhound;
	}

	@Override
	public EntityWolf createChild(EntityAgeable otherParent) {
		EntityWolf entitywolf = new EntityFoxhound(this.world);
		UUID uuid = this.getOwnerId();

		if (uuid != null) {
			entitywolf.setOwnerId(uuid);
			entitywolf.setTamed(true);
		}

		return entitywolf;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("OhLawdHeComin", timeUntilPotatoEmerges);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		timeUntilPotatoEmerges = compound.getInteger("OhLawdHeComin");
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return isSleeping() ? null : super.getAmbientSound();
	}

	public boolean isSleeping() {
		return dataManager.get(SLEEPING);
	}

	public void setSleeping(boolean sleeping) {
		dataManager.set(SLEEPING, sleeping);
	}

	@Override
	public boolean getCanSpawnHere() {
        IBlockState iblockstate = world.getBlockState((new BlockPos(this)).down());
		
		return world.getDifficulty() != EnumDifficulty.PEACEFUL 
				&& isValidLightLevel() 
				&& getBlockPathWeight(new BlockPos(posX, getEntityBoundingBox().minY, posZ)) >= 0F
				&& iblockstate.canEntitySpawn(this);
	}

	public EntityAISleep getAISleep() {
		return aiSleep;
	}

	private void setWoke() {
		EntityAISleep sleep = getAISleep();
		if(sleep != null) {
			setSleeping(false);
			sleep.setSleeping(false);
		}
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return 0.5F - this.world.getLightBrightness(pos);
	}

	protected boolean isValidLightLevel() {
		BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
		int i = world.getLightFromNeighbors(blockpos);
		return i < 8;
	}

}
