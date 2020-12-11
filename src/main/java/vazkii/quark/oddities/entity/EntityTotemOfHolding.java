package vazkii.quark.oddities.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.oddities.feature.TotemOfHolding;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class EntityTotemOfHolding extends Entity {

	private static final String TAG_ITEMS = "storedItems";
	private static final String TAG_DYING = "dying";
	private static final String TAG_OWNER = "owner";
	
	private static final DataParameter<Boolean> DYING = EntityDataManager.createKey(EntityTotemOfHolding.class, DataSerializers.BOOLEAN);
	
	public static final int DEATH_TIME = 40;

	private int deathTicks = 0;
	private String owner;
	private List<ItemStack> storedItems = new LinkedList<>();

	public EntityTotemOfHolding(World worldIn) {
		super(worldIn);
		isImmuneToFire = true;
		setSize(0.5F, 1F);
	}
	
	@Override
	protected void entityInit() { 
		dataManager.register(DYING, false);
	}

	public void addItem(ItemStack stack) {
		storedItems.add(stack);
	}
	
	public void setOwner(EntityPlayer player) {
		owner = EntityPlayer.getUUID(player.getGameProfile()).toString();
	}
	
	EntityPlayer getOwnerEntity() {
		for(EntityPlayer player : world.playerEntities) {
			String uuid = EntityPlayer.getUUID(player.getGameProfile()).toString();
			if(uuid.equals(owner))
				return player;
		}
		
		return null;
	}

	@Override
	public boolean hitByEntity(Entity e) {
		if(!world.isRemote && e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e;
			
			if(!TotemOfHolding.anyoneCollect && !player.isCreative()) {
				EntityPlayer owner = getOwnerEntity();
				if(e != owner)
					return false;
			}
			
			int drops = Math.min(storedItems.size(), 3 + world.rand.nextInt(4));
			
			for(int i = 0; i < drops; i++) {
				ItemStack stack = storedItems.remove(0);
				
				if(stack.getItem() instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) stack.getItem();
					EntityEquipmentSlot slot = armor.armorType;
					ItemStack curr = player.getItemStackFromSlot(slot);
					
					if(curr.isEmpty()) {
						player.setItemStackToSlot(slot, stack);
						stack = null;
					} else if(EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, curr) == 0) {
						player.setItemStackToSlot(slot, stack);
						stack = curr;
					}
				}
				
				if(stack != null)
					if(!player.addItemStackToInventory(stack))
						entityDropItem(stack, 0);
			}
			
			if(world instanceof WorldServer) {
				((WorldServer) world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, false, posX, posY + 0.5, posZ, drops, 0.1, 0.5, 0.1, 0);
				((WorldServer) world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, false, posX, posY + 0.5, posZ, drops, 0.4, 0.5, 0.4, 0);
			}
		}

		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if(isDead)
			return;
		
		if(TotemOfHolding.darkSoulsMode) {
			EntityPlayer owner = getOwnerEntity();
			if(owner != null && !world.isRemote) {
				String ownerTotem = TotemOfHolding.getTotemUUID(owner);
				if(!getUniqueID().toString().equals(ownerTotem))
					dropEverythingAndDie();
			}
		}
		
		if(storedItems.isEmpty() && !world.isRemote)
			dataManager.set(DYING, true);
		
		if(isDying()) {
			if(deathTicks > DEATH_TIME)
				setDead();
			else deathTicks++;
		}
		
		else if(world.isRemote)
			world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY + (Math.random() - 0.5) * 0.2, posZ, Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
	}
	
	private void dropEverythingAndDie() {
		if(!TotemOfHolding.destroyItems)
			for (ItemStack storedItem : storedItems)
				entityDropItem(storedItem, 0);
		
		storedItems.clear();
		
		setDead();
	}

	public int getDeathTicks() {
		return deathTicks;
	}
	
	public boolean isDying() {
		return dataManager.get(DYING); 
	}
				
	@Override
	protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
		NBTTagList list = compound.getTagList(TAG_ITEMS, 10);
		storedItems = new LinkedList<>();

		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound cmp = list.getCompoundTagAt(i);
			ItemStack stack = new ItemStack(cmp);
			storedItems.add(stack);
		}
		
		boolean dying = compound.getBoolean(TAG_DYING);
		dataManager.set(DYING, dying);
		
		owner = compound.getString(TAG_OWNER);
	}

	@Override
	protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for(ItemStack stack : storedItems) {
			list.appendTag(stack.serializeNBT());
		}

		compound.setTag(TAG_ITEMS, list);
		compound.setBoolean(TAG_DYING, isDying());
		if (owner != null)
			compound.setString(TAG_OWNER, owner);
	}

}
