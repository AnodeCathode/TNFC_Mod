package tnfcmod.recipes;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;

import tnfcmod.tnfcmod;

import static tnfcmod.tnfcmod.MODID;

public class LootTablesTNFC
{
    public static ResourceLocation ENTITIES_ZOMBIE_PIGMAN;
    public static ResourceLocation ENTITIES_MAGMA_CUBE;
    public static ResourceLocation ENTITIES_WITHER_SKELETON;
    public static ResourceLocation CHESTS_NETHER_BRIDGE;
    public static ResourceLocation CHESTS_END_CITY_TREASURE;
    public static ResourceLocation GAMEPLAY_FISHING_JUNK;
    public static ResourceLocation GAMEPLAY_FISHING_TREASURE;


    public static void init()
    {

        ENTITIES_ZOMBIE_PIGMAN = register("entities/zombie_pigman");
        ENTITIES_MAGMA_CUBE = register("entities/magma_cube");
        ENTITIES_WITHER_SKELETON = register("entities/wither_skeleton");
        CHESTS_NETHER_BRIDGE = register("chests/nether_bridge");
        CHESTS_END_CITY_TREASURE = register("chests/end_city_treasure");
        GAMEPLAY_FISHING_JUNK = register("gameplay/fishing/junk");
        GAMEPLAY_FISHING_TREASURE = register("gameplay/fishing/treasure");

    }

    public static void modifyLootTableLoad(LootTableLoadEvent event)
    {

        //Okay here we are. We have a loot table being registered. Now what?
        //Get rid of vanilla stuff in entities



        //Get rid of vanilla stuff in dungeon chests
        remove(event, "minecraft:chests/nether_bridge", "main");
        remove(event, "minecraft:chests/end_city_treasure", "main");

        //Get rid of gameplay (fishing) loots we don't like
        remove(event, "minecraft:gameplay/fishing/junk", "main");
        remove(event, "minecraft:gameplay/fishing/treasure", "main");
        remove(event, "betterwithmods:gameplay/fishing/junk", "main");

        // Fix ZombiePigman
        if ("minecraft:entities/zombie_pigman".equals(event.getName().toString()))
        {
            tnfcmod.getLog().info("Modifying Zombie Pigman Loot Table entries");
            remove(event, "minecraft:entities/zombie_pigman", "pool1");
            remove(event, "minecraft:entities/zombie_pigman", "pool2");
            //Drop TFC Gold Nuggets
            event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(ENTITIES_ZOMBIE_PIGMAN).getPool("pooltnfc1"));
            //Chance drop TFC Gold ingots
            event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(ENTITIES_ZOMBIE_PIGMAN).getPool("pooltnfc2"));
        }
        // Fix Magma Cube - TODO Change after AR added
        if ("minecraft:entities/magma_cube".equals(event.getName().toString()))
        {
            tnfcmod.getLog().info("Modifying Magma Cube Table entries");
            remove(event, "minecraft:entities/magma_cube", "main");
            event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(ENTITIES_MAGMA_CUBE).getPool("pooltnfc1"));
        }
        // Fix Wither Skeleton - TODO Change after AR added
        if ("minecraft:entities/wither_skeleton".equals(event.getName().toString()))
        {
            tnfcmod.getLog().info("Modifying Wither Skelly Table entries");
            remove(event, "minecraft:entities/wither_skeleton", "main");
            event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(ENTITIES_WITHER_SKELETON).getPool("pooltnfc1"));
        }
        // Fix Nether Fortress Chest -
        if ("minecraft:chests/nether_bridge".equals(event.getName().toString()))
        {
            tnfcmod.getLog().info("Modifying Nether Chest Table entries");
            remove(event, "minecraft:chests/nether_bridge", "main");
            event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(CHESTS_NETHER_BRIDGE).getPool("pooltnfc1"));
        }

        // Fix Fishing Junk -
        if ("minecraft:gameplay/fishing/junk".equals(event.getName().toString()))
        {
            tnfcmod.getLog().info("Modifying Fishing Junk Table entries");
            remove(event, "minecraft:gameplay/fishing/junk", "main");
            event.getTable().addPool(event.getLootTableManager().getLootTableFromLocation(GAMEPLAY_FISHING_JUNK).getPool("pooltnfc1"));
        }
    }

    private static ResourceLocation register(String id)
    {
        return LootTableList.register(new ResourceLocation(MODID, id));
    }

    private static void remove(LootTableLoadEvent event, String tableName, String pool)
    {
        if (tableName.equals(event.getName().toString()))
        {
            event.getTable().removePool(pool);
        }
    }

    private static void remove(LootTableLoadEvent event, String tableName, String poolName, String entry)
    {
        if (tableName.equals(event.getName().toString()))
        {
            LootPool pool = event.getTable().getPool(poolName);
            //noinspection ConstantConditions
            if (pool != null)
            {
                pool.removeEntry(entry);
            }
        }
    }
}
