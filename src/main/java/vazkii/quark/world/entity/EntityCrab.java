/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 19:51 AM (EST)]
 */
package vazkii.quark.world.entity;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.util.EntityOpacityHandler;
import vazkii.quark.world.entity.ai.EntityAIRave;
import vazkii.quark.world.entity.ai.MovementHelperZigZag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EntityCrab extends EntityAnimal {

	public static final ResourceLocation CRAB_LOOT_TABLE = new ResourceLocation("quark", "entities/crab");

	private static final DataParameter<Float> SIZE_MODIFIER = EntityDataManager.createKey(EntityCrab.class, DataSerializers.FLOAT);

	private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(Items.WHEAT, Items.FISH, Items.CHICKEN);

	private static int lightningCooldown;

    private boolean crabRave;
    private BlockPos jukeboxPosition;

	public EntityCrab(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 0.5F);
		this.moveHelper = new MovementHelperZigZag(this);
		
		spawnableBlock = Blocks.SAND;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean isNotColliding()
	{
		return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
	}

	@Nonnull
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(SIZE_MODIFIER, 1f);
	}

	@Override
	public float getEyeHeight() {
		return 0.1f * getSizeModifier();
	}

	public float getSizeModifier() {
		return dataManager.get(SIZE_MODIFIER);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
		this.tasks.addTask(2, new EntityAIRave(this));
		this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(4, new EntityAITempt(this, 1.2D, false, TEMPTATION_ITEMS));
		this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
		this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		return EntityOpacityHandler.isEntityInsideOpaqueBlock(this);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (inWater)
			stepHeight = 1F;
		else
			stepHeight = 0.6F;

		if (lightningCooldown > 0)
			lightningCooldown--;
		
        if(isRaving() && (jukeboxPosition == null || jukeboxPosition.distanceSq(posX, posY, posZ) > 24.0D || world.getBlockState(jukeboxPosition).getBlock() != Blocks.JUKEBOX))
        	party(null, false);

		float sizeModifier = getSizeModifier();
		if (height != sizeModifier * 0.5f)
			setSize(0.9f * sizeModifier, 0.5f * sizeModifier);
		
		if(isRaving() && world.isRemote && ticksExisted % 10 == 0) {
			BlockPos below = getPosition().down();
			IBlockState belowState = world.getBlockState(below);
			if(belowState.getMaterial() == Material.SAND)
				world.playEvent(2001, below, Block.getStateId(belowState));
		}
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
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {
		if (lightningCooldown > 0)
			return;

		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("Lightning Bonus", 0.5, 1));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier("Lightning Bonus", 0.125, 1));
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(new AttributeModifier("Lightning Bonus", 1, 1));
		float sizeModifier = Math.min(getSizeModifier() * 2, 16);
		this.dataManager.set(SIZE_MODIFIER, sizeModifier);
		setSize(0.9f * sizeModifier, 0.5f * sizeModifier);

		lightningCooldown = 100;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {
		super.collideWithEntity(entityIn);
		if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityCrab))
			entityIn.attackEntityFrom(DamageSource.CACTUS, 1f);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return !stack.isEmpty() && TEMPTATION_ITEMS.contains(stack.getItem());
	}

	@Nullable
	@Override
	public EntityAgeable createChild(@Nonnull EntityAgeable other) {
		return new EntityCrab(world);
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return CRAB_LOOT_TABLE;
	}


	public void party(BlockPos pos, boolean isPartying) {
		// A separate method, due to setPartying being side-only.
		jukeboxPosition = pos;
		crabRave = isPartying;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void setPartying(BlockPos pos, boolean isPartying) {
		party(pos, isPartying);
    }

	public boolean isRaving() {
        return crabRave;
    }

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		lightningCooldown = compound.getInteger("LightningCooldown");

		if (compound.hasKey("EnemyCrabRating")) {
			float sizeModifier = compound.getFloat("EnemyCrabRating");
			dataManager.set(SIZE_MODIFIER, sizeModifier);
			setSize(0.5f * sizeModifier, 0.9f * sizeModifier);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setFloat("EnemyCrabRating", getSizeModifier());
		compound.setInteger("LightningCooldown", lightningCooldown);
	}
	
}
