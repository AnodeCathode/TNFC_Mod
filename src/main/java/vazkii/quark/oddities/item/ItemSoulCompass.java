package vazkii.quark.oddities.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.oddities.feature.TotemOfHolding;

import javax.annotation.Nonnull;

public class ItemSoulCompass extends ItemMod implements IQuarkItem, IItemPropertyGetter {

	private static final String TAG_POS_X = "posX";
	private static final String TAG_POS_Y = "posY";
	private static final String TAG_POS_Z = "posZ";
	
	@SideOnly(Side.CLIENT)
	private static double rotation, rota;

	@SideOnly(Side.CLIENT)
	private static long lastUpdateTick;
	
	public ItemSoulCompass() {
		super("soul_compass");
		
		addPropertyOverride(new ResourceLocation("angle"), this);
		setCreativeTab(CreativeTabs.TOOLS);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if(!worldIn.isRemote) {
			BlockPos pos = TotemOfHolding.getPlayerDeathPosition(entityIn);
			ItemNBTHelper.setInt(stack, TAG_POS_X, pos.getX());
			ItemNBTHelper.setInt(stack, TAG_POS_Y, pos.getY());
			ItemNBTHelper.setInt(stack, TAG_POS_Z, pos.getZ());
		}
	}
	
	private BlockPos getPos(ItemStack stack) {
		if(stack.hasTagCompound()) {
			int x = ItemNBTHelper.getInt(stack, TAG_POS_X, 0);
			int y = ItemNBTHelper.getInt(stack, TAG_POS_Y, -1);
			int z = ItemNBTHelper.getInt(stack, TAG_POS_Z, 0);
			
			return new BlockPos(x, y, z);
		}
		
		return new BlockPos(0, -1, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float apply(@Nonnull ItemStack stack, World world, EntityLivingBase entityIn) {
		if(entityIn == null && !stack.isOnItemFrame())
			return 0;

		else {
			boolean hasEntity = entityIn != null;
			Entity entity = (hasEntity ? entityIn : stack.getItemFrame());

			if (entity == null)
				return 0;

			if(world == null)
				world = entity.world;

			double angle;
			BlockPos pos = getPos(stack);

			if(pos.getY() == world.provider.getDimension()) {
				double yaw = hasEntity ? entity.rotationYaw : getFrameRotation((EntityItemFrame) entity);
				yaw = MathHelper.positiveModulo(yaw / 360.0, 1.0);
				double relAngle = getDeathToAngle(entity, pos) / (Math.PI * 2);
				angle = 0.5 - (yaw - 0.25 - relAngle);
			}
			else angle = Math.random();

			if (hasEntity)
				angle = wobble(world, angle);

			return MathHelper.positiveModulo((float) angle, 1.0F);
		}
	}
	@SideOnly(Side.CLIENT)
	private double wobble(World worldIn, double angle) {
		if(worldIn.getTotalWorldTime() != lastUpdateTick) {
			lastUpdateTick = worldIn.getTotalWorldTime();
			double relAngle = angle - rotation;
			relAngle = MathHelper.positiveModulo(relAngle + 0.5, 1.0) - 0.5;
			rota += relAngle * 0.1;
			rota *= 0.8;
			rotation = MathHelper.positiveModulo(rotation + rota, 1.0);
		}

		return rotation;
	}

	@SideOnly(Side.CLIENT)
	private double getFrameRotation(EntityItemFrame frame) {
		EnumFacing facing = frame.facingDirection;
		if (facing == null)
			facing = EnumFacing.NORTH;
		return MathHelper.wrapDegrees(180 + facing.getHorizontalAngle());
	}

	@SideOnly(Side.CLIENT)
	private double getDeathToAngle(Entity entity, BlockPos blockpos) {
		return Math.atan2(blockpos.getZ() - entity.posZ, blockpos.getX() - entity.posX);
	}


}

