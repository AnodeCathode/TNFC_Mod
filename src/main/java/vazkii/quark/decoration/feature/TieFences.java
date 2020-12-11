package vazkii.quark.decoration.feature;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.client.render.RenderLeashKnot2;
import vazkii.quark.decoration.entity.EntityLeashKnot2TheKnotting;

public class TieFences extends Feature {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		String knotName = "quark:leash_knot";
		EntityRegistry.registerModEntity(new ResourceLocation(knotName), EntityLeashKnot2TheKnotting.class, knotName, LibEntityIDs.LEASH_KNOT, Quark.instance, 256, 64, false);
	}
	
	@SubscribeEvent
	public void onRightClick(RightClickBlock event) {
		World world = event.getWorld();
		if(world.isRemote)
			return;
		
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItem(event.getHand());
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		
		if(stack.getItem() == Items.LEAD && state.getBlock() instanceof BlockFence) {
			for(EntityLiving entityliving : world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(player.posX - 7, player.posY - 7, player.posZ - 7, player.posX + 7, player.posY + 7, player.posZ + 7))) {
				if(entityliving.getLeashHolder() == player)
					return;
			}

			EntityLeashKnot2TheKnotting knot = new EntityLeashKnot2TheKnotting(world);
			knot.setPosition(pos.getX() + 0.5, pos.getY() + 0.5 - 1F / 8F, pos.getZ() + 0.5);
			world.spawnEntity(knot);
			knot.setLeashHolder(player, true);

			if(!player.isCreative())
				stack.shrink(1);
			world.playSound(null, pos, SoundEvents.ENTITY_LEASHKNOT_PLACE, SoundCategory.BLOCKS, 1F, 1F);
			event.setCanceled(true);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityLeashKnot2TheKnotting.class, RenderLeashKnot2.FACTORY);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
