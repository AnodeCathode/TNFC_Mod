package vazkii.quark.world.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.client.gui.GuiArchaeologist;
import vazkii.quark.world.client.render.RenderArchaeologist;
import vazkii.quark.world.entity.EntityArchaeologist;
import vazkii.quark.world.item.ItemArchaeologistHat;
import vazkii.quark.world.world.ArchaeologistHouseGenerator;

import java.util.List;

public class Archaeologist extends Feature {

	public static final ResourceLocation HOUSE_STRUCTURE = new ResourceLocation("quark", "archaeologist_house");

	public static double chance;
	public static int maxY, minY;
	public static DimensionConfig dims;
	
	public static Item archaeologist_hat;
	
	public static boolean enableHat, sellHat, dropHat, hatIncreasesOreYield;
	public static float increaseChance;

	@Override
	public void setupConfig() {
		chance = loadLegacyPropChance("Percentage Chance Per Chunk", "Chance Per Chunk", "The chance that the generator will attempt to place an Archaeologist in a chunk", 0.1);
		maxY = loadPropInt("Max Y", "", 50);
		minY = loadPropInt("Min Y", "", 20);
		dims = new DimensionConfig(configCategory);
		
		enableHat = loadPropBool("Enable Hat", "", true);
		sellHat = loadPropBool("Sell Hat", "Set to false to make the archaeologist not sell the hat", true);
		dropHat = loadPropBool("Drop Hat", "Set to false to make the archaeologist not drop the hat", true);
		hatIncreasesOreYield = loadPropBool("Hat Increases Ore Yield" , "Set to false to make the hat not increase ore yield", true);
		increaseChance = (float) loadPropDouble("Yield Increase Chance", "The chance for the hat to increase ore yield, 0 is 0%, 1 is 100%", 0.25);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(enableHat)
			archaeologist_hat = new ItemArchaeologistHat();
		
		String archaeologistName = "quark:archaeologist";
		EntityRegistry.registerModEntity(new ResourceLocation(archaeologistName), EntityArchaeologist.class, archaeologistName, LibEntityIDs.ARCHAEOLOGIST, Quark.instance, 80, 3, true, 0xb5966e, 0xb37b62);

		GameRegistry.registerWorldGenerator(new ArchaeologistHouseGenerator(), 3000);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityArchaeologist.class, RenderArchaeologist.FACTORY);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onGuiOpen(GuiOpenEvent event) {
		GuiScreen gui = event.getGui();
		if (gui instanceof GuiMerchant) {
			GuiMerchant guiMerchant = (GuiMerchant) gui;
			IMerchant merchant = guiMerchant.getMerchant();
			if (merchant instanceof EntityArchaeologist) {
				EntityPlayer player = Minecraft.getMinecraft().player;
				event.setGui(new GuiArchaeologist(player.inventory, merchant, player.world));
			}
		}
	}
	
	@SubscribeEvent
	public void onDrops(HarvestDropsEvent event) {
		if(enableHat && hatIncreasesOreYield) {
			EntityPlayer player = event.getHarvester();
			if(player == null)
				return;
			
			ItemStack hat = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			if(hat.getItem() == archaeologist_hat) {
				List<ItemStack> drops = event.getDrops();
				if(drops.size() == 1) {
					ItemStack drop = drops.get(0);
					if(!drop.isEmpty() && !(drop.getItem() instanceof ItemBlock) && drop.getCount() < drop.getMaxStackSize()) {
						IBlockState state = event.getState();
						Block block = state.getBlock();
						ItemStack stack = new ItemStack(block);
						if (!stack.isEmpty()) {
							int[] ids = OreDictionary.getOreIDs(stack);

							for (int i : ids) {
								String name = OreDictionary.getOreName(i);
								if (name.matches("^ore[A-Z][a-zA-Z]+$")) {
									if (player.world.rand.nextFloat() < increaseChance) {
										drop.grow(1);
									}

									break;
								}
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("SpellCheckingInspection")
	public void missingItemMappings(RegistryEvent.MissingMappings<Item> event) {
		for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings()) {
			if (mapping.key.getPath().equals("archeologist_hat"))
				mapping.remap(archaeologist_hat);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("SpellCheckingInspection")
	public void missingEntityMappings(RegistryEvent.MissingMappings<EntityEntry> event) {
		for (RegistryEvent.MissingMappings.Mapping<EntityEntry> mapping : event.getMappings()) {
			if (mapping.key.getPath().equals("archeologist")) {
				EntityEntry entry = EntityRegistry.getEntry(EntityArchaeologist.class);
				if (entry != null)
					mapping.remap(entry);
			}
		}
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
