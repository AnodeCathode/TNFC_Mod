package tnfcmod.util;

/**
 * Just a helper class to store which equipment we should give to some entities
 * If needed, this functionality can be extended via json
 * and if this is done, may as well merge into entity resistance data
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemMetalArmor;
import net.dries007.tfc.objects.items.metal.ItemMetalSword;
import net.dries007.tfc.util.collections.WeightedCollection;


public class MonsterGear
{
    private static final Map<String, MonsterGear> ENTRIES = new HashMap<>();

    static
    {
        //Would need to rework this to make it all work.
        WeightedCollection<Metal> metals = new WeightedCollection<>();
        metals.add(0.8, Metal.BRONZE);
        metals.add(0.7, Metal.BLACK_BRONZE);
        metals.add(0.7, Metal.BISMUTH_BRONZE);
        metals.add(0.3, Metal.WROUGHT_IRON);
        metals.add(0.2, Metal.STEEL);
        metals.add(0.1, Metal.BLUE_STEEL);
        metals.add(0.1, Metal.RED_STEEL);

        WeightedCollection<ItemStack> weapons = new WeightedCollection<>();
        weapons.add(0.4, ItemStack.EMPTY);
        weapons.add(0.2, new ItemStack(ItemMetal.get(Metal.BRONZE, Metal.ItemType.MACE)));
        weapons.add(0.2, new ItemStack(ItemMetalSword.get(Metal.BRONZE)));
        weapons.add(0.2, new ItemStack(ItemMetal.get(Metal.BRONZE, Metal.ItemType.KNIFE)));

        WeightedCollection<ItemStack> rangedWeapons = new WeightedCollection<>();
        rangedWeapons.add(0.7, new ItemStack(Items.BOW));
        rangedWeapons.add(0.1, new ItemStack(ItemMetal.get(Metal.BRONZE, Metal.ItemType.MACE)));
        rangedWeapons.add(0.1, new ItemStack(ItemMetalSword.get(Metal.BRONZE)));
        rangedWeapons.add(0.2, new ItemStack(ItemMetal.get(Metal.BRONZE, Metal.ItemType.KNIFE)));

        WeightedCollection<ItemStack> helmets = new WeightedCollection<>();
        helmets.add(0.8, ItemStack.EMPTY);
        helmets.add(0.2, new ItemStack(ItemMetalArmor.get(Metal.BRONZE, Metal.ItemType.HELMET)));

        WeightedCollection<ItemStack> chestplates = new WeightedCollection<>();
        chestplates.add(0.8, ItemStack.EMPTY);
        chestplates.add(0.2, new ItemStack(ItemMetalArmor.get(Metal.BRONZE, Metal.ItemType.CHESTPLATE)));

        WeightedCollection<ItemStack> leggings = new WeightedCollection<>();
        leggings.add(0.8, ItemStack.EMPTY);
        leggings.add(0.2, new ItemStack(ItemMetalArmor.get(Metal.BRONZE, Metal.ItemType.GREAVES)));

        WeightedCollection<ItemStack> boots = new WeightedCollection<>();
        boots.add(0.8, ItemStack.EMPTY);
        boots.add(0.2, new ItemStack(ItemMetalArmor.get(Metal.BRONZE, Metal.ItemType.BOOTS)));

        MonsterGear equipment = new MonsterGear(weapons, helmets, chestplates, leggings, boots);
        MonsterGear rangedEquipment = new MonsterGear(rangedWeapons, helmets, chestplates, leggings, boots);

        // Register to some vanilla mobs
        // Do some of these even spawn? I think not...
        ENTRIES.put("minecraft:zombie_pigman", equipment);
        ENTRIES.put("minecraft:zombie", equipment);
    }

    @Nullable
    public static MonsterGear get(Entity entity)
    {
        ResourceLocation entityType = EntityList.getKey(entity);
        if (entityType != null)
        {
            String entityTypeName = entityType.toString();
            return ENTRIES.get(entityTypeName);
        }
        return null;
    }

    @Nullable
    public static MonsterGear get(String entityId)
    {
        return ENTRIES.get(entityId);
    }

    public static void put(String entityId, MonsterGear equipment)
    {
        ENTRIES.put(entityId, equipment);
    }

    private final Map<EntityEquipmentSlot, WeightedCollection<ItemStack>> equipment;

    public MonsterGear(WeightedCollection<ItemStack> weapons, WeightedCollection<ItemStack> helmets, WeightedCollection<ItemStack> chestplates, WeightedCollection<ItemStack> leggings, WeightedCollection<ItemStack> boots)
    {
        equipment = new ImmutableMap.Builder<EntityEquipmentSlot, WeightedCollection<ItemStack>>()
            .put(EntityEquipmentSlot.MAINHAND, weapons)
            .put(EntityEquipmentSlot.HEAD, helmets)
            .put(EntityEquipmentSlot.CHEST, chestplates)
            .put(EntityEquipmentSlot.LEGS, leggings)
            .put(EntityEquipmentSlot.FEET, boots)
            .build();
    }

    public Optional<ItemStack> getEquipment(EntityEquipmentSlot slot, Random random)
    {
        if (equipment.containsKey(slot))
        {
            return Optional.of(equipment.get(slot).getRandomEntry(random));
        }
        return Optional.empty();
    }
}
