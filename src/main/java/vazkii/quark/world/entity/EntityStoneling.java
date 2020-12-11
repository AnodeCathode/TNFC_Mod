package vazkii.quark.world.entity;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.world.base.EnumStonelingVariant;
import vazkii.quark.world.entity.ai.EntityAIActWary;
import vazkii.quark.world.entity.ai.EntityAIFavorBlock;
import vazkii.quark.world.entity.ai.EntityAIRunAndPoof;
import vazkii.quark.world.feature.Frogs;
import vazkii.quark.world.feature.Stonelings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EntityStoneling extends EntityCreature {

	public static final ResourceLocation CARRY_LOOT_TABLE = new ResourceLocation("quark", "entities/stoneling_carry");
	public static final ResourceLocation LOOT_TABLE = new ResourceLocation("quark", "entities/stoneling");

	private static final DataParameter<ItemStack> CARRYING_ITEM = EntityDataManager.createKey(EntityStoneling.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Byte> VARIANT = EntityDataManager.createKey(EntityStoneling.class, DataSerializers.BYTE);
	private static final DataParameter<Float> HOLD_ANGLE = EntityDataManager.createKey(EntityStoneling.class, DataSerializers.FLOAT);

	private static final String TAG_CARRYING_ITEM = "carryingItem";
	private static final String TAG_VARIANT = "variant";
	private static final String TAG_HOLD_ANGLE = "itemAngle";
	private static final String TAG_PLAYER_MADE = "playerMade";

	private EntityAIActWary waryTask;

	private boolean isTame;

	public EntityStoneling(World worldIn) {
		super(worldIn);
		this.setPathPriority(PathNodeType.DAMAGE_CACTUS, 1.0F);
		this.setPathPriority(PathNodeType.DANGER_CACTUS, 1.0F);
		setSize(0.5F, 0.9F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(CARRYING_ITEM, ItemStack.EMPTY);
		dataManager.register(VARIANT, (byte) 0);
		dataManager.register(HOLD_ANGLE, 0F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.2, 0.98F));
		tasks.addTask(4, new EntityAIFavorBlock(this, 0.2, Blocks.DIAMOND_ORE));

		if(Stonelings.enableDiamondHeart || Stonelings.tamableStonelings) {
			int priority = Stonelings.tamableStonelings ? 0 : 3;
			tasks.addTask(priority, new EntityAITempt(this, 0.6, Items.DIAMOND, false));
		}

		tasks.addTask(2, new EntityAIRunAndPoof<>(this, EntityPlayer.class, 4, 0.5, 0.5));
		tasks.addTask(1, waryTask = new EntityAIActWary(this, 0.1, 6, Stonelings.cautiousStonelings));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
	}


	@Override
	public void onUpdate() {
		super.onUpdate();

		if (inWater)
			stepHeight = 1F;
		else
			stepHeight = 0.6F;

		if (!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL && !isTame)
			setDead();

		this.prevRenderYawOffset = this.prevRotationYaw;
		this.renderYawOffset = this.rotationYaw;
	}

	@Override
	public boolean isCreatureType(@Nonnull EnumCreatureType type, boolean forSpawnCount) {
		if (isTame)
			return type == EnumCreatureType.CREATURE;
		return type == EnumCreatureType.MONSTER;
	}

	@Override
	protected boolean canDespawn() {
		return !isTame;
	}

	private static boolean fitsOreKey(ItemStack stack, String key) {
		if (stack.isEmpty())
			return false;

		int[] ids = OreDictionary.getOreIDs(stack);
		int oreKey = OreDictionary.getOreID(key);
		for (int id : ids) {
			if (id == oreKey)
				return true;
		}
		return false;
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(!stack.isEmpty() && stack.getItem() == Items.NAME_TAG) {
			stack.interactWithEntity(player, this, hand);
			return true;
		} else
			return super.processInteract(player, hand);
	}

	@Nonnull
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		if(hand == EnumHand.MAIN_HAND) {
			ItemStack playerItem = player.getHeldItem(hand);

			if(!world.isRemote) {
				if (isPlayerMade()) {
					if (!player.isSneaking() && !playerItem.isEmpty()) {

						EnumStonelingVariant currentVariant = getVariant();
						EnumStonelingVariant targetVariant = null;
						for (EnumStonelingVariant variant : EnumStonelingVariant.values()) {
							if (fitsOreKey(playerItem, variant.getOreKey()))
								targetVariant = variant;
						}

						if (targetVariant != null) {
							if (world instanceof WorldServer) {
								((WorldServer) world).spawnParticle(EnumParticleTypes.HEART, posX, posY + height, posZ, 1, 0.1, 0.1, 0.1, 0.1);
								if (targetVariant != currentVariant)
									((WorldServer) world).spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX, posY + height / 2, posZ, 16, 0.1, 0.1, 0.1, 0.25,
											Block.getStateId(targetVariant.getDisplayState()));
							}

							if (targetVariant != currentVariant) {
								playSound(QuarkSounds.ENTITY_STONELING_EAT, 1F, 1F);
								dataManager.set(VARIANT, targetVariant.getIndex());
							}

							playSound(QuarkSounds.ENTITY_STONELING_PURR, 1F, 1F + world.rand.nextFloat() * 1F);

							heal(1);

							if (!player.capabilities.isCreativeMode)
								playerItem.shrink(1);

							return EnumActionResult.SUCCESS;
						}

						return EnumActionResult.PASS;
					}

					ItemStack stonelingItem = dataManager.get(CARRYING_ITEM);

					if (!stonelingItem.isEmpty() || !playerItem.isEmpty()) {
						player.setHeldItem(hand, stonelingItem.copy());
						dataManager.set(CARRYING_ITEM, playerItem.copy());

						if (playerItem.isEmpty())
							playSound(QuarkSounds.ENTITY_STONELING_GIVE, 1F, 1F);
						else playSound(QuarkSounds.ENTITY_STONELING_TAKE, 1F, 1F);
					}
				} else if (Stonelings.tamableStonelings && fitsOreKey(playerItem, "gemDiamond")) {
					heal(8);

					setPlayerMade(true);

					playSound(QuarkSounds.ENTITY_STONELING_PURR, 1F, 1F + world.rand.nextFloat() * 1F);

					if (!player.capabilities.isCreativeMode)
						playerItem.shrink(1);

					if (world instanceof WorldServer)
						((WorldServer) world).spawnParticle(EnumParticleTypes.HEART, posX, posY + height, posZ, 4, 0.1, 0.1, 0.1, 0.1);

					return EnumActionResult.SUCCESS;
				}
			}
		}

		return EnumActionResult.PASS;
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
		byte variant;
		if (data instanceof EnumStonelingVariant)
			variant = ((EnumStonelingVariant) data).getIndex();
		else
			variant = (byte) world.rand.nextInt(EnumStonelingVariant.values().length);

		dataManager.set(VARIANT, variant);
		dataManager.set(HOLD_ANGLE, world.rand.nextFloat() * 90 - 45);

		if(!isTame && !world.isRemote) {
			if (ModuleLoader.isFeatureEnabled(Frogs.class) && rand.nextDouble() < 0.01) {
				EntityFrog frog = new EntityFrog(world, 0.25f);
				frog.setPosition(posX, posY, posZ);
				world.spawnEntity(frog);
				frog.startRiding(this);
			} else {
				List<ItemStack> items = world.getLootTableManager().getLootTableFromLocation(CARRY_LOOT_TABLE).generateLootForPools(rand, new LootContext.Builder((WorldServer) world).build());
				if (!items.isEmpty())
					dataManager.set(CARRYING_ITEM, items.get(0));
			}
		}

		return super.onInitialSpawn(difficulty, data);
	}

	@Override
	public boolean isEntityInvulnerable(@Nonnull DamageSource source) {
		return source == DamageSource.CACTUS || source.isProjectile() || super.isEntityInvulnerable(source);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean isNotColliding() {
		return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
	}

	@Override
	public double getMountedYOffset() {
		return this.height;
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		// NO-OP
	}

	@Override
	protected void damageEntity(@Nonnull DamageSource damageSrc, float damageAmount) {
		super.damageEntity(damageSrc, damageAmount);

		if(!isPlayerMade() && damageSrc.getTrueSource() instanceof EntityPlayer) {
			startle();
			for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(this,
					getEntityBoundingBox().grow(16))) {
				if (entity instanceof EntityStoneling) {
					EntityStoneling stoneling = (EntityStoneling) entity;
					if (!stoneling.isPlayerMade() && stoneling.getEntitySenses().canSee(this)) {
						startle();
					}
				}
			}
		}
	}

	public void startle() {
		waryTask.startle();
		Set<EntityAITasks.EntityAITaskEntry> entries = Sets.newHashSet(tasks.taskEntries);

		for (EntityAITasks.EntityAITaskEntry task : entries)
			if (task.action instanceof EntityAITempt)
				tasks.removeTask(task.action);
	}

	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
		super.dropEquipment(wasRecentlyHit, lootingModifier);

		ItemStack stack = getCarryingItem();
		if(!stack.isEmpty())
			entityDropItem(stack, 0F);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return Stonelings.enableDiamondHeart ? LOOT_TABLE : null;
	}

	public void setPlayerMade(boolean value) {
		isTame = value;
	}

	public ItemStack getCarryingItem() {
		return dataManager.get(CARRYING_ITEM);
	}

	public EnumStonelingVariant getVariant() {
		return EnumStonelingVariant.byIndex(dataManager.get(VARIANT));
	}

	public float getItemAngle() {
		return dataManager.get(HOLD_ANGLE);
	}

	public boolean isPlayerMade() {
		return isTame;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		if(compound.hasKey(TAG_CARRYING_ITEM, 10)) {
			NBTTagCompound itemCmp = compound.getCompoundTag(TAG_CARRYING_ITEM);
			ItemStack stack = new ItemStack(itemCmp);
			dataManager.set(CARRYING_ITEM, stack);
		}

		dataManager.set(VARIANT, compound.getByte(TAG_VARIANT));
		dataManager.set(HOLD_ANGLE, compound.getFloat(TAG_HOLD_ANGLE));
		setPlayerMade(compound.getBoolean(TAG_PLAYER_MADE));
	}

	@Override
	public boolean canEntityBeSeen(Entity entityIn) {
		Vec3d origin = new Vec3d(posX, posY + getEyeHeight(), posZ);
		Vec3d targetBase = new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ);
		float otherEyes = entityIn.getEyeHeight();
		for (float height = 0; height <= otherEyes; height += otherEyes / 8) {
			if (this.world.rayTraceBlocks(origin, targetBase.add(0, height, 0), false, true, false) == null)
				return true;
		}

		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		compound.setTag(TAG_CARRYING_ITEM, getCarryingItem().serializeNBT());

		compound.setByte(TAG_VARIANT, getVariant().getIndex());
		compound.setFloat(TAG_HOLD_ANGLE, getItemAngle());
		compound.setBoolean(TAG_PLAYER_MADE, isPlayerMade());
	}

	@Override
	public boolean getCanSpawnHere() {
		IBlockState state = this.world.getBlockState((new BlockPos(this)).down());
		if (state.getMaterial() != Material.ROCK)
			return false;
		return Stonelings.dimensions.canSpawnHere(world) && posY < Stonelings.maxYLevel && isValidLightLevel() && super.getCanSpawnHere();
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return QuarkSounds.ENTITY_STONELING_CRY;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return QuarkSounds.ENTITY_STONELING_DIE;
	}

	@Override
	public int getTalkInterval() {
		return 1200;
	}

	@Override
	public void playLivingSound() {
		SoundEvent sound = this.getAmbientSound();

		if (sound != null) this.playSound(sound, this.getSoundVolume(), 1f);
	}

	@Override
	public void setDead() {
		super.setDead();
		for (Entity passenger : getRecursivePassengers())
			if (!(passenger instanceof EntityPlayer))
				passenger.setDead();
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (hasCustomName()) {
			String customName = getCustomNameTag();
			if (customName.equalsIgnoreCase("michael stevens") || customName.equalsIgnoreCase("vsauce"))
				return QuarkSounds.ENTITY_STONELING_MICHAEL;
		}

		return null;
	}

	// Vanilla copy pasta from EntityMob
	protected boolean isValidLightLevel() {
		BlockPos blockpos = new BlockPos(posX, getEntityBoundingBox().minY, posZ);

		if(world.getLightFor(EnumSkyBlock.SKY, blockpos) != 0)
			return false;
		else {
			int i = world.getLightFromNeighbors(blockpos);

			if (world.isThundering()) {
				int j = world.getSkylightSubtracted();
				world.setSkylightSubtracted(10);
				i = world.getLightFromNeighbors(blockpos);
				world.setSkylightSubtracted(j);
			}

			return i <= rand.nextInt(8);
		}
	}

}
