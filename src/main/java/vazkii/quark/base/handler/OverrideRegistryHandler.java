/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 20, 2019, 10:35 AM (EST)]
 */
package vazkii.quark.base.handler;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.Quark;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class OverrideRegistryHandler {

	public static void crackFinalField(Field field) throws NoSuchFieldException, IllegalAccessException {
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}

	private static Level revokeLog() {
		Level prior = FMLLog.log.getLevel();
		if (FMLLog.log instanceof Logger)
			((Logger) FMLLog.log).setLevel(Level.OFF);
		return prior;
	}

	private static void restoreLog(Level level) {
		if (FMLLog.log instanceof Logger)
			((Logger) FMLLog.log).setLevel(level);
	}
	
	public static void registerBlock(Block block, String baseName) {
		register(block, Blocks.class, baseName);
	}

	public static void registerItem(Item item, String baseName) {
		register(item, Items.class, baseName);
	}

	public static void registerBiome(Biome item, String baseName) {
		register(item, Biomes.class, baseName);
	}

	public static <T extends IForgeRegistryEntry<T>> void register(T obj, Class<?> registryType, String baseName) {
		Level revoked = revokeLog();
		ResourceLocation regName = new ResourceLocation("minecraft", baseName);
		obj.setRegistryName(regName);
		restoreLog(revoked);

		ProxyRegistry.register(obj);

		for (Field declared : registryType.getDeclaredFields()) {
			if (Modifier.isStatic(declared.getModifiers()) && declared.getType().isAssignableFrom(obj.getClass())) {
				try {
					IForgeRegistryEntry.Impl fieldVal = (IForgeRegistryEntry.Impl) declared.get(null);
					if (regName.equals(fieldVal.getRegistryName())) {
						if (obj instanceof Block && fieldVal instanceof Block) {
							BiMap<Block, Item> itemMap = GameData.getBlockItemMap();
							itemMap.forcePut((Block) obj, itemMap.get(fieldVal));
						} else if (obj instanceof ItemBlock) {
							BiMap<Block, Item> itemMap = GameData.getBlockItemMap();
							itemMap.forcePut(((ItemBlock) obj).getBlock(), (Item) obj);
						}

						crackFinalField(declared);
						declared.set(null, obj);
					}
				} catch (IllegalAccessException | NoSuchFieldException e) {
					Quark.LOG.warn("Was unable to replace registry entry for " + regName + ", may cause issues", e);
				}
			}
		}
	}


}
