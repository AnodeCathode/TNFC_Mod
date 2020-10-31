package mods.immibis.core.experimental.mgui1;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import immibis.mgui.MClientWindow;
import immibis.mgui.MEventHandler;
import immibis.mgui.MGUI;
import immibis.mgui.MHostingEnvironment;
import immibis.mgui.MServerWindow;
import immibis.mgui.control_spi.ServerControl;
import immibis.mgui.controls.MItemSlotControl;
import mods.immibis.core.api.APILocator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class IC_MGUI {
	static InputStream createWindowStream(ResourceLocation loc) {
		try {
			if(FMLLaunchHandler.side().isServer()) {
				return IC_MGUI.class.getClassLoader().getResourceAsStream("assets/"+loc.getResourceDomain()+"/"+loc.getResourcePath());
			} else {
				return getClientResourceStream(loc);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static MServerWindow createServerWindow(ResourceLocation loc) {
		try {
			try (InputStream in = createWindowStream(loc)) {
				return MGUI.createServerWindowFromStream(in);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static MClientWindow createClientWindow(ResourceLocation loc, MHostingEnvironment env) {
		try {
			try (InputStream in = createWindowStream(loc)) {
				return MGUI.createClientWindowFromStream(in, env);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static MServerWindow openGUI(final EntityPlayer ply2, String guiResourcePath, Map<String, IInventory> inventories, MEventHandler eventHandler) {
		
		if(!(ply2 instanceof EntityPlayerMP))
			return null;
		
		EntityPlayerMP ply = (EntityPlayerMP)ply2;
		
		MServerWindow w = createServerWindow(new ResourceLocation(guiResourcePath));
		
		
		ContainerMGUIServer c = new ContainerMGUIServer();
		
		ply.getNextWindowId();
		c.windowId = ply.currentWindowId;
		ply.openContainer = c;
		
		PacketOpenMGUI p = new PacketOpenMGUI();
		p.windowID = c.windowId;
		p.guifile = new ResourceLocation(guiResourcePath);
		p.serverIDs = generateIDList(w);
		p.serverSlotControlNumSlots = new int[p.serverIDs.length];
		int totalSlots = 0;
		for(int k = 0; k < p.serverIDs.length; k++) {
			ServerControl ctrl = w.getControlByID(p.serverIDs[k]);
			if(ctrl instanceof MItemSlotControl)
				p.serverSlotControlNumSlots[k] = ((MItemSlotControl)ctrl).getNumSlots();
			else
				p.serverSlotControlNumSlots[k] = 0;
			
			totalSlots += p.serverSlotControlNumSlots[k];
		}
		
		BridgeUtils.BridgeGenResult bridgeInfo = BridgeUtils.generateBridgeAndSlotMap(w, p.serverIDs, totalSlots, inventories);
		p.containerSlotToBridgeInvSlot = bridgeInfo.containerToBridgeSlotMap;
		c.initSlots(bridgeInfo.bridgeInventory, bridgeInfo.containerToBridgeSlotMap);
		
		APILocator.getNetManager().sendToClient(p, ply);
		
		c.initChannel(p.serverIDs);
		for(final ServerControl ctrl : w.getControls())
			ctrl.setSyncChannel(c.channel);
		
		c.initMainWindow(w, inventories, eventHandler);
		c.addCraftingToCrafters(ply);
		c.detectAndSendChanges();
		
		return w;
	}
	
	private static String[] generateIDList(MServerWindow w) {
		List<ServerControl> controls = w.getControls();
		String[] rv = new String[controls.size()];
		for(int k = 0; k < controls.size(); k++)
			rv[k] = controls.get(k).getIdentifier();
		return rv;
	}

	private static InputStream getClientResourceStream(ResourceLocation loc) throws IOException {
		return getClientResourceStream2(loc);
	}
	
	@SideOnly(Side.CLIENT)
	private static InputStream getClientResourceStream2(ResourceLocation loc) throws IOException {
		return Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream();
	}
}
