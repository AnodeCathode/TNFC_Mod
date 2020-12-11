package vazkii.quark.misc.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntitySoulPowder extends Entity {

	private static final DataParameter<Integer> TARGET_X = EntityDataManager.createKey(EntitySoulPowder.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TARGET_Z = EntityDataManager.createKey(EntitySoulPowder.class, DataSerializers.VARINT);

	private int liveTicks = 0;
	private static final String TAG_TARGET_X = "targetX";
	private static final String TAG_TARGET_Z = "targetZ";
	
	public EntitySoulPowder(World worldIn) {
		super(worldIn);
		setSize(0, 0);
	}
	
	public EntitySoulPowder(World worldIn, int x, int z) {
		this(worldIn);
		dataManager.set(TARGET_X, x);
		dataManager.set(TARGET_Z, z);
	}

	@Override
	protected void entityInit() {
		dataManager.register(TARGET_X, 0);
		dataManager.register(TARGET_Z, 0);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		double posSpread = 0.4;
		double scale = 0.08;
		double rotateSpread = 1.5;
		double rise = 0.05;
		int maxLiveTime = 6000;
		int particles = 20;
		double trigArg = liveTicks * 0.32;
		
		if((maxLiveTime - liveTicks) < particles)
			particles = (maxLiveTime - liveTicks);
		
		Vec3d vec = new Vec3d((double) dataManager.get(TARGET_X), posY, (double) dataManager.get(TARGET_Z)).subtract(posX, posY, posZ).normalize().scale(scale);
		double bpx = posX + vec.x * liveTicks + Math.cos(trigArg) * rotateSpread;
		double bpy = posY + vec.y * liveTicks + liveTicks * rise;
		double bpz = posZ + vec.z * liveTicks + Math.sin(trigArg) * rotateSpread;
		
		for(int i = 0; i < particles; i++) {
			double px = bpx + (Math.random() - 0.5) * posSpread;
			double py = bpy + (Math.random() - 0.5) * posSpread;
			double pz = bpz + (Math.random() - 0.5) * posSpread;
			world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, 0.2, 0.12, 0.1);
			if(Math.random() < 0.05)
				world.spawnParticle(EnumParticleTypes.FALLING_DUST, px, py, pz, 0, 0, 0, Block.getStateId(Blocks.SOUL_SAND.getDefaultState()));
		}
		
		if(Math.random() < 0.1)
			world.playSound(null, bpx, bpy, bpz, SoundEvents.ENTITY_GHAST_AMBIENT, SoundCategory.PLAYERS, 0.2F, 1F);

		liveTicks++;
		if(liveTicks > maxLiveTime || world.getBlockState(new BlockPos(bpx, bpy, bpz)).isBlockNormalCube())
			setDead();
	}

	@Override
	protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
		dataManager.set(TARGET_X, compound.getInteger(TAG_TARGET_X));
		dataManager.set(TARGET_Z, compound.getInteger(TAG_TARGET_Z));
	}

	@Override
	protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
		compound.setInteger(TAG_TARGET_X, dataManager.get(TARGET_X));
		compound.setInteger(TAG_TARGET_Z, dataManager.get(TARGET_Z));
	}

}
