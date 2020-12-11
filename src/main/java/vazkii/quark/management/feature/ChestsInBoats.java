package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageRequestPassengerChest;
import vazkii.quark.management.client.render.RenderChestPassenger;
import vazkii.quark.management.entity.EntityChestPassenger;

import java.util.List;

public class ChestsInBoats extends Feature {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		String name = LibMisc.PREFIX_MOD + "chest_passenger";
		EntityRegistry.registerModEntity(new ResourceLocation(name), EntityChestPassenger.class, name, LibEntityIDs.CHEST_PASSENGER, Quark.instance, 64, 128, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityChestPassenger.class, RenderChestPassenger.factory());
	}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
		Entity target = event.getTarget();
		EntityPlayer player = event.getEntityPlayer();

		if(target instanceof EntityBoat && target.getPassengers().isEmpty()) {
			EnumHand hand = EnumHand.MAIN_HAND;
			ItemStack stack = player.getHeldItemMainhand();
			if(!isChest(stack)) {
				stack = player.getHeldItemOffhand();
				hand = EnumHand.OFF_HAND;
			}

			if(isChest(stack)) {
				World world = event.getWorld();
				EntityChestPassenger passenger = new EntityChestPassenger(world, stack);
				passenger.setPosition(target.posX, target.posY, target.posZ);
				passenger.rotationYaw = target.rotationYaw;
				
				if(!event.getWorld().isRemote) {
					if (!player.isCreative())
						stack.shrink(1);
					world.spawnEntity(passenger);
					passenger.startRiding(target);
				}
				
				player.swingArm(hand);
				event.setCancellationResult(EnumActionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	@SideOnly(Side.CLIENT)
	public void onOpenGUI(GuiOpenEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null && event.getGui() instanceof GuiInventory && player.isRiding()) {
			Entity riding = player.getRidingEntity();
			if(riding instanceof EntityBoat) {
				List<Entity> passengers = riding.getPassengers();
				for(Entity passenger : passengers)
					if(passenger instanceof EntityChestPassenger) {
						NetworkHandler.INSTANCE.sendToServer(new MessageRequestPassengerChest());
						event.setCanceled(true);
						return;
					}
			}
		}
	}
	
	private boolean isChest(ItemStack stack) {
		if (stack.isEmpty())
			return false;

		int chestId = OreDictionary.getOreID("chestWood");
		int[] ids = OreDictionary.getOreIDs(stack);
		for (int id : ids) {
			if (id == chestId)
				return true;
		}

		return false;
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
