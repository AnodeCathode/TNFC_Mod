/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Apr 17, 2019, 15:01 AM (EST)]
 */
package vazkii.quark.base.capability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.api.*;
import vazkii.quark.api.capability.IBoatBanner;
import vazkii.quark.api.capability.IEnchantColorProvider;
import vazkii.quark.api.capability.ISearchHandler;
import vazkii.quark.base.lib.LibMisc;

import java.util.concurrent.Callable;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID)
public class CapabilityHandler {
	public static void register() {
		registerLambda(IEnchantColorProvider.class, () -> -1);
		registerLambda(IPistonCallback.class, () -> {});
		registerLambda(ISearchHandler.class, (query, matcher, search) -> false);
		registerLambda(IDropoffManager.class, (player) -> false);
		register(IBoatBanner.class, BoatBanner::new);
		register(ICustomSorting.class, DummySorting::new);
	}

	private static <T> void registerLambda(Class<T> clazz, T provider) {
		register(clazz, () -> provider);
	}

	private static <T> void register(Class<T> clazz, Callable<T> provider) {
		CapabilityManager.INSTANCE.register(clazz, new CapabilityFactory<>(), provider);
	}

	private static class CapabilityFactory<T> implements Capability.IStorage<T> {

		@Override
		public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
			if (instance instanceof INBTSerializable)
				return ((INBTSerializable) instance).serializeNBT();
			return null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound)
				((INBTSerializable) instance).deserializeNBT(nbt);
		}
	}

	private static final ResourceLocation PISTON_CALLBACK = new ResourceLocation(LibMisc.MOD_ID, "piston");
	private static final ResourceLocation ENCHANT_COLOR = new ResourceLocation(LibMisc.MOD_ID, "glint");
	private static final ResourceLocation SEARCH_HANDLER = new ResourceLocation(LibMisc.MOD_ID, "search");
	private static final ResourceLocation DROPOFF_MANAGER = new ResourceLocation(LibMisc.MOD_ID, "dropoff");
	private static final ResourceLocation SORTING_HANDLER = new ResourceLocation(LibMisc.MOD_ID, "sort");


	@SubscribeEvent
	public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		Item item = event.getObject().getItem();

		if (item instanceof ICustomEnchantColor)
			event.addCapability(ENCHANT_COLOR, new EnchantColorWrapper(event.getObject()));
		if (item instanceof ISearchHandler)
			event.addCapability(SEARCH_HANDLER, new SearchWrapper(event.getObject()));
		if (item instanceof ICustomSorting)
			SelfProvider.attachItem(SORTING_HANDLER, ICustomSorting.CAPABILITY, event);
	}

	@SubscribeEvent
	public static void attachTileCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
		if (event.getObject() instanceof IPistonCallback)
			SelfProvider.attach(PISTON_CALLBACK, IPistonCallback.CAPABILITY, event);
		if (event.getObject() instanceof IDropoffManager)
			SelfProvider.attach(DROPOFF_MANAGER, IDropoffManager.CAPABILITY, event);
	}
}
