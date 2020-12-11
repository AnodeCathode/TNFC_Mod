package vazkii.quark.client;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.client.feature.*;

public class QuarkClient extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new ChestSearchBar());
		registerFeature(new AngryCreepers(), "Creepers turn red when they're exploding");
		registerFeature(new ShulkerBoxTooltip());
		registerFeature(new FoodTooltip());
		registerFeature(new GreenerGrass());
		registerFeature(new ImprovedMountHUD());
		registerFeature(new MapTooltip());
		registerFeature(new NoPotionShift());
		registerFeature(new RandomAnimalTextures());
		registerFeature(new LessIntrusiveShields());
		registerFeature(new BetterVanillaTextures());
		registerFeature(new VisualStatDisplay());
		registerFeature(new AutoJumpHotkey());
		registerFeature(new PanoramaMaker());
		registerFeature(new ImprovedSignEdit());
		registerFeature(new UsageTicker());
		registerFeature(new ItemsFlashBeforeExpiring());
		registerFeature(new BetterFireEffect());
		registerFeature(new EnchantedBooksShowItems());
		registerFeature(new ShowInvalidSlots());
		registerFeature(new RenderItemsInChat());
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Items.ENDER_EYE);
	}
	
}
