package vazkii.quark.oddities.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageHandleBackpack;
import vazkii.quark.oddities.client.gui.GuiBackpackInventory;
import vazkii.quark.oddities.item.ItemBackpack;

import javax.annotation.Nonnull;
import java.util.Random;

public class Backpacks extends Feature {

	public static Item backpack;
	
	public static boolean superOpMode, enableTrades, enableCrafting;

	public static  int leatherCount, minEmeralds, maxEmeralds;
	
	@SideOnly(Side.CLIENT)
	public static boolean backpackRequested;
	
	@Override
	public void setupConfig() {
		enableTrades = loadPropBool("Enable Trade", "Set this to false if you want to disable the villager trade so you can add an alternate acquisition method", true);
		enableCrafting = loadPropBool("Enable Crafting", "Set this to true to enable a crafting recipe", false);
		superOpMode = loadPropBool("Unbalanced Mode", "Set this to true to allow the backpacks to be unequipped even with items in them", false);
		leatherCount = loadPropInt("Required Leather", "", 12);
		minEmeralds = loadPropInt("Min Required Emeralds", "", 12);
		maxEmeralds = loadPropInt("Max Required Emeralds", "", 18);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		backpack = new ItemBackpack();
		
		if(enableCrafting)
			RecipeHandler.addOreDictRecipe(new ItemStack(backpack), 
					"LLL", "LCL", "LLL",
					'L', new ItemStack(Items.LEATHER),
					'C', "chestWood");
	}
	
	@SubscribeEvent
	public void onRegisterVillagers(RegistryEvent.Register<VillagerProfession> event) {
		if(!enableTrades)
			return;
		
		VillagerProfession butcher = event.getRegistry().getValue(new ResourceLocation("minecraft:butcher"));
		if (butcher != null) {
			VillagerCareer leatherworker = butcher.getCareer(1);

			leatherworker.addTrade(1, new BackpackTrade());
		}
 	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onOpenGUI(GuiOpenEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null && isInventoryGUI(event.getGui()) && !player.isCreative() && isEntityWearingBackpack(player)) {
			requestBackpack();
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void clientTick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if(isInventoryGUI(mc.currentScreen) && !backpackRequested && isEntityWearingBackpack(mc.player)) {
			requestBackpack();
			backpackRequested = true;
		} else if(mc.currentScreen instanceof GuiBackpackInventory)
			backpackRequested = false;
	}
	
	private void requestBackpack() {
		NetworkHandler.INSTANCE.sendToServer(new MessageHandleBackpack(true));
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void removeCurseTooltip(ItemTooltipEvent event) {
		if(!superOpMode && event.getItemStack().getItem() instanceof ItemBackpack)
			for(String s : event.getToolTip())
				if(s.equals(Enchantments.BINDING_CURSE.getTranslatedName(1))) {
					event.getToolTip().remove(s);
					return;
				}
	}
	
	@SideOnly(Side.CLIENT)
	private static boolean isInventoryGUI(GuiScreen gui) {
		return gui != null && gui.getClass() == GuiInventory.class;
	}
	
	public static boolean isEntityWearingBackpack(Entity e) {
		if(e instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) e;
			ItemStack chestArmor = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			return chestArmor.getItem() instanceof ItemBackpack;
		}
		
		return false;
	}
	
	public static boolean isEntityWearingBackpack(Entity e, ItemStack stack) {
		if(e instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) e;
			ItemStack chestArmor = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			return chestArmor == stack;
		}
		
		return false;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	public static class BackpackTrade implements EntityVillager.ITradeList {

		@Override
		public void addMerchantRecipe(@Nonnull IMerchant merchant, @Nonnull MerchantRecipeList recipeList, @Nonnull Random random) {
			int emeraldCount = random.nextInt(maxEmeralds - minEmeralds) + minEmeralds;
			recipeList.add(new MerchantRecipe(new ItemStack(Items.LEATHER, leatherCount), new ItemStack(Items.EMERALD, emeraldCount), new ItemStack(backpack)));
		}
	}

}
