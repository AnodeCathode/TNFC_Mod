/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [28/03/2016, 16:47:58 (GMT)]
 */
package vazkii.quark.base.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.api.IDropoffManager;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageDisableDropoffClient;
import vazkii.quark.management.feature.ChestButtons;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.StoreToChests;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public final class DropoffHandler {

	public static void dropoff(EntityPlayer player, boolean smart, boolean useContainer) {
		if(!ModuleLoader.isFeatureEnabled(useContainer ? ChestButtons.class : StoreToChests.class) || player.isSpectator())
			return;

		if(!useContainer && !player.getEntityWorld().getWorldInfo().getGameRulesInstance().getBoolean(StoreToChests.GAME_RULE)) {
			disableClientDropoff(player);
			return;
		}

		new Dropoff(player, smart, useContainer).execute();
	}

	public static void restock(EntityPlayer player, boolean filtered) {
		if(!ModuleLoader.isFeatureEnabled(ChestButtons.class) || player.isSpectator())
			return;

		new Restock(player, filtered).execute();
	}

	public static void disableClientDropoff(EntityPlayer player) {
		if(player instanceof EntityPlayerMP)
			NetworkHandler.INSTANCE.sendTo(new MessageDisableDropoffClient(), (EntityPlayerMP) player);
	}

	public static IItemHandler getInventory(EntityPlayer player, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if(te == null)
			return null;
		
		boolean accept = isValidChest(player, te);
		if(accept) {
			Supplier<IItemHandler> supplier = () -> {
				IItemHandler innerRet = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if(innerRet == null && te instanceof IInventory)
					innerRet = new InvWrapper((IInventory) te);
				
				return innerRet;
			};
			
			if(IDropoffManager.hasProvider(te))
				return IDropoffManager.getProvider(te).getDropoffItemHandler(supplier);
			else return supplier.get();
		}
		
		return null;
	}

	private static boolean hasProvider(Object te) {
		return te instanceof TileEntity && IDropoffManager.hasProvider((TileEntity) te);
	}

	private static IDropoffManager getProvider(Object te) {
		return IDropoffManager.getProvider((TileEntity) te);
	}


	private static boolean accepts(Object te, EntityPlayer player) {
		if (hasProvider(te))
			return getProvider(te).acceptsDropoff(player);

		return false;
	}

	public static boolean isValidChest(EntityPlayer player, TileEntity te) {
		boolean accept = accepts(te, player);
		if(!accept) accept = ChestButtons.overriddenDropoff(te);

		if(te instanceof IInventory)
			accept = accept && ((IInventory) te).isUsableByPlayer(player);


		return accept;
	}

	public static boolean isValidChest(EntityPlayer player, IInventory te) {
		if (te instanceof TileEntity)
			return isValidChest(player, (TileEntity) te);

		return accepts(te, player) && te.isUsableByPlayer(player);
	}

	public static boolean isValidChest(EntityPlayer player, IItemHandler te) {
		return accepts(te, player);
	}

	public static class Dropoff {

		public final EntityPlayer player;
		public final boolean smart;
		public final boolean useContainer;

		public final List<Pair<IItemHandler, Double>> itemHandlers = new ArrayList<>();

		public Dropoff(EntityPlayer player, boolean smart, boolean useContainer) {
			this.player = player;
			this.useContainer = useContainer;
			this.smart = smart;
		}

		public void execute() {
			locateItemHandlers();

			if(itemHandlers.isEmpty())
				return;

			if(smart)
				smartDropoff();
			else roughDropoff();

			player.inventoryContainer.detectAndSendChanges();
			if(useContainer)
				player.openContainer.detectAndSendChanges();
		}

		public void smartDropoff() {
			dropoff((stack, handler) -> {
				int slots = handler.getSlots();
				for(int i = 0; i < slots; i++) {
					ItemStack stackAt = handler.getStackInSlot(i);
					if(stackAt.isEmpty())
						continue;

					boolean itemEqual = stack.getItem() == stackAt.getItem();
					boolean damageEqual = stack.getItemDamage() == stackAt.getItemDamage();
					boolean nbtEqual = ItemStack.areItemStackTagsEqual(stackAt, stack);

					if(itemEqual && damageEqual && nbtEqual)
						return true;

					if(!stack.getHasSubtypes() && stack.isItemStackDamageable() && stack.getMaxStackSize() == 1 && itemEqual && nbtEqual)
						return true;
				}

				return false;
			});
		}

		public void roughDropoff() {
			dropoff((stack, handler) -> true);
		}

		public void locateItemHandlers() {
			if(useContainer) {
				Container c = player.openContainer;
				for(Slot s : c.inventorySlots) {
					IInventory inv = s.inventory;
					if(inv != player.inventory) {
						itemHandlers.add(Pair.of(ContainerWrapper.provideWrapper(s, c), 0.0));
						break;
					}
				}
			} else {
				BlockPos playerPos = player.getPosition();
				int range = 6;

				for(int i = -range; i < range * 2 + 1; i++)
					for(int j = -range; j < range * 2 + 1; j++)
						for(int k = -range; k < range * 2 + 1; k++) {
							BlockPos pos = playerPos.add(i, j, k);
							findHandler(pos);
						}

				itemHandlers.sort(Comparator.comparingDouble(Pair::getRight));
			}
		}

		public void findHandler(BlockPos pos) {
			IItemHandler handler = getInventory(player, player.getEntityWorld(), pos);
			if(handler != null)
				itemHandlers.add(Pair.of(handler, player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)));
		}

		public void dropoff(DropoffPredicate predicate) {
			InventoryPlayer inv = player.inventory;

			for(int i = InventoryPlayer.getHotbarSize(); i < inv.mainInventory.size(); i++) {
				ItemStack stackAt = inv.getStackInSlot(i);

				if(!stackAt.isEmpty() && !FavoriteItems.isItemFavorited(stackAt)) {
					ItemStack ret = insert(stackAt, predicate);
					if(!ItemStack.areItemStacksEqual(stackAt, ret))
						inv.setInventorySlotContents(i, ret);
				}
			}
		}

		public ItemStack insert(ItemStack stack, DropoffPredicate predicate) {
			ItemStack ret = stack.copy();
			for(Pair<IItemHandler, Double> pair : itemHandlers) {
				IItemHandler handler = pair.getLeft();
				ret = insertInHandler(handler, ret, predicate);
				if(ret.isEmpty())
					return ItemStack.EMPTY;
			}

			return ret;
		}

		public ItemStack insertInHandler(IItemHandler handler, final ItemStack stack, DropoffPredicate predicate) {
			if(predicate.apply(stack, handler)) {
				ItemStack retStack = ItemHandlerHelper.insertItemStacked(handler, stack, false);
				if(!retStack.isEmpty())
					retStack = retStack.copy();
				else 
					return retStack;

				return retStack;
			}

			return stack;
		}
		
	}

	public static class Restock extends Dropoff {

		public Restock(EntityPlayer player, boolean filtered) {
			super(player, filtered, true);
		}

		@Override
		public void dropoff(DropoffPredicate predicate) {
			IItemHandler inv = itemHandlers.get(0).getLeft();
			IItemHandler playerInv = new PlayerInvWrapper(player.inventory);

			for(int i = inv.getSlots() - 1; i >= 0; i--) {
				ItemStack stackAt = inv.getStackInSlot(i);

				if(!stackAt.isEmpty()) {
					ItemStack copy = stackAt.copy();
					ItemStack ret = insertInHandler(playerInv, copy, predicate);
					
					if(!ItemStack.areItemStacksEqual(stackAt, ret)) {
						inv.extractItem(i, stackAt.getMaxStackSize(), false);
						if(!ret.isEmpty())
							inv.insertItem(i, ret, false);
					}
				}
			}
		}
	}

	public static class PlayerInvWrapper extends InvWrapper {

		public PlayerInvWrapper(IInventory inv) {
			super(inv);
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if(stack.isEmpty())
				stack = stack.copy();

			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public int getSlots() {
			return super.getSlots() - 5;
		}

	}
	
	public static class ContainerWrapper extends InvWrapper {
		
		private final Container container;

		public static IItemHandler provideWrapper(Slot slot, Container container) {
			if (slot instanceof SlotItemHandler) {
				IItemHandler handler = ((SlotItemHandler) slot).getItemHandler();
				if (hasProvider(handler)) {
					return getProvider(handler).getDropoffItemHandler(() -> handler);
				} else {
					return handler;
				}
			} else {
				return provideWrapper(slot.inventory, container);
			}
		}

		public static IItemHandler provideWrapper(IInventory inv, Container container) {
			if(hasProvider(inv))
				return getProvider(inv).getDropoffItemHandler(() -> new ContainerWrapper(inv, container));
			return new ContainerWrapper(inv, container);
		}
		
		private ContainerWrapper(IInventory inv, Container container) {
			super(inv);
			this.container = container;
		}
		
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			Slot containerSlot = getSlot(slot);
			if(containerSlot == null || !containerSlot.isItemValid(stack))
				return stack;
			
			return super.insertItem(slot, stack, simulate);
		}
		
		private Slot getSlot(int slot) {
			return container.getSlotFromInventory(getInv(), slot);
		}
		
	}

	public interface DropoffPredicate {

		boolean apply(ItemStack stack, IItemHandler handler);

	}

}
