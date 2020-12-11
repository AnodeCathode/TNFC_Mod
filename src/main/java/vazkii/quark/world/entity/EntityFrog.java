package vazkii.quark.world.entity;

import com.google.common.collect.Sets;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.base.util.CommonReflectiveAccessor;
import vazkii.quark.base.util.EntityOpacityHandler;
import vazkii.quark.world.entity.ai.EntityAIFavorBlock;
import vazkii.quark.world.entity.ai.EntityAIPassenger;
import vazkii.quark.world.entity.ai.EntityAITemptButNice;
import vazkii.quark.world.feature.Frogs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Set;

public class EntityFrog extends EntityAnimal {

	public static final ResourceLocation FROG_LOOT_TABLE = new ResourceLocation("quark", "entities/frog");

	private static final DataParameter<Integer> TALK_TIME = EntityDataManager.createKey(EntityFrog.class, DataSerializers.VARINT);
	private static final DataParameter<Float> SIZE_MODIFIER = EntityDataManager.createKey(EntityFrog.class, DataSerializers.FLOAT);
	private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(Items.FISH, Items.SPIDER_EYE);
	private static final Set<Item> TEMPTATION_ITEMS_BUT_NICE = Sets.newHashSet(Items.FISH, Items.SPIDER_EYE, Items.CLOCK);

	public int spawnCd = -1;
	public int spawnChain = 30;

	public boolean isDuplicate;

	public EntityFrog(World worldIn) {
		this(worldIn, 1);
	}

