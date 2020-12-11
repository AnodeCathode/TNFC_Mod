/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [17/07/2016, 03:45:23 (GMT)]
 */
package vazkii.quark.misc.feature;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.client.render.RenderExtraArrow;
import vazkii.quark.misc.entity.EntityArrowEnder;
import vazkii.quark.misc.entity.EntityArrowExplosive;
import vazkii.quark.misc.entity.EntityArrowTorch;
import vazkii.quark.misc.item.ItemModArrow;

import javax.annotation.Nonnull;

public class ExtraArrows extends Feature {

	public static Item arrow_ender;
	public static Item arrow_explosive;
	public static Item arrow_torch;

	public static boolean enableEnder, enableExplosive, enableTorch;
	
	public static double explosiveArrowPower;
	public static boolean explosiveArrowDestroysBlocks;
	
	@Override
	public void setupConfig() {
		enableEnder = loadPropBool("Enable Ender Arrow", "", true);
		enableExplosive = loadPropBool("Enable Explosive Arrow", "", true);
		enableTorch = loadPropBool("Enable Torch Arrow", "", true);
		
		explosiveArrowPower = loadPropDouble("Explosive Arrow Power", "", 2.0);
		explosiveArrowDestroysBlocks = loadPropBool("Explosive Arrow Destroys Blocks", "", true);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(enableEnder) {
			String enderArrowName = "quark:arrow_ender";
			arrow_ender = new ItemModArrow("arrow_ender", (World worldIn, ItemStack stack, EntityLivingBase shooter) -> new EntityArrowEnder(worldIn, shooter));
			EntityRegistry.registerModEntity(new ResourceLocation(enderArrowName), EntityArrowEnder.class, enderArrowName, LibEntityIDs.ARROW_ENDER, Quark.instance, 64, 10, true);
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(arrow_ender, new ArrowBehaviour(EntityArrowEnder::new));
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(arrow_ender), ProxyRegistry.newStack(Items.ARROW), ProxyRegistry.newStack(Items.ENDER_PEARL));
		}
		
		if(enableExplosive) {
			String explosiveArrowName = "quark:arrow_explosive";
			arrow_explosive = new ItemModArrow("arrow_explosive", (World worldIn, ItemStack stack, EntityLivingBase shooter) -> new EntityArrowExplosive(worldIn, shooter));
			EntityRegistry.registerModEntity(new ResourceLocation(explosiveArrowName), EntityArrowExplosive.class, explosiveArrowName, LibEntityIDs.ARROW_EXPLOSIVE, Quark.instance, 64, 10, true);
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(arrow_explosive, new ArrowBehaviour(EntityArrowExplosive::new));
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(arrow_explosive), ProxyRegistry.newStack(Items.ARROW), ProxyRegistry.newStack(Items.GUNPOWDER), ProxyRegistry.newStack(Items.GUNPOWDER));
		}
		
		if(enableTorch) {
			String torchArrowName = "quark:arrow_torch";
			arrow_torch = new ItemModArrow("arrow_torch", (World worldIn, ItemStack stack, EntityLivingBase shooter) -> new EntityArrowTorch(worldIn, shooter));
			EntityRegistry.registerModEntity(new ResourceLocation(torchArrowName), EntityArrowTorch.class, torchArrowName, LibEntityIDs.ARROW_TORCH, Quark.instance, 64, 10, true);
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(arrow_torch, new ArrowBehaviour(EntityArrowTorch::new));
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(arrow_torch), ProxyRegistry.newStack(Items.ARROW), ProxyRegistry.newStack(Blocks.TORCH));
		}
	}
	
	@Override
	public void preInitClient() {
		if(enableEnder)
			RenderingRegistry.registerEntityRenderingHandler(EntityArrowEnder.class, RenderExtraArrow.FACTORY_ENDER);
		
		if(enableExplosive)
			RenderingRegistry.registerEntityRenderingHandler(EntityArrowExplosive.class, RenderExtraArrow.FACTORY_EXPLOSIVE);
		
		if(enableTorch)
			RenderingRegistry.registerEntityRenderingHandler(EntityArrowTorch.class, RenderExtraArrow.FACTORY_TORCH);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	public static class ArrowBehaviour extends BehaviorProjectileDispense {

		private final ArrowProvider provider;
		
		public ArrowBehaviour(ArrowProvider provider) {
			this.provider = provider;
		}
		
		@Nonnull
		@Override
		protected IProjectile getProjectileEntity(@Nonnull World worldIn, @Nonnull IPosition position, @Nonnull ItemStack stackIn) {
			EntityArrow arrow = provider.provide(worldIn, position);
			arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
			return arrow;
		}
		
		public interface ArrowProvider {
			EntityArrow provide(World world, IPosition pos);
		}

	}

}

