package vazkii.quark.automation.feature;

import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

import java.util.List;

public class PistonsPushPullItems extends Feature {

	public static float force = 0.48F;

	@Override
	public void setupConfig() {
		force = (float) loadPropDouble("Push Strength", "", force);
	}

	public static void onPistonUpdate(TileEntityPiston piston) {
		if(!ModuleLoader.isFeatureEnabled(PistonsPushPullItems.class) || piston.getWorld().isRemote)
			return;

		IBlockState state = piston.getPistonState();
		boolean pulling = state.getBlock() != Blocks.PISTON_HEAD;
		boolean sticky = state.getBlock() == Blocks.STICKY_PISTON || (state.getPropertyKeys().contains(BlockPistonExtension.TYPE) && state.getValue(BlockPistonExtension.TYPE) == EnumPistonType.STICKY); 

		if(pulling != sticky)
			return;
		
		EnumFacing face = piston.getFacing();
		AxisAlignedBB aabb = new AxisAlignedBB(piston.getPos().offset(face, pulling ? 2 : 1));
		List<EntityItem> items = piston.getWorld().getEntitiesWithinAABB(EntityItem.class, aabb);

		for(EntityItem entity : items)
			onEntityHandled(entity, face, sticky);
	}

	private static void onEntityHandled(EntityItem entity, EnumFacing face, boolean sticky) {
		if(sticky)
			face = face.getOpposite();
		
		World world = entity.getEntityWorld();
		if(sticky) {
			BlockPos offsetPos = entity.getPosition().offset(face);
			boolean closeToEdge = new BlockPos(entity.posX + face.getXOffset() * .5, entity.posY + face.getYOffset() * .5, entity.posZ + face.getZOffset() * .5).equals(offsetPos);
			if(closeToEdge)
				nudgeItem(world, entity, face, false);
		} else nudgeItem(world, entity, face, true);
	}

	private static void nudgeItem(World world, EntityItem entity, EnumFacing whichWay, boolean showParticles) {
		float x = force * whichWay.getXOffset();
		float y = force * whichWay.getYOffset() * 1.5f;
		float z = force * whichWay.getZOffset();
		float px = x == 0 ? 0.4F : 0;
		float py = y == 0 ? 0.4F : 0;
		float pz = z == 0 ? 0.4F : 0;
		entity.setPosition(entity.posX + 0.5 * whichWay.getXOffset(),
				entity.posY + 0.5 * whichWay.getYOffset(),
				entity.posZ + 0.5 * whichWay.getZOffset());
		entity.addVelocity(x, y, z);
		if(showParticles && world instanceof WorldServer)
			((WorldServer) world).spawnParticle(EnumParticleTypes.CRIT, entity.posX, entity.posY, entity.posZ, 12, px, py, pz, 0);
	}

}
