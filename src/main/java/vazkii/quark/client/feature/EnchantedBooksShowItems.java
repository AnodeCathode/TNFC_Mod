package vazkii.quark.client.feature;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.feature.AncientTomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnchantedBooksShowItems extends Feature {

	private static final List<Pair<ResourceLocation, Integer>> testItemLocations = new ArrayList<>();
	private static final List<ItemStack> testItems = new ArrayList<>();

	private static Multimap<Enchantment, ItemStack> additionalStacks = null;
	private static String[] additionalStacksArr;

	private static final Pattern RL_MATCHER = Pattern.compile("^((?:\\w+:)?\\w+)(@\\d+)?$");

	@Override
	public void setupConfig() {
		String[] testItemsArr = loadPropStringList("Items to Test", "", new String[] {
				"minecraft:diamond_sword", "minecraft:diamond_pickaxe", "minecraft:diamond_shovel", "minecraft:diamond_axe", "minecraft:diamond_hoe",
				"minecraft:diamond_helmet", "minecraft:diamond_chestplate", "minecraft:diamond_leggings", "minecraft:diamond_boots",
				"minecraft:shears", "minecraft:bow", "minecraft:fishing_rod", "minecraft:elytra", "quark:pickarang"
		});

		testItemLocations.clear();
		testItems.clear();
		for(String s : testItemsArr) {
			Matcher match = RL_MATCHER.matcher(s);
			if (match.matches()) {
				String metaGroup = match.group(2);
				int meta = metaGroup == null || metaGroup.isEmpty() ? 0 : Integer.parseInt(metaGroup);
				testItemLocations.add(Pair.of(new ResourceLocation(match.group(1)), meta));
			}
		}

		additionalStacksArr = loadPropStringList("Additional Stacks", 
				"A list of additional stacks to display on each enchantment\n"
						+ "The format is as follows:\n"
						+ "enchant_id=item1,item2,item3...\n"
						+ "So to display a carrot on a stick on a mending book, for example, you use:\n"
						+ "minecraft:mending=minecraft:carrot_on_a_stick",
						new String[0]);

	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void makeTooltip(ItemTooltipEvent event) {
		if(Minecraft.getMinecraft().player == null)
			return;

		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Items.ENCHANTED_BOOK || stack.getItem() == AncientTomes.ancient_tome) {
			Minecraft mc = Minecraft.getMinecraft();
			List<String> tooltip = event.getToolTip();
			int tooltipIndex = 0;

			List<EnchantmentData> enchants = getEnchantedBookEnchantments(stack);
			for(EnchantmentData ed : enchants) {
				String match = ed.enchantment.getTranslatedName(ed.enchantmentLevel);

				for(; tooltipIndex < tooltip.size(); tooltipIndex++)
					if(tooltip.get(tooltipIndex).equals(match)) {
						List<ItemStack> items = getItemsForEnchantment(ed.enchantment);
						if(!items.isEmpty()) {
							int len = 3 + items.size() * 9;
							String spaces = "";
							while(mc.fontRenderer.getStringWidth(spaces) < len)
								spaces += " ";

							tooltip.add(tooltipIndex + 1, spaces);
						}

						break;
					}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderTooltip(RenderTooltipEvent.PostText event) {
		ItemStack stack = event.getStack();

		if(stack.getItem() == Items.ENCHANTED_BOOK || stack.getItem() == AncientTomes.ancient_tome) {
			Minecraft mc = Minecraft.getMinecraft();
			List<String> tooltip = event.getLines();

			GlStateManager.pushMatrix();
			GlStateManager.translate(event.getX(), event.getY() + 12, 0);
			GlStateManager.scale(0.5, 0.5, 1.0);

			List<EnchantmentData> enchants = getEnchantedBookEnchantments(stack);
			for(EnchantmentData ed : enchants) {
				String match = TextFormatting.getTextWithoutFormattingCodes(ed.enchantment.getTranslatedName(ed.enchantmentLevel));
				for(int tooltipIndex = 0; tooltipIndex < tooltip.size(); tooltipIndex++) {
					String line = TextFormatting.getTextWithoutFormattingCodes(tooltip.get(tooltipIndex));
					if(line != null && line.equals(match)) {
						int drawn = 0;

						List<ItemStack> items = getItemsForEnchantment(ed.enchantment);
						for(ItemStack testStack : items) {
							mc.getRenderItem().renderItemIntoGUI(testStack, 6 + drawn * 18, tooltipIndex * 20 - 2);
							drawn++;
						}

						break;
					}
				}
			}
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	public static List<ItemStack> getItemsForEnchantment(Enchantment e) {
		List<ItemStack> list = new ArrayList<>();
		if (testItems.isEmpty() && !testItemLocations.isEmpty()) {
			for (Pair<ResourceLocation, Integer> loc : testItemLocations) {
				Item item = Item.REGISTRY.getObject(loc.getKey());
				if (item != null)
					testItems.add(new ItemStack(item, 1, loc.getValue()));
			}
		}

		for(ItemStack stack : testItems) {
			if (e.canApply(stack))
				list.add(stack);
		}

		if(getAdditionalStacks().containsKey(e))
			list.addAll(getAdditionalStacks().get(e));

		return list;
	}

	public static List<EnchantmentData> getEnchantedBookEnchantments(ItemStack stack) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

		List<EnchantmentData> retList = new ArrayList<>(enchantments.size());

		for(Enchantment enchantment : enchantments.keySet()) {
			if (enchantment != null) {
				int level = enchantments.get(enchantment);
				retList.add(new EnchantmentData(enchantment, level));
			}
		}

		return retList;
	}

	private static Multimap<Enchantment, ItemStack> getAdditionalStacks() {
		if (additionalStacks == null)
			computeAdditionalStacks();
		return additionalStacks;
	}

	private static void computeAdditionalStacks() {
		additionalStacks = HashMultimap.create();

		for(String s : additionalStacksArr) {
			if(!s.contains("="))
				continue;

			String[] tokens = s.split("=");
			String left = tokens[0];
			String right = tokens[1];

			Enchantment ench = Enchantment.REGISTRY.getObject(new ResourceLocation(left));
			if(ench != null) {
				tokens = right.split(",");

				for(String itemId : tokens) {
					Item item = Item.REGISTRY.getObject(new ResourceLocation(itemId));
					if(item != null)
						additionalStacks.put(ench, new ItemStack(item));
				}
			}
		}
	}

}
