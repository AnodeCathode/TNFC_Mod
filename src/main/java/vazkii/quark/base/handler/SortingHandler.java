package vazkii.quark.base.handler;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import vazkii.quark.api.ICustomSorting;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.management.feature.InventorySorting;
import vazkii.quark.oddities.inventory.ContainerBackpack;

import java.util.*;
import java.util.function.Predicate;

public final class SortingHandler {

	private static final Comparator<ItemStack> FALLBACK_COMPARATOR = jointComparator(
			Comparator.comparingInt((ItemStack s) -> Item.getIdFromItem(s.getItem())),
			SortingHandler::damageCompare,
			(ItemStack s1, ItemStack s2) -> s2.getCount() - s1.getCount(),
			(ItemStack s1, ItemStack s2) -> s2.getDisplayName().compareTo(s1.getDisplayName()),
			(ItemStack s1, ItemStack s2) -> s2.hashCode() - s1.hashCode());

	private static final Comparator<ItemStack> FOOD_COMPARATOR = jointComparator(
			SortingHandler::foodHealCompare,
			SortingHandler::foodSaturationCompare);

	private static final Comparator<ItemStack> TOOL_COMPARATOR = jointComparator(
			SortingHandler::toolPowerCompare,
			SortingHandler::enchantmentCompare,
			SortingHandler::damageCompare);

	private static final Comparator<ItemStack> SWORD_COMPARATOR = jointComparator(
			SortingHandler::swordPowerCompare,
			SortingHandler::enchantmentCompare,
			SortingHandler::damageCompare);

	private static final Comparator<ItemStack> ARMOR_COMPARATOR = jointComparator(
			SortingHandler::armorSlotAndToughnessCompare,
			SortingHandler::enchantmentCompare,
			SortingHandler::damageCompare);

	private static final Comparator<ItemStack> BOW_COMPARATOR = jointComparator(
			SortingHandler::enchantmentCompare,
			SortingHandler::damageCompare);

	public static void sortInventory(EntityPlayer player, boolean forcePlayer) {
		if (!ModuleLoader.isFeatureEnabled(InventorySorting.class))
			return;

		Container c = player.openContainer;
		if (forcePlayer || c == null)
			c = player.inventoryContainer;

		boolean backpack = c instanceof ContainerBackpack;
		boolean playerContainer = c == player.inventoryContainer || backpack;

		for (Slot s : c.inventorySlots) {
			IInventory inv = s.inventory;
			if ((inv == player.inventory) == playerContainer) {
				if (!playerContainer && s instanceof SlotItemHandler) {
					sortInventory(((SlotItemHandler) s).getItemHandler());
				} else {
					InvWrapper wrapper = new InvWrapper(inv);
					if (playerContainer)
						sortInventory(wrapper, 9, 36);
					else sortInventory(wrapper);
				}
				break;
			}
		}

		if (backpack)
			for (Slot s : c.inventorySlots)
				if (s instanceof SlotItemHandler) {
					sortInventory(((SlotItemHandler) s).getItemHandler());
					break;
				}
	}

	public static void sortInventory(IItemHandler handler) {
		sortInventory(handler, 0);
	}

	public static void sortInventory(IItemHandler handler, int iStart) {
		sortInventory(handler, iStart, handler.getSlots());
	}

	public static void sortInventory(IItemHandler handler, int iStart, int iEnd) {
		List<ItemStack> stacks = new ArrayList<>();
		List<ItemStack> restore = new ArrayList<>();

		for (int i = iStart; i < iEnd; i++) {
			ItemStack stackAt = handler.getStackInSlot(i);
			restore.add(stackAt.copy());
			if (!stackAt.isEmpty())
				stacks.add(stackAt.copy());
		}

		mergeStacks(stacks);
		sortStackList(stacks);

		if (setInventory(handler, stacks, iStart, iEnd) == EnumActionResult.FAIL)
			setInventory(handler, restore, iStart, iEnd);
	}

	private static EnumActionResult setInventory(IItemHandler inventory, List<ItemStack> stacks, int iStart, int iEnd) {
		for (int i = iStart; i < iEnd; i++) {
			int j = i - iStart;
			ItemStack stack = j >= stacks.size() ? ItemStack.EMPTY : stacks.get(j);

			if (!stack.isEmpty() && !inventory.isItemValid(i, stack))
				return EnumActionResult.PASS;
		}

		for (int i = iStart; i < iEnd; i++) {
			int j = i - iStart;
			ItemStack stack = j >= stacks.size() ? ItemStack.EMPTY : stacks.get(j);

			inventory.extractItem(i, inventory.getSlotLimit(i), false);
			if (!stack.isEmpty())
				if (!inventory.insertItem(i, stack, false).isEmpty())
					return EnumActionResult.FAIL;
		}

		return EnumActionResult.SUCCESS;
	}

