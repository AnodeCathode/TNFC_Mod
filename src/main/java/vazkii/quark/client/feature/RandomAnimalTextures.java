package vazkii.quark.client.feature;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.client.render.random.RenderChickenRandom;
import vazkii.quark.client.render.random.RenderCowRandom;
import vazkii.quark.client.render.random.RenderPigRandom;

import java.util.*;

public class RandomAnimalTextures extends Feature {

	private static ListMultimap<RandomTextureType, ResourceLocation> textures;
	private static Map<RandomTextureType, ResourceLocation> shinyTextures;
	
	private static final int COW_COUNT = 10;
	private static final int PIG_COUNT = 4;
	private static final int CHICKEN_COUNT = 6;
	private static final int CHICK_COUNT = 3;

	public static boolean enableCow, enablePig, enableChicken, enableChick;
	public static int shinyAnimalChance;
	
	@Override
	public void setupConfig() {
		enableCow = loadPropBool("Enable Cow", "", true);
		enablePig = loadPropBool("Enable Pig", "", true);
		enableChicken = loadPropBool("Enable Chicken", "", true);
		enableChick = loadPropBool("Enable Chick", "", true);
		
		shinyAnimalChance = loadPropInt("Shiny Animal Chance", "The chance for an animal to have a special \"Shiny\" skin, like a shiny pokemon. This is 1 in X. Set to 0 to disable.", 1024);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		textures = Multimaps.newListMultimap(new EnumMap<>(RandomTextureType.class), ArrayList::new);
		shinyTextures = new HashMap<>();
		
		registerTextures(RandomTextureType.COW, COW_COUNT, new ResourceLocation("textures/entity/cow/cow.png"));
		registerTextures(RandomTextureType.PIG, PIG_COUNT, new ResourceLocation("textures/entity/pig/pig.png"));
		registerTextures(RandomTextureType.CHICKEN, CHICKEN_COUNT, new ResourceLocation("textures/entity/chicken.png"));
		registerTextures(RandomTextureType.CHICK, CHICK_COUNT, null);

		registerOverride(EntityCow.class, RenderCowRandom.factory(), enableCow);
		registerOverride(EntityPig.class, RenderPigRandom.factory(), enablePig);
		registerOverride(EntityChicken.class, RenderChickenRandom.factory(), enableChicken || enableChick);
	}

	@SideOnly(Side.CLIENT)
	public static ResourceLocation getRandomTexture(Entity e, RandomTextureType type) {
		return getRandomTexture(e, type, true);
	}
	
	@SideOnly(Side.CLIENT)
	public static ResourceLocation getRandomTexture(Entity e, RandomTextureType type, boolean choose) {
		List<ResourceLocation> styles = textures.get(type);
		if(!choose)
			return styles.get(styles.size() - 1);
		
		UUID id = e.getUniqueID();
		long most = id.getMostSignificantBits();
		if(shinyAnimalChance > 0 && (most % shinyAnimalChance) == 0)
			return shinyTextures.get(type);
		
		int choice = Math.abs((int) (most % styles.size()));
		return styles.get(choice);
	}

	@SideOnly(Side.CLIENT)
	private static void registerTextures(RandomTextureType type, int count, ResourceLocation vanilla) {
		String name = type.name().toLowerCase();
		for(int i = 1; i < count + 1; i++)
			textures.put(type, new ResourceLocation(LibMisc.MOD_ID, String.format("textures/entity/random/%s%d.png", name, i)));
		
		if(vanilla != null)
			textures.put(type, vanilla);
		
		shinyTextures.put(type, new ResourceLocation(LibMisc.MOD_ID, String.format("textures/entity/random/%s_shiny.png", name)));
	}

	@SideOnly(Side.CLIENT)
	private static <T extends Entity>void registerOverride(Class<T> clazz, IRenderFactory<? super T> factory, boolean enabled) {
		if(enabled)
			RenderingRegistry.registerEntityRenderingHandler(clazz, factory);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	public enum RandomTextureType {
		COW, PIG, CHICKEN, CHICK
	}
	
}

