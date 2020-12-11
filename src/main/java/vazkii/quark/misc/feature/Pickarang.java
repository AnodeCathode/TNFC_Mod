package vazkii.quark.misc.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.item.ItemPickarang;
import vazkii.quark.world.client.render.RenderPickarang;
import vazkii.quark.world.entity.EntityPickarang;
import vazkii.quark.world.feature.Stonelings;

public class Pickarang extends Feature {
	
	public static int timeout, harvestLevel, durability;
	public static boolean neverUseHeartOfDiamond, noCooldown;
	
	public static Item pickarang;

	public static double maxHardness;

	public static boolean canSharpnessApply(ItemStack stack) {
		return stack.getItem() instanceof ItemPickarang;
	}

	private static final ThreadLocal<EntityPickarang> ACTIVE_PICKARANG = new ThreadLocal<>();

	public static void setActivePickarang(EntityPickarang pickarang) {
		ACTIVE_PICKARANG.set(pickarang);
	}

	public static DamageSource createDamageSource(EntityPlayer player) {
		EntityPickarang pickarang = ACTIVE_PICKARANG.get();

		if (pickarang == null)
			return null;

		return new EntityDamageSourceIndirect("player", pickarang, player).setProjectile();
	}

	@Override
	public void setupConfig() {
		timeout = loadPropInt("Timeout", "How long it takes for the pickarang to return to the player if it doesn't hit anything", 20);
		harvestLevel = loadPropInt("Harvest Level", "2 is Iron, 3 is Diamond", 3);
		durability = loadPropInt("Durability", "Set to -1 to have the Pickarang be unbreakable", 800);
		maxHardness = loadPropDouble("Max mining hardness", "22.5 is ender chests, 25.0 is monster boxes, 50 is obsidian. Most things are below 5.", 20.0);
		neverUseHeartOfDiamond = loadPropBool("Never Use Heart of Diamond", "Set this to true to use the recipe without the Heart of Diamond, even if the Heart of Diamond is enabled", false);
		noCooldown = loadPropBool("No Cooldown", "Set this to true to disable the short cooldown between throwing pickarangs", false);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		pickarang = new ItemPickarang();
		
		String pickarangName = "quark:pickarang";
		EntityRegistry.registerModEntity(new ResourceLocation(pickarangName), EntityPickarang.class, pickarangName, LibEntityIDs.PICKARANG, Quark.instance, 80, 3, true);
	}
	
	@Override
	public void postPreInit() {
		if(Stonelings.diamond_heart != null && !neverUseHeartOfDiamond)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(pickarang), 
					"DWC", "  W", "  D",
					'D', "gemDiamond",
					'W', "plankWood",
					'C', Stonelings.diamond_heart);
		else 
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(pickarang), 
					"DWD", "  W", "  D",
					'D', "gemDiamond",
					'W', "plankWood");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityPickarang.class, RenderPickarang.FACTORY);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