	private static void mergeStacks(List<ItemStack> list) {
		for (int i = 0; i < list.size(); i++) {
			ItemStack set = mergeStackWithOthers(list, i);
			list.set(i, set);
		}

		list.removeIf((ItemStack stack) -> stack.isEmpty() || stack.getCount() == 0);
	}

	private static ItemStack mergeStackWithOthers(List<ItemStack> list, int index) {
		ItemStack stack = list.get(index);
		if (stack.isEmpty())
			return stack;

		for (int i = 0; i < list.size(); i++) {
			if (i == index)
				continue;

			ItemStack stackAt = list.get(i);
			if (stackAt.isEmpty())
				continue;

			if (stackAt.getCount() < stackAt.getMaxStackSize() && ItemStack.areItemsEqual(stack, stackAt) && ItemStack.areItemStackTagsEqual(stack, stackAt)) {
				int setSize = stackAt.getCount() + stack.getCount();
				int carryover = Math.max(0, setSize - stackAt.getMaxStackSize());
				stackAt.setCount(carryover);
				stack.setCount(setSize - carryover);

				if (stack.getCount() == stack.getMaxStackSize())
					return stack;
			}
		}

		return stack;
	}

	public static void sortStackList(List<ItemStack> list) {
		list.sort(SortingHandler::stackCompare);
	}

	private static int stackCompare(ItemStack stack1, ItemStack stack2) {
		if (stack1 == stack2)
			return 0;
		if (stack1.isEmpty())
			return -1;
		if (stack2.isEmpty())
			return 1;

		if (ICustomSorting.hasSorting(stack1) && ICustomSorting.hasSorting(stack2)) {
			ICustomSorting sort1 = ICustomSorting.getSorting(stack1);
			ICustomSorting sort2 = ICustomSorting.getSorting(stack2);
			if (sort1.getSortingCategory().equals(sort2.getSortingCategory()))
				return sort1.getItemComparator().compare(stack1, stack2);
		}

		ItemType type1 = getType(stack1);
		ItemType type2 = getType(stack2);

		if (type1 == type2)
			return type1.comparator.compare(stack1, stack2);

		return type1.ordinal() - type2.ordinal();
	}

	private static ItemType getType(ItemStack stack) {
		for (ItemType type : ItemType.values())
			if (type.fitsInType(stack))
				return type;

		throw new RuntimeException("Having an ItemStack that doesn't fit in any type is impossible.");
	}

	private static Predicate<ItemStack> classPredicate(Class<? extends Item> clazz) {
		return (ItemStack s) -> !s.isEmpty() && clazz.isInstance(s.getItem());
	}

	private static Predicate<ItemStack> inverseClassPredicate(Class<? extends Item> clazz) {
		return classPredicate(clazz).negate();
	}

	private static Predicate<ItemStack> itemPredicate(List<Item> list) {
		return (ItemStack s) -> !s.isEmpty() && list.contains(s.getItem());
	}

	public static Comparator<ItemStack> jointComparator(Comparator<ItemStack> finalComparator, Comparator<ItemStack>[] otherComparators) {
		if (otherComparators == null)
			return jointComparator(finalComparator);

		Comparator<ItemStack>[] resizedArray = Arrays.copyOf(otherComparators, otherComparators.length + 1);
		resizedArray[otherComparators.length] = finalComparator;
		return jointComparator(resizedArray);
	}

	@SafeVarargs
	public static Comparator<ItemStack> jointComparator(Comparator<ItemStack>... comparators) {
		return jointComparatorFallback((ItemStack s1, ItemStack s2) -> {
			for (Comparator<ItemStack> comparator : comparators) {
				if (comparator == null)
					continue;

				int compare = comparator.compare(s1, s2);
				if (compare == 0)
					continue;

				return compare;
			}

			return 0;
		}, FALLBACK_COMPARATOR);
	}

	private static Comparator<ItemStack> jointComparatorFallback(Comparator<ItemStack> comparator, Comparator<ItemStack> fallback) {
		return (ItemStack s1, ItemStack s2) -> {
			int compare = comparator.compare(s1, s2);
			if (compare == 0)
				return fallback == null ? 0 : fallback.compare(s1, s2);

			return compare;
		};
	}

	private static Comparator<ItemStack> listOrderComparator(List<Item> list) {
		return (ItemStack stack1, ItemStack stack2) -> {
			Item i1 = stack1.getItem();
			Item i2 = stack2.getItem();
			if (list.contains(i1)) {
				if (list.contains(i2))
					return list.indexOf(i1) - list.indexOf(i2);
				return 1;
			}

			if (list.contains(i2))
				return -1;

			return 0;
		};
	}

	private static List<Item> list(Object... items) {
		List<Item> itemList = new ArrayList<>();
		for (Object o : items)
			if (o != null) {
				if (o instanceof Item)
					itemList.add((Item) o);
				else if (o instanceof Block)
					itemList.add(Item.getItemFromBlock((Block) o));
				else if (o instanceof ItemStack)
					itemList.add(((ItemStack) o).getItem());
				else if (o instanceof String) {
					Item i = Item.getByNameOrId((String) o);
					if (i != null)
						itemList.add(i);
				}
			}

		return itemList;
	}

