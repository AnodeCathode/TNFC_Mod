package mods.immibis.tubestuff;

import mods.immibis.core.Config;
import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.FMLModInfo;
import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.net.IPacketMap;
import mods.immibis.core.api.porting.PortableBaseMod;
import mods.immibis.core.api.porting.SidedProxy;
import mods.immibis.lxp.LiquidXPMod;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(version="59.0.4", modid="Tubestuff", name="Tubestuff", dependencies="required-after:ImmibisCore")
@FMLModInfo(
		url="http://www.minecraftforum.net/topic/1001131-110-immibiss-mods-smp/",
		description="A collection of blocks that are useful with BuildCraft or RedPower.",
		authors="immibis"
		)
public class TubeStuff extends PortableBaseMod implements IPacketMap, IGuiHandler {
	public static TubeStuff instance;
	public static BlockTubestuff block;
	public static BlockStorage blockStorage;
	public static Item itemUseless;

	public static boolean enableSlowFalling;

	private static EntityPlayerFakeTS fakePlayer;

	public static final int GUI_BUFFER = 0;
	public static final int GUI_TABLE = 1;
	public static final int GUI_CHEST = 2;
	// 3 unused (was logic crafter)
	// 4 unused (was retrievulator)
	public static final int GUI_DUPLICATOR = 5;
	public static final int GUI_MCT2_EDIT = 6;
	public static final int GUI_DEPLOYER = 7;

	public static final byte PKT_BLOCK_BREAKER_DESC = 0;
	public static final byte PKT_ACT2_RECIPE_UPDATE = 1;

	public static final String CHANNEL = "TubeStuff";

	public static EntityPlayerFakeTS fakePlayer(World world) {
		if(fakePlayer == null)
			fakePlayer = new EntityPlayerFakeTS(world);
		return fakePlayer;
	}

	public TubeStuff() {
		instance = this;
	}

