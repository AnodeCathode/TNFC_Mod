package mods.immibis.core;

import java.util.logging.Logger;

import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.crossmod.ICrossModBC;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.net.IPacketMap;
import mods.immibis.core.api.traits.IEnergyConsumerTrait;
import mods.immibis.core.api.traits.IInventoryTrait;
import mods.immibis.core.api.traits.ITrait;
import mods.immibis.core.commands.TPSCommand;
import mods.immibis.core.experimental.mgui1.Packet_MGUI_C2S;
import mods.immibis.core.experimental.mgui1.Packet_MGUI_S2C;
import mods.immibis.core.experimental.mgui1.PacketOpenMGUI;
import mods.immibis.core.impl.InventoryTraitImpl;
import mods.immibis.core.impl.NetworkingManager;
import mods.immibis.core.impl.crossmod.*;
import mods.immibis.core.multipart.MultipartSystem;
import mods.immibis.core.multipart.PacketMultipartDigFinish;
import mods.immibis.core.multipart.PacketMultipartDigStart;
import mods.immibis.core.net.FragmentSequence;
import mods.immibis.core.net.PacketButtonPress;
import mods.immibis.core.net.PacketFragment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ImmibisCore implements IPacketMap {
	
	public static final String VERSION = "59.1.4";
	public static final String MODID = "ImmibisCore";
	public static final String NAME = "Immibis Core";

	// 0 unused
	// 1 unused
	public static final int PACKET_TYPE_C2S_MULTIPART_DIG_START = 2;
	// 3 unused
	// 4 unused
	public static final int PACKET_TYPE_FRAGMENT = 5;
	public static final int PACKET_TYPE_C2S_BUTTON_PRESS = 6;
	public static final int PACKET_TYPE_C2S_MULTIPART_DIG_FINISH = 7;
	public static final int PACKET_TYPE_S2C_OPEN_MGUI = 8;
	public static final int PACKET_TYPE_S2C_MGUI = 9;
	public static final int PACKET_TYPE_C2S_MGUI = 10;


	public static final String CHANNEL = "ImmibisCore";

	public static NetworkingManager networkingManager = new NetworkingManager();
	public static ICrossModIC2 crossModIC2;
	public static ICrossModBC crossModBC;
	public static MultipartSystem multipartSystem = new MultipartSystem();
	
	public static String tpsCommandName;
	
	public static Logger LOGGER;
	static {
		LOGGER = Logger.getLogger(MODID);
	}
	
	public static java.util.Timer TIMER = new java.util.Timer("Immibis Core background task", true);

	public void preInit(FMLPreInitializationEvent evt) {
		
		APILocator.getNetManager().listen(this);

		FragmentSequence.init();
		MainThreadTaskQueue.init();
		
		Config.getString("core.mictransformer.ignoredClasses", "", Configuration.CATEGORY_GENERAL, "advanced setting: comma-separated list of classes to ignore when generating dynamic inheritance chains");
		
		tpsCommandName = Config.getString("core.command.tps.name", "tps", Configuration.CATEGORY_GENERAL, "name of TPS command, without the slash. leave blank to disable.");
		
		if(Loader.isModLoaded("IC2") && !Config.getBoolean("core.ignoreIC2", false))
			crossModIC2 = new CrossModIC2_Impl();
		else
			crossModIC2 = new CrossModIC2_Default();
		
		if(Loader.isModLoaded("BuildCraft|Core") && !Config.getBoolean("core.ignoreBuildcraftCore", false))
			if(Loader.isModLoaded("BuildCraft|Transport") && !Config.getBoolean("core.ignoreBuildcraftTransport", false))
				crossModBC = new CrossModBC_Impl();
			else
				crossModBC = new CrossModBC_Impl_NoTransport();
		else
			crossModBC = new CrossModBC_Default();
	}

	public void init(FMLInitializationEvent evt) {
		multipartSystem.init();
	}

	public void postInit(FMLPostInitializationEvent evt) {
		
	}
	
	public void serverStarting(FMLServerStartingEvent evt) {
		if(!tpsCommandName.equals(""))
			evt.registerServerCommand(new TPSCommand(tpsCommandName));
	}

	public static ImmibisCore instance;

	public ImmibisCore() {
		instance = this;
		
		// must happen during construction, as mods can
		// load classes that use traits during construction
		initPreferredEnergySystem(); 
	}



	public static boolean areItemsEqual(ItemStack a, ItemStack b) {
		if(a == null && b == null)
			return true;
		if(a == null || b == null)
			return false;
		if(a.getItem() != b.getItem())
			return false;
		if(a.getHasSubtypes() && a.getItemDamage() != b.getItemDamage())
			return false;
		if(a.stackTagCompound == null && b.stackTagCompound == null)
			return true;
		if(a.stackTagCompound == null || b.stackTagCompound == null)
			return false;
		return a.stackTagCompound.equals(b.stackTagCompound);
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}

	@Override
	public IPacket createS2CPacket(byte id) {
		if(id == PACKET_TYPE_FRAGMENT)
			return new PacketFragment();
		if(id == PACKET_TYPE_S2C_OPEN_MGUI)
			return new PacketOpenMGUI();
		if(id == PACKET_TYPE_S2C_MGUI)
			return new Packet_MGUI_S2C();
		return null;
	}

	@Override
	public IPacket createC2SPacket(byte id) {
		if(id == PACKET_TYPE_C2S_BUTTON_PRESS)
			return new PacketButtonPress(0);
		if(id == PACKET_TYPE_C2S_MULTIPART_DIG_START)
			return new PacketMultipartDigStart();
		if(id == PACKET_TYPE_FRAGMENT)
			return new PacketFragment();
		if(id == PACKET_TYPE_C2S_MULTIPART_DIG_FINISH)
			return new PacketMultipartDigFinish();
		if(id == PACKET_TYPE_C2S_MGUI)
			return new Packet_MGUI_C2S();
		return null;
	}
	
	private static void initPreferredEnergySystem() {
		String preferredEnergySystem = Config.getString("preferredEnergySystem",
			Loader.isModLoaded("IC2") ? "ic2" :
			Loader.isModLoaded("BuildCraft|Energy") ? "redstoneFlux" :
			Loader.isModLoaded("ThermalExpansion") ? "redstoneFlux" :
			"infinite",
			Configuration.CATEGORY_GENERAL,
			"Which power system should be used (for blocks that support this option). Possible values are: ic2 (IndustrialCraft 2's energy network), redstoneFlux (Thermal Expansion's power system, also used by BuildCraft and other mods), infinite (power is free)");
		System.out.println("[Immibis Core] Preferred energy system set to: " + preferredEnergySystem);
		if(preferredEnergySystem.equalsIgnoreCase("ic2"))
			ITrait.knownInterfaces.put(IEnergyConsumerTrait.class, EnergyConsumerTraitImpl_IC2.class);
		else if(preferredEnergySystem.equalsIgnoreCase("redstoneFlux") || preferredEnergySystem.equalsIgnoreCase("minecraftJoules"))
			ITrait.knownInterfaces.put(IEnergyConsumerTrait.class, EnergyConsumerTraitImpl_RF.class);
		else if(preferredEnergySystem.equalsIgnoreCase("infinite"))
			ITrait.knownInterfaces.put(IEnergyConsumerTrait.class, EnergyConsumerTraitImpl_Infinite.class);
		else
			throw new RuntimeException("Invalid preferred energy system selected: "+preferredEnergySystem+". Options are: ic2, redstoneFlux, infinite. Not case sensitive.");
	}

	static {
		ITrait.knownInterfaces.put(IInventoryTrait.class, InventoryTraitImpl.class);
	}

}