	private static int foodHealCompare(ItemStack stack1, ItemStack stack2) {
		return ((ItemFood) stack2.getItem()).getHealAmount(stack2) - ((ItemFood) stack1.getItem()).getHealAmount(stack1);
	}

	private static int foodSaturationCompare(ItemStack stack1, ItemStack stack2) {
		return (int) (((ItemFood) stack2.getItem()).getSaturationModifier(stack2) * 100 - ((ItemFood) stack1.getItem()).getSaturationModifier(stack1) * 100);
	}

	private static int enchantmentCompare(ItemStack stack1, ItemStack stack2) {
		return enchantmentPower(stack2) - enchantmentPower(stack1);
	}

	private static int enchantmentPower(ItemStack stack) {
		if (!stack.isItemEnchanted())
			return 0;

		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		int total = 0;

		for (Integer i : enchantments.values())
			total += i;

		return total;
	}

	private static int toolPowerCompare(ItemStack stack1, ItemStack stack2) {
		ToolMaterial mat1 = ObfuscationReflectionHelper.getPrivateValue(ItemTool.class, (ItemTool) stack1.getItem(), LibObfuscation.TOOL_MATERIAL);
		ToolMaterial mat2 = ObfuscationReflectionHelper.getPrivateValue(ItemTool.class, (ItemTool) stack2.getItem(), LibObfuscation.TOOL_MATERIAL);
		return (int) (mat2.getEfficiency() * 100 - mat1.getEfficiency() * 100);
	}

	private static int swordPowerCompare(ItemStack stack1, ItemStack stack2) {
		return (int) (((ItemSword) stack2.getItem()).getAttackDamage() * 100 - ((ItemSword) stack1.getItem()).getAttackDamage() * 100);
	}

	private static int armorSlotAndToughnessCompare(ItemStack stack1, ItemStack stack2) {
		ItemArmor armor1 = (ItemArmor) stack1.getItem();
		ItemArmor armor2 = (ItemArmor) stack2.getItem();

		EntityEquipmentSlot slot1 = armor1.armorType;
		EntityEquipmentSlot slot2 = armor2.armorType;

		if (slot1 == slot2)
			return armor2.getArmorMaterial().getDamageReductionAmount(slot2) - armor2.getArmorMaterial().getDamageReductionAmount(slot1);

		return slot2.getIndex() - slot1.getIndex();
	}

	private static int damageCompare(ItemStack stack1, ItemStack stack2) {
		return stack1.getItemDamage() - stack2.getItemDamage();
	}

	private enum ItemType {

		FOOD(classPredicate(ItemFood.class), FOOD_COMPARATOR),
		TORCH(list(Blocks.TORCH)),
		TOOL_PICKAXE(classPredicate(ItemPickaxe.class), TOOL_COMPARATOR),
		TOOL_SHOVEL(classPredicate(ItemSpade.class), TOOL_COMPARATOR),
		TOOL_AXE(classPredicate(ItemAxe.class), TOOL_COMPARATOR),
		TOOL_SWORD(classPredicate(ItemSword.class), SWORD_COMPARATOR),
		TOOL_GENERIC(classPredicate(ItemTool.class), TOOL_COMPARATOR),
		ARMOR(classPredicate(ItemArmor.class), ARMOR_COMPARATOR),
		BOW(classPredicate(ItemBow.class), BOW_COMPARATOR),
		ARROWS(classPredicate(ItemArrow.class)),
		POTION(classPredicate(ItemPotion.class)),
		REDSTONE(list(Items.REDSTONE, Blocks.REDSTONE_TORCH, Items.REPEATER, Items.COMPARATOR, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON)),
		MINECART(classPredicate(ItemMinecart.class)),
		RAIL(list(Blocks.RAIL, Blocks.GOLDEN_RAIL, Blocks.DETECTOR_RAIL, Blocks.ACTIVATOR_RAIL)),
		DYE(classPredicate(ItemDye.class)),
		ANY(inverseClassPredicate(ItemBlock.class)),
		BLOCK(classPredicate(ItemBlock.class));

		private final Predicate<ItemStack> predicate;
		private final Comparator<ItemStack> comparator;

		@SafeVarargs
		ItemType(List<Item> list, Comparator<ItemStack>... comparators) {
			this(itemPredicate(list), jointComparator(listOrderComparator(list), comparators));
		}

		ItemType(Predicate<ItemStack> predicate) {
			this(predicate, FALLBACK_COMPARATOR);
		}

		ItemType(Predicate<ItemStack> predicate, Comparator<ItemStack> comparator) {
			this.predicate = predicate;
			this.comparator = comparator;
		}

		public boolean fitsInType(ItemStack stack) {
			return predicate.test(stack);
		}

	}

}

