package vazkii.quark.misc.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.quark.base.handler.RayTraceHandler;
import vazkii.quark.base.module.Feature;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReacharoundPlacing extends Feature {

	float leniency;
	
	List<String> whitelist;
	
	@Override
	public void setupConfig() {
		leniency = (float) loadPropDouble("Leniency", "How lenient the detection for reacharound should be. Higher leniency means you can look further away from the block edge", 0.5);
		
		String[] whitelistArr = loadPropStringList("Whitelist", "A whitelist of items that are allowed to reacharound (all blocks can without being here)", new String[] {
			"quark:trowel"
		}); 
		whitelist = Arrays.asList(whitelistArr);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		
		if(player != null) {
			BlockPos pos = getPlayerReacharoundTarget(player);
			if(pos != null) {
				ScaledResolution res = event.getResolution();
				String s = "[  ]";
				mc.fontRenderer.drawString(s, res.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(s) / 2 + 1, res.getScaledHeight() / 2 - 3, 0xFFFFFF);
			}
		}
	}
	
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickItem event) {
		ItemStack stack = event.getItemStack();
	
		EntityPlayer player = event.getEntityPlayer();
		BlockPos pos = getPlayerReacharoundTarget(player);
		
		if(pos != null) {
			int count = stack.getCount();
			EnumHand hand = event.getHand();
			
			IBlockState currState = player.world.getBlockState(pos);
			EnumActionResult res = stack.getItem().onItemUse(player, player.getEntityWorld(), pos, hand, EnumFacing.DOWN, 0.5F, 1F, 0.5F);
			
			if(res != EnumActionResult.PASS) {
				event.setCanceled(true);
				event.setCancellationResult(res);
				
				if(res == EnumActionResult.SUCCESS) {
					if(!player.world.getBlockState(pos).equals(currState))
						LockDirectionHotkey.fixBlockRotation(player.world, player, pos);
						
					player.swingArm(hand);
				}

				if(player.isCreative() && stack.getCount() < count)
					stack.setCount(count);
			}
		}
	}
	
	private BlockPos getPlayerReacharoundTarget(EntityPlayer player) {
		if(player.rotationPitch < 0 || !(validateReacharoundStack(player.getHeldItemMainhand()) || validateReacharoundStack(player.getHeldItemOffhand())))
			return null;
		
		World world = player.world;
		
		Pair<Vec3d, Vec3d> params = RayTraceHandler.getEntityParams(player);
		double range = RayTraceHandler.getEntityRange(player);
		Vec3d rayPos = params.getLeft();
		Vec3d ray = params.getRight().scale(range);

		RayTraceResult normalRes = RayTraceHandler.rayTrace(world, rayPos, ray, false);
		
		if(normalRes == null || normalRes.typeOfHit == RayTraceResult.Type.MISS) {
			float leniency = 0.5F;
			
			rayPos = rayPos.add(0, leniency, 0);
			RayTraceResult take2Res = RayTraceHandler.rayTrace(world, rayPos, ray, false);
			
			if(take2Res != null && take2Res.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos pos = take2Res.getBlockPos().down();
				IBlockState state = world.getBlockState(pos);

				if(player.posY - pos.getY() > 1 && (world.isAirBlock(pos) || state.getBlock().isReplaceable(world, pos)))
					return pos;
			}
		}
		
		return null;
	}
	
	private boolean validateReacharoundStack(ItemStack stack) {
		Item item = stack.getItem();
		return item instanceof ItemBlock || whitelist.contains(Objects.toString(item.getRegistryName()).toString());
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
