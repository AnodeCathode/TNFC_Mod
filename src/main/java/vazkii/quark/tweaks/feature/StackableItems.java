/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 23:01:43 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

import java.util.regex.Pattern;

public class StackableItems extends Feature {

	public static String[] items;

	@Override
	public void setupConfig() {
		int minecarts = 16;
		int soups = 64;
		int saddle = 8;

		if (hasConfigKey("Minecarts")) {
			minecarts = Math.min(64, loadPropInt("Minecarts", "", 16));
			removeLegacyKey("Minecarts");
		}

		if (hasConfigKey("Soups")) {
			soups = Math.min(64, loadPropInt("Soups", "", 64));
			removeLegacyKey("Soups");
		}

		if (hasConfigKey("Saddle")) {
			saddle = Math.min(64, loadPropInt("Saddle", "", 8));
			removeLegacyKey("Saddle");
		}

		items = loadPropStringList("Stackable Items", "The format for setting an item's max size is item name|stacksize. i.e. `minecraft:saddle|8`",
				new String[] {
						Items.MINECART.getRegistryName() + "|" + minecarts,
						Items.CHEST_MINECART.getRegistryName() + "|" + minecarts,
						Items.COMMAND_BLOCK_MINECART.getRegistryName() + "|" + minecarts,
						Items.FURNACE_MINECART.getRegistryName() + "|" + minecarts,
						Items.HOPPER_MINECART.getRegistryName() + "|" + minecarts,
						Items.TNT_MINECART.getRegistryName() + "|" + minecarts,

						Items.MUSHROOM_STEW.getRegistryName() + "|" + soups,
						Items.RABBIT_STEW.getRegistryName() + "|" + soups,
						Items.BEETROOT_SOUP.getRegistryName() + "|" + soups,

						Items.SADDLE.getRegistryName() + "|" + saddle
				});
	}

	@Override
	public void init() {
		for (String key : items) {
			String[] split = key.split("\\|");
			if (split.length == 2 && Pattern.matches("\\d+", split[1])) {
				ResourceLocation loc = new ResourceLocation(split[0]);
				int size = Math.min(64, Math.max(0, Integer.parseInt(split[1])));

				for (Item item : Item.REGISTRY) {
					if (loc.equals(item.getRegistryName())) {
						item.setMaxStackSize(size);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void finishEvent(LivingEntityUseItemEvent.Finish event) {
		if(event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			ItemStack original = event.getItem();
			ItemStack result = event.getResultStack();
			if(original.getCount() > 1 && (result.getItem() == Items.BOWL || result.getItem() == Items.BUCKET)) {
				ItemStack newResult = original.copy();
				newResult.setCount(original.getCount() - 1);
				event.setResultStack(newResult);
				player.addItemStackToInventory(result);
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
