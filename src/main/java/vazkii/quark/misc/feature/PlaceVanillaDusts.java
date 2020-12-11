package vazkii.quark.misc.feature;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.handler.RayTraceHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.block.BlockGlowstoneDust;
import vazkii.quark.misc.block.BlockGunpowder;

public class PlaceVanillaDusts extends Feature {

	public static Block glowstone_dust_block;
	public static Block gunpowder_block;

	public static boolean enableGlowstone, enableGunpowder;
	public static int gunpowderDelay;
	public static int gunpowderDelayNetherrack;

	@Override
	public void setupConfig() {
		enableGlowstone = loadPropBool("Enable Glowstone", "", true);
		enableGunpowder = loadPropBool("Enable Gunpowder", "", true);
		gunpowderDelay = loadPropInt("Gunpowder Delay", "Amount of ticks between each piece of gunpowder igniting the next", 10);
		gunpowderDelayNetherrack = loadPropInt("Gunpowder Delay on Netherrack", "Amount of ticks between each piece of gunpowder igniting the next, if on Netherrack", 5);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(enableGlowstone)
			glowstone_dust_block = new BlockGlowstoneDust();

		if(enableGunpowder)
			gunpowder_block = new BlockGunpowder();
	}

	@SubscribeEvent
	public void onRightClick(RightClickItem event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = event.getWorld();
		EnumHand hand = event.getHand();
		ItemStack stack = event.getItemStack();
		RayTraceResult res = RayTraceHandler.rayTrace(world, player, false);
		if(res != null) {
			BlockPos pos = res.getBlockPos();
			EnumFacing face = res.sideHit;

			if(enableGlowstone && stack.getItem() == Items.GLOWSTONE_DUST)
				setBlock(event, player, stack, world, pos, hand, face, glowstone_dust_block, res);
			else if(enableGunpowder && stack.getItem() == Items.GUNPOWDER)
				setBlock(event, player, stack, world, pos, hand, face, gunpowder_block, res);
		}
	}


	@SubscribeEvent
	public void missingItemMappings(RegistryEvent.MissingMappings<Item> event) {
		for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings()) {
			if (mapping.key.getPath().equals("glowstone_dust_block") || mapping.key.getPath().equals("gunpowder_block"))
				mapping.ignore();
		}
	}

	@SuppressWarnings("deprecation")
	public static void setBlock(PlayerInteractEvent event, EntityPlayer player, ItemStack stack, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, Block block, RayTraceResult res) {
		boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
		BlockPos blockpos = flag ? pos : pos.offset(facing);
		ItemStack itemstack = player.getHeldItem(hand);

		if(player.canPlayerEdit(blockpos, facing, itemstack) && worldIn.mayPlace(worldIn.getBlockState(blockpos).getBlock(), blockpos, false, facing, null) && block.canPlaceBlockAt(worldIn, blockpos)) {
	        float hx = (float) (res.hitVec.x - blockpos.getX());
	        float hy = (float) (res.hitVec.y - blockpos.getY());
	        float hz = (float) (res.hitVec.z - blockpos.getZ());
			IBlockState state = block.getStateForPlacement(worldIn, blockpos, facing, hx, hy, hz, stack.getMetadata(), player);
					
			worldIn.setBlockState(blockpos, state);
			SoundType soundtype = state.getBlock().getSoundType(state, worldIn, pos, player);
			worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

			if(player instanceof EntityPlayerMP)
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, blockpos, itemstack);

			if(!player.capabilities.isCreativeMode)
				itemstack.shrink(1);
			player.swingArm(hand);
		}

		event.setCanceled(true);
		event.setCancellationResult(flag ? EnumActionResult.SUCCESS : EnumActionResult.FAIL);
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}

