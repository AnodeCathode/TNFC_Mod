package vazkii.quark.automation.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.automation.block.BlockGravisand;
import vazkii.quark.automation.feature.Gravisand;

public class EntityGravisand extends EntityFallingBlock {

	private static final DataParameter<Float> DIRECTION = EntityDataManager.createKey(EntityGravisand.class, DataSerializers.FLOAT);

	private static final String TAG_DIRECTION = "fallDirection";

	// dumb hardcoding because of networking being a pain and this bock is really only used for this anyway
	private final IBlockState fallTile = Gravisand.gravisand.getDefaultState();

	public EntityGravisand(World worldIn) {
		super(worldIn);
		setSize(0.98F, 0.98F);
	}

	public EntityGravisand(World worldIn, double x, double y, double z, IBlockState fallingBlockState, float direction) {
		super(worldIn, x, y, z, fallingBlockState);

		dataManager.set(DIRECTION, direction);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(DIRECTION, 0F);
	}

	// Mostly vanilla copy but supporting directional falling
	@Override
	public void onUpdate() {
		Block block = fallTile.getBlock();

		if(fallTile.getMaterial() == Material.AIR || posY > 300 || posY < -50) {
			setDead();
			return;
		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		if(fallTime++ == 0) {
			BlockPos blockpos = new BlockPos(this);

			if (world.getBlockState(blockpos).getBlock() == block)
				world.setBlockToAir(blockpos);
			else if(!world.isRemote) {
				setDead();
				return;
			}
		}

		float fallDirection = getFallDirection();

		motionY = fallDirection * 0.4F;

		move(MoverType.SELF, motionX, motionY, motionZ);

		if(!world.isRemote) {
			float off = fallDirection < 0 ?  + 0.5F : 0F;
			BlockPos fallTarget = new BlockPos(posX, posY + fallDirection + off, posZ);

			
			if(collidedVertically) {
				BlockPos pos = new BlockPos(this);
				IBlockState iblockstate = world.getBlockState(pos);

				motionX *= 0.7;
				motionZ *= 0.7;
				motionY *= -0.5;

				if(iblockstate.getBlock() != Blocks.PISTON_EXTENSION) {
					setDead();

					// This is correct, the if has no block
					if (!world.mayPlace(block, pos, true, fallDirection < 0 ? EnumFacing.UP : EnumFacing.DOWN, this) ||
							BlockGravisand.canFallThrough(world.getBlockState(fallTarget)) ||
							!world.setBlockState(pos, fallTile, 3)) {
						if (shouldDropItem && world.getGameRules().getBoolean("doEntityDrops")) {
							entityDropItem(new ItemStack(block, 1, block.damageDropped(fallTile)), 0.0F);
						}
					}
				}
			}
		}


		motionX *= 0.98;
		motionY *= 0.98;
		motionZ *= 0.98;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		// NO-OP
	}

	private float getFallDirection() {
		return dataManager.get(DIRECTION);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		compound.setFloat(TAG_DIRECTION, getFallDirection());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		dataManager.set(DIRECTION, compound.getFloat(TAG_DIRECTION));
	}

	@Override
	public IBlockState getBlock() {
		return fallTile;
	}

}
