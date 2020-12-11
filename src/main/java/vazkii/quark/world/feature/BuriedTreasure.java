/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [30/04/2016, 18:34:27 (GMT)]
 */
package vazkii.quark.world.feature;


import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration.Type;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BuriedTreasure extends Feature {

	public static final String TAG_TREASURE_MAP = "Quark:TreasureMap";
	public static final String TAG_TREASURE_MAP_DELEGATE = "Quark:TreasureMapDelegate";

	public static final ImmutableSet<ResourceLocation> tablesToEdit = ImmutableSet.of(LootTableList.CHESTS_DESERT_PYRAMID, LootTableList.CHESTS_JUNGLE_TEMPLE, LootTableList.CHESTS_STRONGHOLD_CORRIDOR);
	public static final Map<ResourceLocation, String> customPools = new HashMap<>();

	public static int rarity, quality;

	@Override
	public void setupConfig() {
		rarity = loadPropInt("Treasure map Rarity", "", 10);
		quality = loadPropInt("Treasure map item quality", "This is used for the luck attribute in loot tables. It doesn't affect the loot you get from the map itself.", 2);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		LootFunctionManager.registerFunction(new SetAsTreasureFunction.Serializer());
		customPools.put(PirateShips.PIRATE_CHEST_LOOT_TABLE, "quark:pirate_ship");
	}
	
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		ResourceLocation res = event.getName();
		if(tablesToEdit.contains(res)) {
			if(customPools.containsKey(res))
				customPools.get(res);

			event.getTable().getPool("main").addEntry(new LootEntryItem(Items.FILLED_MAP, rarity, quality, new LootFunction[] { new SetAsTreasureFunction() }, new LootCondition[0], "quark:treasure_map"));
		}
	}

	@SubscribeEvent
	public void onUpdate(LivingUpdateEvent event) {
		if(event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(!stack.isEmpty() && stack.hasTagCompound()) {
					if(ItemNBTHelper.getBoolean(stack, TAG_TREASURE_MAP_DELEGATE, false))
						makeMap(stack, player.getEntityWorld(), player.getPosition());
				}
			}
		}
	}

	public ItemStack makeMap(ItemStack itemstack, World world, BlockPos sourcePos) {
		Random r = world.rand;

		BlockPos treasurePos;
		boolean validPos = false;
		int tries = 0;

		do {
			if(tries > 100)
				return null;

			int distance = 400 + r.nextInt(200);
			double angle = r.nextFloat() * (Math.PI * 2);
			int x = (int) (sourcePos.getX() + Math.cos(angle) * distance);
			int z = (int) (sourcePos.getZ() + Math.sin(angle) * distance);
			treasurePos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 255, z)).add(0, -4, 0);
			IBlockState state = world.getBlockState(treasurePos);
			if(state.getBlock() == Blocks.DIRT)
				validPos = true;
			tries++;
		} while(!validPos);

		String s = "map_" + itemstack.getMetadata();
		MapData mapdata = new MapData(s);
		world.setData(s, mapdata);
		mapdata.scale = 1;
		mapdata.calculateMapCenter(treasurePos.getX() + (int) ((Math.random() - 0.5) * 64),
				treasurePos.getZ() + (int) ((Math.random() - 0.5) * 64),
				mapdata.scale);
		mapdata.dimension = 0;
		mapdata.trackingPosition = true;
		mapdata.unlimitedTracking = true;
		ItemMap.renderBiomePreviewMap(world, itemstack);
		MapData.addTargetDecoration(itemstack, treasurePos, "x", Type.TARGET_X);

		mapdata.markDirty();

		world.setBlockState(treasurePos, Blocks.CHEST.getDefaultState());
		TileEntityChest chest = (TileEntityChest) world.getTileEntity(treasurePos);
		if (chest != null)
			chest.setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, r.nextLong());

		ItemNBTHelper.setBoolean(itemstack, TAG_TREASURE_MAP, true);
		ItemNBTHelper.setBoolean(itemstack, TAG_TREASURE_MAP_DELEGATE, false);

		return itemstack;
	}

	public int xy(int x, int y) {
		return x + y * 128;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	public static class SetAsTreasureFunction extends LootFunction {

		protected SetAsTreasureFunction() {
			super(new LootCondition[0]);
		}

		@Nonnull
		@Override
		public ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
			int id = context.getWorld().getUniqueDataId("map");
			stack.setItemDamage(id);
			stack.setTranslatableName("quarkmisc.buried_chest_map");
			NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "display", false);
			cmp.setInteger("MapColor", 0x8C0E0E);
			ItemNBTHelper.setCompound(stack, "display", cmp);
			ItemNBTHelper.setBoolean(stack, TAG_TREASURE_MAP_DELEGATE, true);
			
			return stack;
		}

		public static class Serializer extends LootFunction.Serializer<SetAsTreasureFunction> {

			protected Serializer() {
				super(new ResourceLocation(LibMisc.MOD_ID, "set_treasure"),SetAsTreasureFunction.class);
			}

			@Override
			public void serialize(@Nonnull JsonObject object, @Nonnull SetAsTreasureFunction functionClazz,
								  @Nonnull JsonSerializationContext serializationContext) {}

			@Nonnull
			@Override
			public SetAsTreasureFunction deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext,
													 @Nonnull LootCondition[] conditionsIn) {
				return new SetAsTreasureFunction();
			}
		}
	}

}