	private boolean hadFirstTick = false;
	@Override
	public boolean onTickInGame() {
		if(!hadFirstTick) {
			SharedProxy.FirstTick();
			hadFirstTick = true;
		}
		return false;
	}
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent evt) {
		block = new BlockTubestuff();
		GameRegistry.registerBlock(block, ItemTubestuff.class, "machine");
		
		SharedProxy.enableStorageBlocks = Config.getBoolean("tubestuff.enableStorageBlocks", true);
		SharedProxy.enableStorageBlocksVanilla = Config.getBoolean("tubestuff.enableStorageBlocks.vanilla", true);
		SharedProxy.enableStorageBlockOreDictionary = Config.getBoolean("tubestuff.enableStorageBlocks.useOreDictionary", true);

		enableSlowFalling = Config.getBoolean("tubestuff.enableSlowDustFalling", false);

		if(SharedProxy.enableStorageBlocks) {
			blockStorage = new BlockStorage();
			GameRegistry.registerBlock(blockStorage, ItemStorage.class, "storage");
		}

		itemUseless = new Item().setUnlocalizedName("tubestuff.jammer").setTextureName("tubestuff:jammer");
		itemUseless.setMaxStackSize(1);
		GameRegistry.registerItem(itemUseless, "uselessItem");
	}

	@EventHandler
	public void load(FMLInitializationEvent evt) {
		BlockTubestuff.model = SidedProxy.instance.getUniqueBlockModelID("mods.immibis.tubestuff.BlockRenderer", true);

		enableClockTicks(true);
		enableClockTicks(false);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, this);

		GameRegistry.registerTileEntity(TileBuffer.class, "TubeStuff buffer");
		GameRegistry.registerTileEntity(TileAutoCraftingMk2.class, "TubeStuff crafting table");
		GameRegistry.registerTileEntity(TileIncinerator.class, "TubeStuff incinerator");
		GameRegistry.registerTileEntity(TileDuplicator.class, "TubeStuff duplicator");
		SidedProxy.instance.registerTileEntity(TileBlackHoleChest.class, "TubeStuff infinite chest", "mods.immibis.tubestuff.RenderTileBlackHoleChest");
		SidedProxy.instance.registerTileEntity(TileBlockBreaker.class, "TubeStuff block breaker", "mods.immibis.tubestuff.RenderTileBlockBreaker");
		GameRegistry.registerTileEntity(TileLiquidIncinerator.class, "TubeStuff liquid incinerator");
		GameRegistry.registerTileEntity(TileLiquidDuplicator.class, "TubeStuff liquid duplicator");
		GameRegistry.registerTileEntity(TileOnlineDetector.class, "TubeStuff online detector");
		GameRegistry.registerTileEntity(TileMCT2.class, "TubeStuff MCT2");
		GameRegistry.registerTileEntity(TileDeployer.class, "TubeStuff deployer");
		
		MinecraftForge.EVENT_BUS.register(this);

		APILocator.getNetManager().listen(this);
	}

	public static final boolean areItemsEqual(ItemStack recipe, ItemStack input)
	{
		return input.getItem() == recipe.getItem() && (!input.getHasSubtypes() || input.getItemDamage() == recipe.getItemDamage() || recipe.getItemDamage() == Short.MAX_VALUE);
	}

	@SubscribeEvent
	public void onPickup(EntityItemPickupEvent evt) {
		if(evt.item.getEntityItem() != null && evt.item.getEntityItem().stackSize > 0 && evt.item.getEntityItem().getItem() == itemUseless) {
			evt.item.setDead();
			evt.setCanceled(true);
		}
	}

	private static Object icWrenchItem = null;
	private static Class<?> bcWrenchInterface = null;
	
	/**
	 * Accepts any of the following items:
	 * <ul>
	 * <li>BuildCraft wrench</li>
	 * <li>IndustrialCraft wrench</li>
	 * <li>RedPower screwdriver</li>
	 * <li>RedPower sonic screwdriver (if charged)</li>
	 * <li>Vanilla stone hoe</li>
	 * </ul>
	 * No charge or durability is used.
	 */
	public static boolean isValidWrench(ItemStack s) {
		if(s == null)
			return false;

		Item i = s.getItem();
		if(i == Items.stone_hoe)
			return true;

		bcWrenchInterface = APILocator.getCrossModBC().getWrenchInterface();

		if(icWrenchItem == null) {
			icWrenchItem = APILocator.getCrossModIC2().getWrenchItem();
			if(icWrenchItem == null)
				icWrenchItem = new Object();
		}

		if(i == icWrenchItem || (bcWrenchInterface != null && bcWrenchInterface.isInstance(i)))
			return true;

		return false;
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}

	@Override
	public IPacket createS2CPacket(byte id) {
		switch(id) {
		case PKT_BLOCK_BREAKER_DESC: return new PacketBlockBreakerDescription();
		case PKT_ACT2_RECIPE_UPDATE: return new PacketACT2RecipeUpdate();
		default: return null;
		}
	}

	@Override
	public IPacket createC2SPacket(byte id) {
		switch(id) {
		case PKT_ACT2_RECIPE_UPDATE: return new PacketACT2RecipeUpdate();
		default: return null;
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		switch(ID) {
		case GUI_BUFFER:
			return new ContainerChest(player.inventory, (TileBuffer)tile);
		case GUI_TABLE:
			return new ContainerAutoCraftingMk2(player, (TileAutoCraftingMk2)tile);
		case GUI_CHEST:
			return new ContainerBlackHoleChest(player, (TileBlackHoleChest)tile);
		case GUI_DUPLICATOR:
			return new ContainerOneSlot(player, ((IDuplicator)tile).getGuiInventory());
		case GUI_MCT2_EDIT:
			return new ContainerMCT2Edit(player, ((TileMCT2)tile).getEditingInventory());
		case GUI_DEPLOYER:
			return new ContainerDeployer(player, (TileDeployer)tile);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		switch(ID) {
		case GUI_BUFFER:
			return new GuiChest(player.inventory, (TileBuffer)tile);
		case GUI_TABLE:
			return new GuiAutoCraftingMk2(player, (TileAutoCraftingMk2)tile);
		case GUI_CHEST:
			return new GuiBlackHoleChest(player, (TileBlackHoleChest)tile);
		case GUI_DUPLICATOR:
			return new GuiOneSlot(new ContainerOneSlot(player, ((IDuplicator)tile).getGuiInventory()), "tile.tubestuff.duplicator.name");
		case GUI_MCT2_EDIT:
			return new GuiMCT2Edit(new ContainerMCT2Edit(player, ((TileMCT2)tile).getEditingInventory()));
		case GUI_DEPLOYER:
			return new GuiDeployer(new ContainerDeployer(player, (TileDeployer)tile));
		}
		return null;
	}

	// crashes if LXP not installed
	public static int convertXPtoLXP_MB(int xp) {
		return (int)(LiquidXPMod.convertXPToMB(xp) + Math.random());
	}
}
