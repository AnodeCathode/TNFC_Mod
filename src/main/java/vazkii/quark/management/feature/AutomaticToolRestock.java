package vazkii.quark.management.feature;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.*;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.quark.base.module.Feature;

import java.util.*;
import java.util.function.Predicate;

public class AutomaticToolRestock extends Feature {

	private static final WeakHashMap<EntityPlayer, Stack<Pair<Integer, Integer>>> replacements = new WeakHashMap<>();

	public final List<Enchantment> importantEnchants = new ArrayList<>();
	private String[] enchantNames;
	private boolean enableLooseMatching;
	private boolean enableEnchantMatching;
	private boolean unstackablesOnly;

	@Override
	public void setupConfig() {
		enchantNames = loadPropStringList("Important Enchantments", "Enchantments deemed important enough to have special priority when finding a replacement", generateDefaultEnchantmentList());
		enableLooseMatching = loadPropBool("Enable Loose Matching", "Enable replacing your tools with tools of the same type but not the same item", true);
		enableEnchantMatching = loadPropBool("Enable Enchantment Matching", "Enable comparing enchantments to find a replacement", true);
		unstackablesOnly = loadPropBool("Unstackable Items Only", "", false);
	}

	@Override
	public void postInit() {
		initializeEnchantmentList(enchantNames, importantEnchants);
	}

	@SubscribeEvent
	public void onToolBreak(PlayerDestroyItemEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = event.getOriginal();
		Item item = stack.getItem();

		if(player != null && player.world != null && !player.world.isRemote && !stack.isEmpty() && !(item instanceof ItemArmor) && (!unstackablesOnly || !stack.isStackable())) {
			int currSlot = player.inventory.currentItem;
			if(event.getHand() == EnumHand.OFF_HAND)
				currSlot = player.inventory.getSizeInventory() - 1;

			List<Enchantment> enchantmentsOnStack = getImportantEnchantments(stack);
			Predicate<ItemStack> itemPredicate = (other) -> other.getItem() == item;
			if(!stack.isItemStackDamageable())
				itemPredicate = itemPredicate.and((other) -> other.getItemDamage() == stack.getItemDamage());

			Predicate<ItemStack> enchantmentPredicate = (other) -> !(new ArrayList<>(enchantmentsOnStack)).retainAll(getImportantEnchantments(other));

			if(enableEnchantMatching && findReplacement(player, currSlot, itemPredicate.and(enchantmentPredicate)))
				return;

			if(findReplacement(player, currSlot, itemPredicate))
				return;

			if(enableLooseMatching) {
				Set<String> classes = getItemClasses(stack);

				if(!classes.isEmpty()) {
					Predicate<ItemStack> toolPredicate = (other) -> {
						Set<String> otherClasses = getItemClasses(other);
						return !otherClasses.isEmpty() && !otherClasses.retainAll(classes);
					};

					if(enableEnchantMatching && !enchantmentsOnStack.isEmpty() && findReplacement(player, currSlot, toolPredicate.and(enchantmentPredicate)))
						return;

					findReplacement(player, currSlot, toolPredicate);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == Phase.END && replacements.containsKey(event.player)) {
			Stack<Pair<Integer, Integer>> replacementStack = replacements.get(event.player);
			synchronized(this) {
				while(!replacementStack.isEmpty()) {
					Pair<Integer, Integer> pair = replacementStack.pop();
					switchItems(event.player, pair.getLeft(), pair.getRight());
				}
			}
		}
	}

	private HashSet<String> getItemClasses(ItemStack stack) {
		Item item = stack.getItem();
		if(item instanceof ItemTool)
			return new HashSet<>(item.getToolClasses(stack));
		else if(item instanceof ItemSword)
			return new HashSet<>(Collections.singletonList("sword"));
		else if(item instanceof ItemBow)
			return new HashSet<>(Collections.singletonList("bow"));
		else if(item instanceof ItemFishingRod)
			return new HashSet<>(Collections.singletonList("fishing_rod"));

		return new HashSet<>();
	}

	private boolean findReplacement(EntityPlayer player, int currSlot, Predicate<ItemStack> match) {
		for(int i = 0; i < player.inventory.mainInventory.size(); i++) {
			if(i == currSlot)
				continue;

			ItemStack stackAt = player.inventory.getStackInSlot(i);
			if(!stackAt.isEmpty() && match.test(stackAt)) {
				pushReplace(player, i, currSlot);
				return true;
			}
		}

		return false;
	}

	private void pushReplace(EntityPlayer player, int slot1, int slot2) {
		synchronized(this) {
			if(!replacements.containsKey(player))
				replacements.put(player, new Stack<>());
			replacements.get(player).push(Pair.of(slot1, slot2));
		}
	}

	private void switchItems(EntityPlayer player, int slot1, int slot2) {
		int size = player.inventory.mainInventory.size();
		if(slot1 >= size || slot2 >= size)
			return;

		ItemStack stackAtSlot1 = player.inventory.getStackInSlot(slot1);
		ItemStack stackAtSlot2 = player.inventory.getStackInSlot(slot2);

		player.inventory.setInventorySlotContents(slot2, stackAtSlot1);
		player.inventory.setInventorySlotContents(slot1, stackAtSlot2);
	}

	private List<Enchantment> getImportantEnchantments(ItemStack stack) {
		List<Enchantment> enchantsOnStack = new ArrayList<>();
		for(Enchantment ench : importantEnchants)
			if(EnchantmentHelper.getEnchantmentLevel(ench, stack) > 0)
				enchantsOnStack.add(ench);

		return enchantsOnStack;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "inventorytweaks" };
	}

	private String[] generateDefaultEnchantmentList() {
		Enchantment[] enchants = new Enchantment[] {
				Enchantments.SILK_TOUCH,
				Enchantments.FORTUNE,
				Enchantments.INFINITY,
				Enchantments.LUCK_OF_THE_SEA,
				Enchantments.LOOTING
		};

		List<String> strings = new ArrayList<>();
		for(Enchantment e : enchants)
			if(e != null && e.getRegistryName() != null)
				strings.add(e.getRegistryName().toString());

		return strings.toArray(new String[0]);
	}

}
