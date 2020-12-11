/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 03:49:22 (GMT)]
 */
package vazkii.quark.world.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import vazkii.quark.world.feature.Wraiths;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityWraith extends EntityZombie {

	public static final ResourceLocation LOOT_TABLE = new ResourceLocation("quark:entities/wraith");

	private static final DataParameter<String> IDLE_SOUND = EntityDataManager.createKey(EntityWraith.class, DataSerializers.STRING);
	private static final DataParameter<String> HURT_SOUND = EntityDataManager.createKey(EntityWraith.class, DataSerializers.STRING);
	private static final DataParameter<String> DEATH_SOUND = EntityDataManager.createKey(EntityWraith.class, DataSerializers.STRING);
	private static final String TAG_IDLE_SOUND = "IdleSound";
	private static final String TAG_HURT_SOUND = "HurtSound";
	private static final String TAG_DEATH_SOUND = "DeathSound";

	public EntityWraith(World worldIn) {
		super(worldIn);
		isImmuneToFire = true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(IDLE_SOUND, "");
		dataManager.register(HURT_SOUND, "");
		dataManager.register(DEATH_SOUND, "");
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0);
		getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(@Nonnull DifficultyInstance difficulty) {
		// NO-OP
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return getSound(IDLE_SOUND);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return getSound(HURT_SOUND);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getSound(DEATH_SOUND);
	}

	@Override
	protected float getSoundPitch() {
		return rand.nextFloat() * 0.1F + 0.75F;
	}

	public SoundEvent getSound(DataParameter<String> param) {
		ResourceLocation loc = new ResourceLocation(dataManager.get(param));

		return SoundEvent.REGISTRY.getObject(loc);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		AxisAlignedBB aabb = getEntityBoundingBox();
		double x = aabb.minX + Math.random() * (aabb.maxX - aabb.minX);
		double y = aabb.minY + Math.random() * (aabb.maxY - aabb.minY);
		double z = aabb.minZ + Math.random() * (aabb.maxZ - aabb.minZ);
		getEntityWorld().spawnParticle(EnumParticleTypes.TOWN_AURA, x, y, z, 0, 0, 0);
	}


	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean did = super.attackEntityAsMob(entityIn);
		if(did) {
			if(entityIn instanceof EntityLivingBase)
				((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));

			double dx = posX - entityIn.posX;
			double dz = posZ - entityIn.posZ;
			Vec3d vec = new Vec3d(dx, 0, dz).normalize().add(0, 0.5, 0).normalize().scale(0.85);
			motionX = vec.x;
			motionY = vec.y;
			motionZ = vec.z;
		}

		return did;
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
		int idx = rand.nextInt(Wraiths.validWraithSounds.size());
		String sound = Wraiths.validWraithSounds.get(idx);
		String[] split = sound.split("\\|");

		dataManager.set(IDLE_SOUND, split[0]);
		dataManager.set(HURT_SOUND, split[1]);
		dataManager.set(DEATH_SOUND, split[2]);

		return super.onInitialSpawn(difficulty, data);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		// NO-OP
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		compound.setString(TAG_IDLE_SOUND, dataManager.get(IDLE_SOUND));
		compound.setString(TAG_HURT_SOUND, dataManager.get(HURT_SOUND));
		compound.setString(TAG_DEATH_SOUND, dataManager.get(DEATH_SOUND));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		dataManager.set(IDLE_SOUND, compound.getString(TAG_IDLE_SOUND));
		dataManager.set(HURT_SOUND, compound.getString(TAG_HURT_SOUND));
		dataManager.set(DEATH_SOUND, compound.getString(TAG_DEATH_SOUND));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	@Override
	public void setChild(boolean childZombie) {
		// NO-OP
	}

	@Override
	public boolean isChild() {
		return false;
	}

	@Override
	public boolean getCanSpawnHere() {
		BlockPos blockpos = new BlockPos(posX, getEntityBoundingBox().minY - 1, posZ);
		return super.getCanSpawnHere() && getEntityWorld().getBlockState(blockpos).getBlock() == Blocks.SOUL_SAND;
	}

}