	public EntityFrog(World worldIn, float sizeModifier) {
		super(worldIn);
		if (sizeModifier != 1) {
			setSize(0.65f * sizeModifier, 0.5f * sizeModifier);
			dataManager.set(SIZE_MODIFIER, sizeModifier);
		}

		this.jumpHelper = new FrogJumpHelper();
		this.moveHelper = new FrogMoveHelper();
		this.setMovementSpeed(0.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(TALK_TIME, 0);
		dataManager.register(SIZE_MODIFIER, 1f);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAIPassenger(this));
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new AIPanic(1.25));
		tasks.addTask(3, new EntityAIMate(this, 1.0));
		tasks.addTask(4, new EntityAITemptButNice(this, 1.2, false, TEMPTATION_ITEMS, TEMPTATION_ITEMS_BUT_NICE));
		tasks.addTask(5, new EntityAIFollowParent(this, 1.1));
		tasks.addTask(6, new EntityAIFavorBlock(this, 1, Blocks.WATERLILY));
		tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1, 0.5F));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6));
		tasks.addTask(9, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		// NO-OP
	}

	@Override
	public float getEyeHeight() {
		return 0.1f * getSizeModifier();
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		return EntityOpacityHandler.isEntityInsideOpaqueBlock(this);
	}

	public int getTalkTime() {
		return dataManager.get(TALK_TIME);
	}

	public float getSizeModifier() {
		return dataManager.get(SIZE_MODIFIER);
	}

	@Override
	public void onUpdate() {
		float sizeModifier = getSizeModifier();
		if (height != sizeModifier * 0.5f)
			setSize(0.65f * sizeModifier, 0.5f * sizeModifier);

		super.onUpdate();

		int talkTime = getTalkTime();
		if (talkTime > 0)
			dataManager.set(TALK_TIME, talkTime - 1);

		if (Frogs.frogsDoTheFunny && spawnCd > 0 && spawnChain > 0) {
			spawnCd--;
			if (spawnCd == 0 && !world.isRemote) {
				float multiplier = 0.8F;
				EntityFrog newFrog = new EntityFrog(world);
				newFrog.setPosition(posX, posY, posZ);
				newFrog.motionX = (Math.random() - 0.5) * multiplier;
				newFrog.motionY = (Math.random() - 0.5) * multiplier;
				newFrog.motionZ = (Math.random() - 0.5) * multiplier;
				newFrog.isDuplicate = true;
				newFrog.spawnCd = 2;
				newFrog.spawnChain = spawnChain - 1;
				world.spawnEntity(newFrog);
				spawnChain = 0;
			}
		}

		this.prevRotationYaw = this.prevRotationYawHead;
		this.rotationYaw = this.rotationYawHead;
	}

	@Override
	protected boolean canDropLoot() {
		return !isDuplicate && super.canDropLoot();
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return FROG_LOOT_TABLE;
	}

	private int droppedLegs = -1;

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		droppedLegs = 0;
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
		droppedLegs = -1;
	}

	@Nullable
	@Override
	public EntityItem entityDropItem(ItemStack stack, float offsetY) {
		if (droppedLegs >= 0) {
			int count = Math.max(4 - droppedLegs, 0);
			droppedLegs += stack.getCount();

			if (stack.getCount() > count) {
				ItemStack copy = stack.copy();
				copy.shrink(count);
				copy.getOrCreateSubCompound("display")
						.setString("LocName", "item.quark:frog_maybe_leg.name");

				stack = stack.copy();
				stack.shrink(copy.getCount());

				super.entityDropItem(copy, offsetY);
			}
		}

		return super.entityDropItem(stack, offsetY);
	}

	@Override
	public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand) {
		if (super.processInteract(player, hand))
			return true;

		Calendar calendar = world.getCurrentDate();
		if (Frogs.frogsDoTheFunny && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
			if (!world.isRemote && spawnChain > 0 && !isDuplicate) {
				spawnCd = 50;
				dataManager.set(TALK_TIME, 80);
				world.playSound(null, posX, posY, posZ, QuarkSounds.ENTITY_FROG_WEDNESDAY, SoundCategory.NEUTRAL, 1F, 1F);
			}

			return true;
		}

		return false;
	}

	@Nullable
	@Override
	public EntityAgeable createChild(@Nonnull EntityAgeable otherParent) {
		if (isDuplicate)
			return null;

		float sizeMod = getSizeModifier();
		if (otherParent instanceof EntityFrog) {
			if (((EntityFrog) otherParent).isDuplicate)
				return null;

			sizeMod += ((EntityFrog) otherParent).getSizeModifier();
			sizeMod /= 2;
		}

		double regression = rand.nextGaussian() / 20;
		regression *= Math.abs((sizeMod + regression) / sizeMod);



		return new EntityFrog(world, MathHelper.clamp(sizeMod + (float) regression, 0.25f, 2.0f));
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		Calendar calendar = world.getCurrentDate();
		return !stack.isEmpty() &&
				(Frogs.frogsDoTheFunny && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ?
						TEMPTATION_ITEMS_BUT_NICE : TEMPTATION_ITEMS).contains(stack.getItem());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		spawnCd = compound.getInteger("Cooldown");
		if (compound.hasKey("Chain"))
			spawnChain = compound.getInteger("Chain");
		dataManager.set(TALK_TIME, compound.getInteger("DudeAmount"));

		float sizeModifier = compound.hasKey("FrogAmount") ? compound.getFloat("FrogAmount") : 1f;
		dataManager.set(SIZE_MODIFIER, sizeModifier);
		setSize(0.65f * sizeModifier, 0.5f * sizeModifier);

		isDuplicate = compound.getBoolean("FakeFrog");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setFloat("FrogAmount", getSizeModifier());
		compound.setInteger("Cooldown", spawnCd);
		compound.setInteger("Chain", spawnChain);
		compound.setInteger("DudeAmount", getTalkTime());
		compound.setBoolean("FakeFrog", isDuplicate);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return QuarkSounds.ENTITY_FROG_IDLE;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return QuarkSounds.ENTITY_FROG_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return QuarkSounds.ENTITY_FROG_DIE;
	}

	protected SoundEvent getJumpSound() {
		return QuarkSounds.ENTITY_FROG_JUMP;
	}

	// Begin copypasta from EntityRabbit

	private int jumpTicks;
	private int jumpDuration;
	private boolean wasOnGround;
	private int currentMoveTypeDuration;

	@Override
	public void updateAITasks() {
		if (this.currentMoveTypeDuration > 0) --this.currentMoveTypeDuration;

		if (this.onGround) {
			if (!this.wasOnGround) {
				this.setJumping(false);
				this.checkLandingDelay();
			}

			FrogJumpHelper jumpHelper = (FrogJumpHelper) this.jumpHelper;

			if (!jumpHelper.getIsJumping()) {
				if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0) {
					Path path = this.navigator.getPath();
					Vec3d vec3d = new Vec3d(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());

					if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength())
						vec3d = path.getPosition(this);

					this.calculateRotationYaw(vec3d.x, vec3d.z);
					this.startJumping();
				}
			} else if (!jumpHelper.canJump()) this.enableJumpControl();
		}

		this.wasOnGround = this.onGround;
	}

	@Override
	public void spawnRunningParticles() {
		// NO-OP
	}

	private void calculateRotationYaw(double x, double z) {
		this.rotationYaw = (float) (MathHelper.atan2(z - this.posZ, x - this.posX) * (180D / Math.PI)) - 90.0F;
	}

	private void enableJumpControl() {
		((FrogJumpHelper) this.jumpHelper).setCanJump(true);
	}

	private void disableJumpControl() {
		((FrogJumpHelper) this.jumpHelper).setCanJump(false);
	}

	private void updateMoveTypeDuration() {
		if (this.moveHelper.getSpeed() < 2.2D)
			this.currentMoveTypeDuration = 10;
		else
			this.currentMoveTypeDuration = 1;
	}

	private void checkLandingDelay() {
		this.updateMoveTypeDuration();
		this.disableJumpControl();
	}

	@Override
	public void onLivingUpdate() {
		EntityLiving steed = null;
		double speed = 1.0;
		Path path = null;

		if (this.isRiding() && this.getRidingEntity() instanceof EntityLiving) {
			steed = (EntityLiving)this.getRidingEntity();
			speed = CommonReflectiveAccessor.getSpeed(steed.getNavigator());
			path = steed.getNavigator().getPath();
			this.getMoveHelper().read(steed.getMoveHelper());
		}
		super.onLivingUpdate();

		if (this.jumpTicks != this.jumpDuration) ++this.jumpTicks;
		else if (this.jumpDuration != 0) {
			this.jumpTicks = 0;
			this.jumpDuration = 0;
			this.setJumping(false);
		}

		if (steed != null)
			steed.getNavigator().setPath(path, speed);
	}

	@Override
	protected void jump() {
		super.jump();
		double d0 = this.moveHelper.getSpeed();

		if (d0 > 0.0D) {
			double d1 = this.motionX * this.motionX + this.motionZ * this.motionZ;

			if (d1 < 0.01) this.moveRelative(0.0F, 0.0F, 1.0F, 0.1F);
		}

		if (!this.world.isRemote)
			this.world.setEntityState(this, (byte) 1);
	}

	public void setMovementSpeed(double newSpeed) {
		this.getNavigator().setSpeed(newSpeed);
		this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), newSpeed);
	}

	@Override
	public void setJumping(boolean jumping) {
		super.setJumping(jumping);

		if (jumping)
			this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
	}

	public void startJumping() {
		this.setJumping(true);
		this.jumpDuration = 10;
		this.jumpTicks = 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 1) {
			this.createRunningParticles();
			this.jumpDuration = 10;
			this.jumpTicks = 0;
		} else
			super.handleStatusUpdate(id);
	}

	public class FrogJumpHelper extends EntityJumpHelper {
		private boolean canJump;

		public FrogJumpHelper() {
			super(EntityFrog.this);
		}

		public boolean getIsJumping() {
			return this.isJumping;
		}

		public boolean canJump() {
			return this.canJump;
		}

		public void setCanJump(boolean canJumpIn) {
			this.canJump = canJumpIn;
		}

		@Override
		public void doJump() {
			if (this.isJumping) {
				startJumping();
				this.isJumping = false;
			}
		}
	}

	public class FrogMoveHelper extends EntityMoveHelper {
		private double nextJumpSpeed;

		public FrogMoveHelper() {
			super(EntityFrog.this);
		}

		@Override
		public void onUpdateMoveHelper() {
			if (onGround && !isJumping && !((EntityFrog.FrogJumpHelper) jumpHelper).getIsJumping())
				setMovementSpeed(0.0D);
			else if (this.isUpdating()) setMovementSpeed(this.nextJumpSpeed);

			super.onUpdateMoveHelper();
		}

		@Override
		public void setMoveTo(double x, double y, double z, double speedIn) {
			if (isInWater()) speedIn = 1.5D;

			super.setMoveTo(x, y, z, speedIn);

			if (speedIn > 0.0D) this.nextJumpSpeed = speedIn;
		}
	}

	public class AIPanic extends EntityAIPanic {

		public AIPanic(double speedIn) {
			super(EntityFrog.this, speedIn);
		}

		public void updateTask() {
			super.updateTask();
			setMovementSpeed(this.speed);
		}
	}
}
