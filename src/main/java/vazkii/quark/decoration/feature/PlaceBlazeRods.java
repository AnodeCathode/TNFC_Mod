package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.handler.RayTraceHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockBlazeRod;
import vazkii.quark.misc.feature.PlaceVanillaDusts;

public class PlaceBlazeRods extends Feature {

	public static Block blaze_rod;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		blaze_rod = new BlockBlazeRod();
	}
	
	@SubscribeEvent
	public void onRightClick(RightClickItem event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = event.getWorld();
		EnumHand hand = event.getHand();
		ItemStack stack = event.getItemStack();
		RayTraceResult res = RayTraceHandler.rayTrace(world, player, false, player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue());
		if(res != null) {
			BlockPos pos = res.getBlockPos();
			EnumFacing face = res.sideHit;

			if(stack.getItem() == Items.BLAZE_ROD) {
				PlaceVanillaDusts.setBlock(event, player, stack, world, pos, hand, face, blaze_rod, res);
			}
		}
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
