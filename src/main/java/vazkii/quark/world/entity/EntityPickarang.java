package vazkii.quark.world.entity;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.misc.feature.Pickarang;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityPickarang extends EntityThrowable {

	private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(EntityPickarang.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Boolean> RETURNING = EntityDataManager.createKey(EntityPickarang.class, DataSerializers.BOOLEAN);

	private int liveTime;
	private int slot;

	private static final ThreadLocal<Boolean> IS_PICKARANG_UPDATING = ThreadLocal.withInitial(() -> false);

	private static final String TAG_RETURNING = "returning";
	private static final String TAG_LIVE_TIME = "liveTime";
	private static final String TAG_RETURN_SLOT = "returnSlot";
	private static final String TAG_ITEM_STACK = "itemStack";

	public EntityPickarang(World worldIn) {
		super(worldIn);
	}
	
    public EntityPickarang(World worldIn, EntityLivingBase throwerIn) {
    	super(worldIn, throwerIn);
    	this.setPosition(posX, throwerIn.posY + throwerIn.getEyeHeight(), posZ);
    }
    
    public void setThrowData(int slot, ItemStack stack) {
    	this.slot = slot;
    	setStack(stack.copy());
    }
    
    @Override
    protected void entityInit() {
    	super.entityInit();

		dataManager.register(STACK, new ItemStack(Pickarang.pickarang));
    	dataManager.register(RETURNING, false);
    }

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if(dataManager.get(RETURNING) || world.isRemote)
			return;
		
		EntityLivingBase owner = getThrower();

		if(result.typeOfHit == Type.BLOCK) {
			dataManager.set(RETURNING, true);
			
			if(!(owner instanceof EntityPlayerMP))
				return;
			
			EntityPlayerMP player = (EntityPlayerMP) owner;
			BlockPos hit = result.getBlockPos();

			IBlockState state = world.getBlockState(hit);
			float hardness = state.getBlockHardness(world, hit);
			if (hardness <= Pickarang.maxHardness && hardness >= 0) {
				ItemStack prev = player.getHeldItemMainhand();
				player.setHeldItem(EnumHand.MAIN_HAND, getStack());

				if (player.interactionManager.tryHarvestBlock(hit))
					world.playEvent(null, 2001, hit, Block.getStateId(state));
				else
					playSound(QuarkSounds.ENTITY_PICKARANG_CLANK, 1, 1);

				setStack(player.getHeldItemMainhand());

				player.setHeldItem(EnumHand.MAIN_HAND, prev);
			} else
				playSound(QuarkSounds.ENTITY_PICKARANG_CLANK, 1, 1);

		} else if(result.typeOfHit == Type.ENTITY) {
			Entity hit = result.entityHit;
			if(hit != owner) {
				setReturning();
				if (hit instanceof EntityPickarang) {
					((EntityPickarang) hit).setReturning();
					playSound(QuarkSounds.ENTITY_PICKARANG_CLANK, 1, 1);
				} else {

					ItemStack pickarang = getStack();
					Multimap<String, AttributeModifier> modifiers = pickarang.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);

					if (owner != null) {
						ItemStack prev = owner.getHeldItemMainhand();
						owner.setHeldItem(EnumHand.MAIN_HAND, pickarang);
						owner.getAttributeMap().applyAttributeModifiers(modifiers);

						int ticksSinceLastSwing = ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, owner, "field_184617_aD");
						int cooldownPeriod = (int) (1.0 / owner.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0);
						ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, owner, cooldownPeriod, "field_184617_aD");

						Pickarang.setActivePickarang(this);

						if (owner instanceof EntityPlayer)
							((EntityPlayer) owner).attackTargetEntityWithCurrentItem(hit);
						else
							owner.attackEntityAsMob(hit);

						Pickarang.setActivePickarang(null);

						ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, owner, ticksSinceLastSwing, "field_184617_aD");

						setStack(owner.getHeldItemMainhand());
						owner.setHeldItem(EnumHand.MAIN_HAND, prev);
						owner.getAttributeMap().removeAttributeModifiers(modifiers);
					} else {
						AttributeMap map = new AttributeMap();
						map.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1);
						map.applyAttributeModifiers(modifiers);
						ItemStack stack = getStack();
						stack.attemptDamageItem(1, world.rand, null);
						setStack(stack);
						hit.attackEntityFrom(new EntityDamageSourceIndirect("player", this, this).setProjectile(),
								(float) map.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
					}

				}
			}
		}
	}

	protected void setReturning() {
		dataManager.set(RETURNING, true);
	}

	@Override
	public boolean canBeCollidedWith() {
		return IS_PICKARANG_UPDATING.get();
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	public void onUpdate() {
		IS_PICKARANG_UPDATING.set(true);
		super.onUpdate();
		IS_PICKARANG_UPDATING.set(false);
		
		if(isDead)
			return;
		
		boolean returning = dataManager.get(RETURNING);
		liveTime++;
		
		if(!returning) {
			if(liveTime > Pickarang.timeout)
				setReturning();
		} else {
			noClip = true;

			ItemStack stack = getStack();
			int eff = getEfficiencyModifier();
			
			List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().grow(2));
			List<EntityXPOrb> xp = world.getEntitiesWithinAABB(EntityXPOrb.class, getEntityBoundingBox().grow(2));

			Vec3d ourPos = getPositionVector();
			for(EntityItem item : items) {
				if (item.isRiding())
					continue;
				item.startRiding(this);
				
				item.setPickupDelay(2);
			}
			for(EntityXPOrb xpOrb : xp) {
				if (xpOrb.isRiding())
					continue;
				xpOrb.startRiding(this);

				xpOrb.delayBeforeCanPickup = 2;
			}


			EntityLivingBase owner = getThrower();
			if(owner == null || owner.isDead || !(owner instanceof EntityPlayer)) {
				if(!world.isRemote) {
					entityDropItem(stack, 0);
					setDead();
				}

				return;
			}
			
			Vec3d ownerPos = owner.getPositionVector().add(0, 1, 0);
			Vec3d motion = ownerPos.subtract(ourPos);
			double motionMag = 3.25 + eff * 0.25;

			if(motion.lengthSquared() < motionMag) {
				EntityPlayer player = (EntityPlayer) owner;
				ItemStack stackInSlot = player.inventory.getStackInSlot(slot);
				
		        if(!world.isRemote) {
		        	playSound(QuarkSounds.ENTITY_PICKARANG_PICKUP, 1, 1);

			        if(!stack.isEmpty()) {
						if(!player.isDead && stackInSlot.isEmpty())
							player.inventory.setInventorySlotContents(slot, stack);
						else if(player.isDead || !player.inventory.addItemStackToInventory(stack))
							player.dropItem(stack, false);
			        }

			        if (!player.isDead) {
						for (EntityItem item : items) {
							ItemStack drop = item.getItem();
							if (!player.addItemStackToInventory(drop))
								player.dropItem(drop, false);
							item.setDead();
						}

						for (EntityXPOrb xpOrb : xp) {
							xpOrb.onCollideWithPlayer(player);
						}

						for (Entity riding : getPassengers()) {
							if (riding.isDead)
								continue;

							if (riding instanceof EntityItem) {
								ItemStack drop = ((EntityItem) riding).getItem();
								if (!player.addItemStackToInventory(drop))
									player.dropItem(drop, false);
								riding.setDead();
							} else if (riding instanceof EntityXPOrb)
								riding.onCollideWithPlayer(player);
						}
					}

					setDead();
		        }
			} else {
				motion = motion.normalize().scale(0.7 + eff * 0.325F);
				motionX = motion.x;
				motionY = motion.y;
				motionZ = motion.z;
			}
		}
	}

	@Override
	protected boolean canFitPassenger(Entity passenger) {
		return super.canFitPassenger(passenger) || passenger instanceof EntityItem || passenger instanceof EntityXPOrb;
	}

	@Override
	public double getMountedYOffset() {
		return 0;
	}

	@Nonnull
	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.PLAYERS;
	}

	public int getEfficiencyModifier() {
		return EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, getStack());
	}

	public ItemStack getStack() {
		return dataManager.get(STACK);
	}

	public void setStack(ItemStack stack) {
		dataManager.set(STACK, stack);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		
		dataManager.set(RETURNING, compound.getBoolean(TAG_RETURNING));
		liveTime = compound.getInteger(TAG_LIVE_TIME);
		slot = compound.getInteger(TAG_RETURN_SLOT);

		if (compound.hasKey(TAG_ITEM_STACK))
			setStack(new ItemStack(compound.getCompoundTag(TAG_ITEM_STACK)));
		else
			setStack(new ItemStack(Pickarang.pickarang));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		
		compound.setBoolean(TAG_RETURNING, dataManager.get(RETURNING));
		compound.setInteger(TAG_LIVE_TIME, liveTime);
		compound.setInteger(TAG_RETURN_SLOT, slot);

		compound.setTag(TAG_ITEM_STACK, getStack().serializeNBT());
	}
	
	@Override
	protected float getGravityVelocity() {
		return 0F;
	}

}
