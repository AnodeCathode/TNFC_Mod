/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:31:04 (GMT)]
 */
package vazkii.quark.base.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ClassTransformer implements IClassTransformer, Opcodes {

	private static final String ASM_HOOKS = "vazkii/quark/base/asm/ASMHooks";

	private static final Map<String, Transformer> transformers = new HashMap<>();

	static {
		// For Emotes
		transformers.put("net.minecraft.client.model.ModelBiped", ClassTransformer::transformModelBiped);

		// For Color Runes
		transformers.put("net.minecraft.client.renderer.RenderItem", ClassTransformer::transformRenderItem);
		transformers.put("net.minecraft.client.renderer.entity.layers.LayerArmorBase", ClassTransformer::transformLayerArmorBase);

		// For Boat Sails and Chain Linkage
		transformers.put("net.minecraft.client.renderer.entity.RenderBoat", ClassTransformer::transformRenderBoat);
		transformers.put("net.minecraft.entity.item.EntityBoat", ClassTransformer::transformEntityBoat);
		transformers.put("net.minecraft.entity.item.EntityMinecart", ClassTransformer::transformEntityMinecart);
		transformers.put("net.minecraft.client.renderer.entity.RenderManager", ClassTransformer::transformRenderManager);

		// For Piston Block Breakers and Pistons Move TEs
		transformers.put("net.minecraft.block.BlockPistonBase", ClassTransformer::transformBlockPistonBase);

		// For Better Craft Shifting
		transformers.put("net.minecraft.inventory.ContainerWorkbench", ClassTransformer::transformContainerWorkbench);
		transformers.put("net.minecraft.inventory.ContainerMerchant", ClassTransformer::transformContainerMerchant);

		// For Pistons Move TEs
		transformers.put("net.minecraft.tileentity.TileEntityPiston", ClassTransformer::transformTileEntityPiston);
		transformers.put("net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer", ClassTransformer::transformTileEntityPistonRenderer);

		// For Improved Sleeping
		transformers.put("net.minecraft.world.WorldServer", ClassTransformer::transformWorldServer);

		// For Colored Lights
		transformers.put("net.minecraft.client.renderer.BlockModelRenderer", ClassTransformer::transformBlockModelRenderer);

		// For More Banner Layers
		transformers.put("net.minecraft.item.crafting.RecipesBanners$RecipeAddPattern", ClassTransformer::transformRecipeAddPattern);
		transformers.put("net.minecraft.item.ItemBanner", ClassTransformer::transformItemBanner);

		// Better Fire Effect
		transformers.put("net.minecraft.client.renderer.entity.Render", ClassTransformer::transformRender);

		// For Witch Hats
		transformers.put("net.minecraft.entity.ai.EntityAITarget", ClassTransformer::transformEntityAITarget);

		// For Show Invalid Slots
		transformers.put("net.minecraft.client.gui.inventory.GuiContainer", ClassTransformer::transformGuiContainer);

		// For Springy Slime & Boat Sails and Chain Linkage
		transformers.put("net.minecraft.entity.Entity", ClassTransformer::transformEntity);

		// For Items Flash Before Expiring
		transformers.put("net.minecraft.entity.item.EntityItem", ClassTransformer::transformEntityItem);

		// For Extra Potions
		transformers.put("net.minecraft.client.gui.inventory.GuiBeacon$PowerButton", ClassTransformer::transformBeaconButton);

		// For Better Nausea
		transformers.put("net.minecraft.client.renderer.EntityRenderer", ClassTransformer::transformEntityRenderer);

		// For Pickarangs
		transformers.put("net.minecraft.enchantment.EnchantmentDamage", ClassTransformer::transformSharpness);
		transformers.put("net.minecraft.util.DamageSource", ClassTransformer::transformDamageSource);

		// For Render Items In Chat
		transformers.put("net.minecraft.item.ItemStack", ClassTransformer::transformItemStack);
		transformers.put("net.minecraftforge.client.model.pipeline.LightUtil", ClassTransformer::transformLightUtil);

		// For Hoe Sickles
		transformers.put("net.minecraft.enchantment.Enchantment", ClassTransformer::transformEnchantment);

		// For Iron Ladders
		transformers.put("net.minecraft.block.BlockDynamicLiquid", ClassTransformer::transformDynamicLiquid);

		// For Ancient Tomes
		transformers.put("net.minecraft.enchantment.EnchantmentHelper", ClassTransformer::transformEnchantmentHelper);

		// For Parrot Eggs
		transformers.put("net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder", ClassTransformer::transformLayerEntityOnShoulder);
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformers.containsKey(transformedName)) {
			log("Transforming " + transformedName);
			return transformers.get(transformedName).apply(basicClass);
		}

		return basicClass;
	}

	private static byte[] transformModelBiped(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("setRotationAngles", "func_78087_a", "(FFFFFFLnet/minecraft/entity/Entity;)V");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == RETURN;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 7));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "updateEmotes", "(Lnet/minecraft/entity/Entity;)V", false));

					method.instructions.insertBefore(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformRenderItem(byte[] basicClass) {
		MethodSignature sig1 = new MethodSignature("renderItem", "func_180454_a", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");
		MethodSignature sig2 = new MethodSignature("renderEffect", "func_191966_a", "(Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");

		byte[] transClass = basicClass;

		transClass = transform(transClass, forMethod(sig1,
				(MethodNode method) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 1));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "setColorRuneTargetStack", "(Lnet/minecraft/item/ItemStack;)V", false));

					method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
					return true;
				}), forMethod(sig2, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == LDC && ((LdcInsnNode) node).cst.equals(-8372020);
				}, (MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "getRuneColor", "(I)I", false));

					method.instructions.insert(node, newInstructions);
					return false;
				})));

		return transClass;
	}

	private static byte[] transformLayerArmorBase(byte[] basicClass) {
		MethodSignature sig1 = new MethodSignature("renderArmorLayer", "func_188361_a", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V");
		MethodSignature sig2 = new MethodSignature("renderEnchantedGlint", "func_188364_a", "(Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/model/ModelBase;FFFFFFF)V");

		MethodSignature target = new MethodSignature("color", "func_179131_c", "(FFFF)V");

		byte[] transClass = basicClass;

		transClass = transform(transClass, forMethod(sig1, (MethodNode method) -> { // Action
			InsnList newInstructions = new InsnList();

			newInstructions.add(new VarInsnNode(ALOAD, 1));
			newInstructions.add(new VarInsnNode(ALOAD, 9));
			newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "setColorRuneTargetStack", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/inventory/EntityEquipmentSlot;)V", false));

			method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
			return true;
		}));

		if (!hasOptifine(sig2.toString())) {
			transClass = transform(transClass, forMethod(sig2, combine(
					(AbstractInsnNode node) -> { // Filter
						return node.getOpcode() == INVOKESTATIC && target.matches((MethodInsnNode) node);
					},
					(MethodNode method, AbstractInsnNode node) -> { // Action

						InsnList newInstructions = new InsnList();

						newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "applyRuneColor", "()V", false));

						method.instructions.insert(node, newInstructions);
						return false;
					})));
		}

		return transClass;
	}

	private static byte[] transformEntityBoat(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("attackEntityFrom", "func_70097_a", "(Lnet/minecraft/util/DamageSource;F)Z");

		MethodSignature target = new MethodSignature("dropItemWithOffset", "func_145778_a", "(Lnet/minecraft/item/Item;IF)Lnet/minecraft/entity/item/EntityItem;");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == INVOKEVIRTUAL && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 0));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "boatDrops", "(Lnet/minecraft/entity/item/EntityBoat;)V", false));

					method.instructions.insert(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformEntityMinecart(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("killMinecart", "func_94095_a", "(Lnet/minecraft/util/DamageSource;)V");

		MethodSignature target = new MethodSignature("entityDropItem", "func_70099_a", "(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/item/EntityItem;");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == INVOKEVIRTUAL && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 0));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "minecartDrops", "(Lnet/minecraft/entity/item/EntityMinecart;)V", false));

					method.instructions.insert(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformRenderManager(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("renderEntity", "func_188391_a", "(Lnet/minecraft/entity/Entity;DDDFFZ)V");

		MethodSignature target = new MethodSignature("doRender", "func_76986_a", "(Lnet/minecraft/entity/Entity;DDDFF)V");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> node.getOpcode() == INVOKEVIRTUAL && target.matches((MethodInsnNode) node),
				(MethodNode method, AbstractInsnNode node) -> {
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 11));
					newInstructions.add(new VarInsnNode(DLOAD, 2));
					newInstructions.add(new VarInsnNode(DLOAD, 4));
					newInstructions.add(new VarInsnNode(DLOAD, 6));
					newInstructions.add(new VarInsnNode(ALOAD, 1));
					newInstructions.add(new VarInsnNode(FLOAD, 9));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "renderChain", "(Lnet/minecraft/client/renderer/entity/Render;DDDLnet/minecraft/entity/Entity;F)V", false));

					method.instructions.insert(node, newInstructions);

					return true;
				})));
	}

	private static byte[] transformRenderBoat(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("doRender", "func_188300_b", "(Lnet/minecraft/entity/item/EntityBoat;DDDFF)V");

		MethodSignature targetA = new MethodSignature("render", "func_78088_a", "(Lnet/minecraft/entity/Entity;FFFFFF)V");
		MethodSignature targetB = new MethodSignature("renderMultipass", "func_187054_b", "(Lnet/minecraft/entity/Entity;FFFFFF)V");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return (node.getOpcode() == INVOKEVIRTUAL || node.getOpcode() == INVOKEINTERFACE) &&
							(targetA.matches((MethodInsnNode) node) || targetB.matches((MethodInsnNode) node));
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 1));
					newInstructions.add(new VarInsnNode(FLOAD, 9));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "renderBannerOnBoat", "(Lnet/minecraft/entity/item/EntityBoat;F)V", false));

					method.instructions.insert(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformBlockPistonBase(byte[] basicClass) {
		MethodSignature sig1 = new MethodSignature("canPush", "func_185646_a", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;ZLnet/minecraft/util/EnumFacing;)Z");
		MethodSignature sig2 = new MethodSignature("doMove", "func_176319_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Z");
		MethodSignature sig3 = new MethodSignature("checkForMove", "func_176316_e", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V");

		String targetClazz = "net/minecraft/block/state/BlockPistonStructureHelper";

		MethodSignature target = new MethodSignature("hasTileEntity", "", "(Lnet/minecraft/block/state/IBlockState;)Z");
		MethodSignature target2 = new MethodSignature("getBlocksToMove", "func_177254_c", "()Ljava/util/List;");
		MethodSignature target3 = new MethodSignature("<init>", "", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)V");

		return transform(basicClass,
				forMethod(sig1, combine(
						(AbstractInsnNode node) -> { // Filter
							return node.getOpcode() == INVOKEVIRTUAL && target.matches((MethodInsnNode) node);
						},
						(MethodNode method, AbstractInsnNode node) -> { // Action
							InsnList newInstructions = new InsnList();

							newInstructions.add(new VarInsnNode(ALOAD, 0));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "shouldPistonMoveTE", "(ZLnet/minecraft/block/state/IBlockState;)Z", false));

							method.instructions.insert(node, newInstructions);
							return true;
						})),
				forMethod(sig2, combine(
						(AbstractInsnNode node) -> { // Filter
							return node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).owner.equals(targetClazz) && target2.matches((MethodInsnNode) node);
						},
						(MethodNode method, AbstractInsnNode node) -> { // Action
							InsnList newInstructions = new InsnList();

							newInstructions.add(new VarInsnNode(ALOAD, 5));
							newInstructions.add(new VarInsnNode(ALOAD, 1));
							newInstructions.add(new VarInsnNode(ALOAD, 3));
							newInstructions.add(new VarInsnNode(ILOAD, 4));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "postPistonPush", "(Lnet/minecraft/block/state/BlockPistonStructureHelper;Lnet/minecraft/world/World;Lnet/minecraft/util/EnumFacing;Z)V", false));

							method.instructions.insertBefore(node, newInstructions);
							return true;
						})),
				forMethod(sig2, combine(
						(AbstractInsnNode node) -> { // Filter
							return node.getOpcode() == INVOKESPECIAL && ((MethodInsnNode) node).owner.equals(targetClazz) && target3.matches((MethodInsnNode) node);
						},
						(MethodNode method, AbstractInsnNode node) -> { // Action
							InsnList newInstructions = new InsnList();

							newInstructions.add(new VarInsnNode(ALOAD, 1));
							newInstructions.add(new VarInsnNode(ALOAD, 2));
							newInstructions.add(new VarInsnNode(ALOAD, 3));
							newInstructions.add(new VarInsnNode(ILOAD, 4));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "transformStructureHelper", "(Lnet/minecraft/block/state/BlockPistonStructureHelper;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Lnet/minecraft/block/state/BlockPistonStructureHelper;", false));

							method.instructions.insert(node, newInstructions);
							return true;
						})),
				forMethod(sig3, combine(
						(AbstractInsnNode node) -> { // Filter
							return node.getOpcode() == INVOKESPECIAL && ((MethodInsnNode) node).owner.equals(targetClazz) && target3.matches((MethodInsnNode) node);
						},
						(MethodNode method, AbstractInsnNode node) -> { // Action
							InsnList newInstructions = new InsnList();

							newInstructions.add(new VarInsnNode(ALOAD, 1));
							newInstructions.add(new VarInsnNode(ALOAD, 2));
							newInstructions.add(new VarInsnNode(ALOAD, 4));
							newInstructions.add(new InsnNode(ICONST_1));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "transformStructureHelper", "(Lnet/minecraft/block/state/BlockPistonStructureHelper;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Lnet/minecraft/block/state/BlockPistonStructureHelper;", false));

							method.instructions.insert(node, newInstructions);
							return true;
						})));
	}

	private static byte[] transformContainerWorkbench(byte[] basicClass) {
		return transformTransferStackInSlot(basicClass, "getMinInventoryBoundaryCrafting", "getMaxInventoryBoundaryCrafting");
	}

	private static byte[] transformContainerMerchant(byte[] basicClass) {
		return transformTransferStackInSlot(basicClass, "getMinInventoryBoundaryVillager", "getMaxInventoryBoundaryVillager");
	}

	private static byte[] transformTransferStackInSlot(byte[] basicClass, String firstHook, String secondHook) {
		MethodSignature sig = new MethodSignature("transferStackInSlot", "func_82846_b", "(Lnet/minecraft/entity/player/EntityPlayer;I)Lnet/minecraft/item/ItemStack;");

		MethodSignature target = new MethodSignature("mergeItemStack", "func_75135_a", "(Lnet/minecraft/item/ItemStack;IIZ)Z");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == INVOKEVIRTUAL && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					// Stack is at
					// IiZ
					// We need to modify I, i, while preserving Z
					// We also need both I as input for the methods
					// 1 will refer to the output of the first hook, and 2 to the second hook.
					// Our stack needs to end as 12Z

					// Stack state: IiZ
					newInstructions.add(new InsnNode(DUP_X2));
					newInstructions.add(new InsnNode(POP));
					// Stack state: ZIi
					newInstructions.add(new InsnNode(DUP2));
					// Stack state: ZIiIi
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, firstHook, "(II)I", false));
					// Stack state: ZIi1
					newInstructions.add(new InsnNode(DUP_X2));
					newInstructions.add(new InsnNode(POP));
					// Stack state: Z1Ii
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, secondHook, "(II)I", false));
					// Stack state: Z12
					newInstructions.add(new InsnNode(DUP_X2));
					newInstructions.add(new InsnNode(POP));
					// Stack state: 2Z1
					newInstructions.add(new InsnNode(DUP_X2));
					newInstructions.add(new InsnNode(POP));
					// Stack state: 12Z

					method.instructions.insertBefore(node, newInstructions);
					return false;
				})));
	}

	private static byte[] transformTileEntityPiston(byte[] basicClass) {
		MethodSignature clearPistonTileEntitySig = new MethodSignature("clearPistonTileEntity", "func_145866_f", "()V");
		MethodSignature updateSig = new MethodSignature("update", "func_73660_a", "()V");

		MethodSignature target = new MethodSignature("setBlockState", "func_180501_a", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z");

		MethodAction setPistonBlockAction = combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == INVOKEVIRTUAL && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "setPistonBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", false));

					method.instructions.insert(node, newInstructions);
					method.instructions.remove(node);

					return true;
				});

		MethodAction onUpdateAction = (MethodNode method) -> { // Action
			InsnList newInstructions = new InsnList();

			newInstructions.add(new VarInsnNode(ALOAD, 0));
			newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "onPistonUpdate", "(Lnet/minecraft/tileentity/TileEntityPiston;)V", false));

			method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);

			return true;
		};

		byte[] transClass = basicClass;
		transClass = transform(transClass, forMethod(updateSig, onUpdateAction));
		transClass = transform(transClass, forMethod(clearPistonTileEntitySig, setPistonBlockAction));
		transClass = transform(transClass, forMethod(updateSig, setPistonBlockAction));

		return transClass;
	}

	private static byte[] transformTileEntityPistonRenderer(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("render", "func_192841_a", "(Lnet/minecraft/tileentity/TileEntityPiston;DDDFIF)V");

		MethodSignature target = new MethodSignature("draw", "func_78381_a", "()V");

		return transform(basicClass, forMethod(sig, (MethodNode method) -> {
					InsnList newInstructions = new InsnList();

					LabelNode escape = new LabelNode();

					newInstructions.add(new VarInsnNode(ALOAD, 1));
					newInstructions.add(new VarInsnNode(DLOAD, 2));
					newInstructions.add(new VarInsnNode(DLOAD, 4));
					newInstructions.add(new VarInsnNode(DLOAD, 6));
					newInstructions.add(new VarInsnNode(FLOAD, 8));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "renderPistonBlock", "(Lnet/minecraft/tileentity/TileEntityPiston;DDDF)Z", false));
					newInstructions.add(new JumpInsnNode(IFEQ, escape));
					newInstructions.add(new InsnNode(RETURN));
					newInstructions.add(escape);

					method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
					return true;
				}));
	}

	private static byte[] transformWorldServer(byte[] basicClass) {
		MethodSignature sig1 = new MethodSignature("areAllPlayersAsleep", "func_73056_e", "()Z");
		MethodSignature sig2 = new MethodSignature("wakeAllPlayers", "func_73053_d", "()V");

		return transform(basicClass, forMethod(sig1, (MethodNode method) -> { // Action
			InsnList newInstructions = new InsnList();

			newInstructions.add(new VarInsnNode(ALOAD, 0));
			newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "isEveryoneAsleep", "(Lnet/minecraft/world/World;)I", false));
			newInstructions.add(new InsnNode(DUP));
			LabelNode label = new LabelNode();
			newInstructions.add(new JumpInsnNode(IFEQ, label));
			newInstructions.add(new InsnNode(ICONST_1));
			newInstructions.add(new InsnNode(ISUB));
			newInstructions.add(new InsnNode(IRETURN));
			newInstructions.add(label);
			newInstructions.add(new InsnNode(POP));

			method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
			return true;
		}), forMethod(sig2, (MethodNode method) -> { // Action
			InsnList newInstructions = new InsnList();

			newInstructions.add(new VarInsnNode(ALOAD, 0));
			newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "whenPlayersWake", "(Lnet/minecraft/world/WorldServer;)V", false));

			method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
			return true;
		}));
	}

	private static byte[] transformBlockModelRenderer(byte[] basicClass) {
		MethodSignature sig1 = new MethodSignature("renderQuadsFlat", "func_187496_a", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;IZLnet/minecraft/client/renderer/BufferBuilder;Ljava/util/List;Ljava/util/BitSet;)V");

		MethodSignature target = new MethodSignature("putPosition", "func_178987_a", "(DDD)V");

		if (hasOptifine(sig1.toString()))
			return basicClass;

		return transform(basicClass, forMethod(sig1, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == INVOKEVIRTUAL && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 1));
					newInstructions.add(new VarInsnNode(ALOAD, 2));
					newInstructions.add(new VarInsnNode(ALOAD, 3));
					newInstructions.add(new VarInsnNode(ALOAD, 6));
					newInstructions.add(new VarInsnNode(ALOAD, 18));
					newInstructions.add(new VarInsnNode(ILOAD, 4));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "putColorsFlat", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/client/renderer/block/model/BakedQuad;I)V", false));

					method.instructions.insertBefore(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformRecipeAddPattern(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("matches", "func_77569_a", "(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/world/World;)Z");

		MethodSignature target = new MethodSignature("getPatterns", "func_175113_c", "(Lnet/minecraft/item/ItemStack;)I");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == INVOKESTATIC && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "shiftLayerCountDown", "(I)I", false));

					method.instructions.insert(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformItemBanner(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("appendHoverTextFromTileEntityTag", "func_185054_a", "(Lnet/minecraft/item/ItemStack;Ljava/util/List;)V");
		return transform(basicClass, forMethod(sig, combine((AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == BIPUSH && ((IntInsnNode) node).operand == 6;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "shiftLayerCountUp", "(I)I", false));

					method.instructions.insert(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformRender(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("renderEntityOnFire", "func_76977_a", "(Lnet/minecraft/entity/Entity;DDDF)V");

		return transform(basicClass, forMethod(sig, (MethodNode method) -> { // Action
			InsnList newInstructions = new InsnList();

			newInstructions.add(new VarInsnNode(ALOAD, 1));
			newInstructions.add(new VarInsnNode(DLOAD, 2));
			newInstructions.add(new VarInsnNode(DLOAD, 4));
			newInstructions.add(new VarInsnNode(DLOAD, 6));
			newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "renderFire", "(Lnet/minecraft/entity/Entity;DDD)Z", false));
			LabelNode label = new LabelNode();
			newInstructions.add(new JumpInsnNode(IFEQ, label));
			newInstructions.add(new InsnNode(RETURN));
			newInstructions.add(label);

			method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
			return true;
		}));
	}

	private static byte[] transformEntityAITarget(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("isSuitableTarget", "func_179445_a", "(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/entity/EntityLivingBase;ZZ)Z");

		return transform(basicClass, forMethod(sig, (MethodNode method) -> { // Action
			InsnList newInstructions = new InsnList();
			newInstructions.add(new VarInsnNode(ALOAD, 0));
			newInstructions.add(new VarInsnNode(ALOAD, 1));
			newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "hasWitchHat", "(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/entity/EntityLivingBase;)Z", false));
			LabelNode label = new LabelNode();
			newInstructions.add(new JumpInsnNode(IFEQ, label));
			newInstructions.add(new InsnNode(ICONST_0));
			newInstructions.add(new InsnNode(IRETURN));
			newInstructions.add(label);

			method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
			return true;
		}));
	}

	private static byte[] transformGuiContainer(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("drawScreen", "func_73863_a", "(IIF)V");
		MethodSignature target = new MethodSignature("drawGuiContainerForegroundLayer", "func_146979_b", "(II)V");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return (node.getOpcode() == INVOKEVIRTUAL || node.getOpcode() == INVOKESPECIAL) && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 0));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "drawInvalidSlotOverlays", "(Lnet/minecraft/client/gui/inventory/GuiContainer;)V", false));

					method.instructions.insertBefore(node, newInstructions);
					return false;
				})));
	}

	private static byte[] transformEntity(byte[] basicClass) {
		MethodSignature sig1 = new MethodSignature("move", "func_70091_d", "(Lnet/minecraft/entity/MoverType;DDD)V");
		MethodSignature sig2 = new MethodSignature("onEntityUpdate", "func_70030_z", "()V");
		MethodSignature target = new MethodSignature("doBlockCollisions", "func_145775_I", "()V");

		return transform(basicClass, forMethod(sig1,
				(MethodNode method) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 0));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "recordMotion", "(Lnet/minecraft/entity/Entity;)V", false));

					method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
					return true;
				}
		, combine(
				(AbstractInsnNode node) -> { // Filter
					return (node.getOpcode() == INVOKEVIRTUAL || node.getOpcode() == INVOKESPECIAL) && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					int d2 = 14;
					int d3 = 16;
					int d4 = 18;
					for (LocalVariableNode lvn : method.localVariables) {
						switch (lvn.name) {
							case "d2":
								d2 = lvn.index;
								break;
							case "d3":
								d3 = lvn.index;
								break;
							case "d4":
								d4 = lvn.index;
								break;
						}
					}


					newInstructions.add(new VarInsnNode(ALOAD, 0));
					newInstructions.add(new VarInsnNode(DLOAD, d2));
					newInstructions.add(new VarInsnNode(DLOAD, d3));
					newInstructions.add(new VarInsnNode(DLOAD, d4));
					newInstructions.add(new VarInsnNode(DLOAD, 2));
					newInstructions.add(new VarInsnNode(DLOAD, 4));
					newInstructions.add(new VarInsnNode(DLOAD, 6));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "applyCollisionLogic", "(Lnet/minecraft/entity/Entity;DDDDDD)V", false));

					method.instructions.insert(node, newInstructions);
					return false;
				}
		)), forMethod(sig2, (MethodNode method) -> {
			InsnList newInstructions = new InsnList();

			newInstructions.add(new VarInsnNode(ALOAD, 0));
			newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "onEntityUpdate", "(Lnet/minecraft/entity/Entity;)V", false));

			method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
			return true;
		}));
	}

	private static byte[] transformEntityItem(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("onUpdate", "func_70071_h_", "()V");

		return transform(basicClass, forMethod(sig, (MethodNode method) -> { // Action
			InsnList newInstructions = new InsnList();

			newInstructions.add(new VarInsnNode(ALOAD, 0));
			newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "ensureUpdatedItemAge", "(Lnet/minecraft/entity/item/EntityItem;)V", false));

			method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
			return true;
		}));
	}

	private static byte[] transformBeaconButton(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("drawButton", "func_191745_a", "(Lnet/minecraft/client/Minecraft;IIF)V");
		MethodSignature checkAgainst = new MethodSignature("", "func_146111_b", "(II)V");


		return transform(basicClass, inject(sig, (MethodNode method) -> {
					boolean deobf = method.name.equals(sig.srgName);

					InsnList newInstructions = new InsnList();

					LabelNode skipSuper = new LabelNode();

					newInstructions.add(new VarInsnNode(ALOAD, 0));

					newInstructions.add(new InsnNode(DUP));
					newInstructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/inventory/GuiBeacon$PowerButton", deobf ? "this$0" : "field_146150_o", "Lnet/minecraft/client/gui/inventory/GuiBeacon;"));
					newInstructions.add(new VarInsnNode(ALOAD, 1));
					newInstructions.add(new VarInsnNode(ILOAD, 2));
					newInstructions.add(new VarInsnNode(ILOAD, 3));
					newInstructions.add(new VarInsnNode(FLOAD, 4));

					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "renderBeaconButton", "(Lnet/minecraft/client/gui/GuiButton;Lnet/minecraft/client/gui/inventory/GuiBeacon;Lnet/minecraft/client/Minecraft;IIF)Z", false));
					newInstructions.add(new JumpInsnNode(IFNE, skipSuper));
					newInstructions.add(new InsnNode(RETURN));
					newInstructions.add(skipSuper);

					method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
					return true;
				},
				(ClassNode clazz, MethodVisitor method) -> {
					boolean deobf = true;
					for (MethodNode node : clazz.methods) {
						if (checkAgainst.matches(node)) {
							deobf = false;
							break;
						}
					}

					InsnList instructions = new InsnList();

					Label begin = new Label();
					Label end = new Label();
					Label skipSuper = new Label();

					method.visitParameter("this", ACC_MANDATED);
					method.visitParameter("mc", ACC_MANDATED);
					method.visitParameter("mouseX", ACC_MANDATED);
					method.visitParameter("mouseY", ACC_MANDATED);
					method.visitParameter("partialTicks", ACC_MANDATED);

					method.visitCode();
					method.visitLabel(begin);
					method.visitVarInsn(ALOAD, 0);
					method.visitInsn(DUP);
					method.visitFieldInsn(GETFIELD, "net/minecraft/client/gui/inventory/GuiBeacon$PowerButton",
							deobf ? "this$0" : "field_146150_o",
							"Lnet/minecraft/client/gui/inventory/GuiBeacon;");
					method.visitVarInsn(ALOAD, 1);
					method.visitVarInsn(ILOAD, 2);
					method.visitVarInsn(ILOAD, 3);
					method.visitVarInsn(FLOAD, 4);
					method.visitMethodInsn(INVOKESTATIC, ASM_HOOKS, "renderBeaconButton",
							"(Lnet/minecraft/client/gui/GuiButton;Lnet/minecraft/client/gui/inventory/GuiBeacon;Lnet/minecraft/client/Minecraft;IIF)Z", false);

					method.visitJumpInsn(IFNE, skipSuper);
					method.visitVarInsn(ALOAD, 0);
					method.visitVarInsn(ALOAD, 1);
					method.visitVarInsn(ILOAD, 2);
					method.visitVarInsn(ILOAD, 3);
					method.visitVarInsn(FLOAD, 4);
					method.visitMethodInsn(INVOKESPECIAL, "net/minecraft/client/gui/inventory/GuiBeacon$Button",
							deobf ? sig.funcName : sig.srgName, sig.funcDesc, false);
					method.visitLabel(skipSuper);
					method.visitInsn(RETURN);
					method.visitLabel(end);

					method.visitLocalVariable("this", "Lnet/minecraft/client/gui/inventory/GuiBeacon$PowerButton;", null, begin, end, 0);
					method.visitLocalVariable("mc", "Lnet/minecraft/client/Minecraft;", null, begin, end, 1);
					method.visitLocalVariable("mouseX", "I", null, begin, end, 2);
					method.visitLocalVariable("mouseY", "I", null, begin, end, 3);
					method.visitLocalVariable("partialTicks", "F", null, begin, end, 4);

					method.visitMaxs(5, 5);

					method.visitEnd();

					return true;
				}));
	}

	private static byte[] transformEntityRenderer(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("renderWorldPass", "func_175068_a", "(IFJ)V");
		MethodSignature target = new MethodSignature("setupCameraTransform", "func_78479_a", "(FI)V");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return (node.getOpcode() == INVOKEVIRTUAL || node.getOpcode() == INVOKESPECIAL) && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 0));
					newInstructions.add(new VarInsnNode(FLOAD, 2));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "renderNausea", "(Lnet/minecraft/client/renderer/EntityRenderer;F)V", false));

					method.instructions.insert(node, newInstructions);
					return false;
				}
		)));
	}

	private static byte[] transformSharpness(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("canApply", "func_92089_a", "(Lnet/minecraft/item/ItemStack;)Z");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> node.getOpcode() == IRETURN,
				(MethodNode method, AbstractInsnNode node) -> {
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 1));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "canSharpnessApply", "(Lnet/minecraft/item/ItemStack;)Z", false));
					newInstructions.add(new InsnNode(IOR));

					method.instructions.insertBefore(node, newInstructions);
					return false;
				}
		)));
	}
	private static byte[] transformEnchantment(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("canApply", "func_92089_a", "(Lnet/minecraft/item/ItemStack;)Z");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> node.getOpcode() == IRETURN,
				(MethodNode method, AbstractInsnNode node) -> {
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 0));
					newInstructions.add(new VarInsnNode(ALOAD, 1));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "canFortuneApply", "(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)Z", false));
					newInstructions.add(new InsnNode(IOR));

					method.instructions.insertBefore(node, newInstructions);
					return false;
				}
		)));
	}

	private static byte[] transformItemStack(byte[] basicClass) {
		MethodSignature sig1 = new MethodSignature("getTextComponent", "func_151000_E", "()Lnet/minecraft/util/text/ITextComponent;");

		return transform(basicClass, forMethod(sig1, combine(
				(AbstractInsnNode node) -> node.getOpcode() == ARETURN,
				(MethodNode method, AbstractInsnNode node) -> {
					InsnList newInstructions = new InsnList();

					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "createStackComponent", "(Lnet/minecraft/util/text/ITextComponent;)Lnet/minecraft/util/text/ITextComponent;", false));

					method.instructions.insertBefore(node, newInstructions);
					return false;
				}
		)));
	}

	private static byte[] transformLightUtil(byte[] basicClass) {
		MethodSignature sig1 = new MethodSignature("renderQuadColor", "", "(Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/client/renderer/block/model/BakedQuad;I)V");

		return transform(basicClass, forMethod(sig1,
				(MethodNode method) -> {
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ILOAD, 2));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "transformQuadRenderColor", "(I)I", false));
					newInstructions.add(new VarInsnNode(ISTORE, 2));

					method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
					return true;
				}
		));
	}

	private static byte[] transformDynamicLiquid(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("isBlocked", "func_176372_g", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> node.getOpcode() == IRETURN,
				(MethodNode method, AbstractInsnNode node) -> {
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 4));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "isBlockNotBrokenByWater", "(Lnet/minecraft/block/Block;)Z", false));
					newInstructions.add(new InsnNode(IOR));

					method.instructions.insertBefore(node, newInstructions);
					return false;
				}
		)));
	}

	private static byte[] transformEnchantmentHelper(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("getEnchantments", "func_82781_a", "(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;");

		MethodSignature target = new MethodSignature("getEnchantmentTagList", "func_77986_q", "()Lnet/minecraft/nbt/NBTTagList;");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return (node.getOpcode() == INVOKEVIRTUAL || node.getOpcode() == INVOKESPECIAL) && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 0));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "getEnchantmentsForStack", "(Lnet/minecraft/nbt/NBTTagList;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NBTTagList;", false));

					method.instructions.insert(node, newInstructions);
					return false;
				}
		)));
	}

	private static byte[] transformLayerEntityOnShoulder(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("renderEntityOnShoulder", "func_192864_a", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/UUID;Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/client/model/ModelBase;Lnet/minecraft/util/ResourceLocation;Ljava/lang/Class;FFFFFFFZ)Lnet/minecraft/client/renderer/entity/layers/LayerEntityOnShoulder$DataHolder;");

		MethodSignature target = new MethodSignature("bindTexture", "func_110776_a", "(Lnet/minecraft/util/ResourceLocation;)V");
		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return (node.getOpcode() == INVOKEVIRTUAL || node.getOpcode() == INVOKESPECIAL) && target.matches((MethodInsnNode) node);
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 3));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "replaceParrotTexture", "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/util/ResourceLocation;", false));

					method.instructions.insertBefore(node, newInstructions);
					return false;
				}
		)));
	}

	private static byte[] transformDamageSource(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("causePlayerDamage", "func_76365_a", "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/util/DamageSource;");

		return transform(basicClass, forMethod(sig, (MethodNode method) -> { // Action
					InsnList newInstructions = new InsnList();

					LabelNode jump = new LabelNode();

					newInstructions.add(new VarInsnNode(ALOAD, 0));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "createPlayerDamage", "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/util/DamageSource;", false));
					newInstructions.add(new InsnNode(DUP));
					newInstructions.add(new JumpInsnNode(IFNULL, jump));
					newInstructions.add(new InsnNode(ARETURN));
					newInstructions.add(jump);
					newInstructions.add(new InsnNode(POP));

					method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
					return true;
				}
		));
	}

	// BOILERPLATE BELOW ==========================================================================================================================================

	private static boolean debugLog = false;

	private static byte[] transform(byte[] basicClass, TransformerAction... methods) {
		ClassReader reader;
		try {
			reader = new ClassReader(basicClass);
		} catch (NullPointerException ex) {
			return basicClass;
		}

		ClassNode node = new ClassNode();
		reader.accept(node, 0);
		if (debugLog)
			log(getNodeString(node));

		boolean didAnything = false;

		for (TransformerAction pair : methods)
			didAnything |= pair.test(node);

		if (didAnything) {
			ClassWriter writer = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			if (debugLog)
				log(getNodeString(node));
			debugLog = false;
			node.accept(writer);
			return writer.toByteArray();
		}

		return basicClass;
	}

	public static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction predicate, boolean logResult) {
		for (MethodNode method : node.methods) {
			if (sig.matches(method)) {
				log("Located Method, patching...");

				boolean finish = predicate.test(method);
				if (logResult)
					log("Patch result: " + finish);

				debugMethod(method, finish);

				return finish;
			}
		}

		if (logResult)
			log("Failed to locate the method!");
		return false;
	}

	public static void debugMethod(MethodNode node, boolean result) {
		if (!result) {
			String nodeString = getNodeString(node);
			log("============= Please report this to Quark! =============\n" + nodeString.substring(0, nodeString.length() - 1));
			log("============= Please report this to Quark! =============");
		}
	}

	public static MethodAction combine(NodeFilter filter, NodeAction action) {
		return (MethodNode node) -> applyOnNode(node, filter, action);
	}

	public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
		Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

		boolean didAny = false;
		while (iterator.hasNext()) {
			AbstractInsnNode anode = iterator.next();
			if (filter.test(anode)) {
				log("Located patch target node " + getNodeString(anode));
				didAny = true;
				if (action.test(method, anode))
					break;
			}
		}

		return didAny;
	}

	private static void log(String str) {
		LogManager.getLogger("Quark ASM").info(str);
	}

	private static String getNodeString(ClassNode node) {
		StringWriter sw = new StringWriter();
		PrintWriter printer = new PrintWriter(sw);

		TraceClassVisitor visitor = new TraceClassVisitor(printer);
		node.accept(visitor);

		return sw.toString();
	}

	private static String getNodeString(MethodNode node) {
		Printer printer = new Textifier();

		TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
		node.accept(visitor);

		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();

		return sw.toString();
	}


	private static String getNodeString(AbstractInsnNode node) {
		Printer printer = new Textifier();

		TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
		node.accept(visitor);

		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();

		return sw.toString().replaceAll("\n", "").trim();
	}

	private static boolean hasOptifine(String msg) {
		try {
			if (Class.forName("optifine.OptiFineTweaker") != null) {
				log("Optifine Detected. Disabling Patch for " + msg);
				return true;
			}
		} catch (ClassNotFoundException ignored) {
		}
		return false;
	}

	public static class MethodSignature {
		private final String funcName, srgName, funcDesc;

		public MethodSignature(String funcName, String srgName, String funcDesc) {
			this.funcName = funcName;
			this.srgName = srgName;
			this.funcDesc = funcDesc;
		}

		@Override
		public String toString() {
			return "Names [" + funcName + ", " + srgName + "] Descriptor " + funcDesc;
		}

		public boolean matches(String methodName, String methodDesc) {
			return (methodName.equals(funcName) || methodName.equals(srgName))
					&& (methodDesc.equals(funcDesc));
		}

		public boolean matches(MethodNode method) {
			return matches(method.name, method.desc);
		}

		public boolean matches(MethodInsnNode method) {
			return matches(method.name, method.desc);
		}

		public String mappedName(String owner) {
			return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, srgName, funcDesc);
		}

	}
	/**
	 * Safe class writer.
	 * The way COMPUTE_FRAMES works may require loading additional classes. This can cause ClassCircularityErrors.
	 * The override for getCommonSuperClass will ensure that COMPUTE_FRAMES works properly by using the right ClassLoader.
	 * <p>
	 * Code from: https://github.com/JamiesWhiteShirt/clothesline/blob/master/src/core/java/com/jamieswhiteshirt/clothesline/core/SafeClassWriter.java
	 */
	public static class SafeClassWriter extends ClassWriter {
		public SafeClassWriter(int flags) {
			super(flags);
		}

		@Override
		protected String getCommonSuperClass(String type1, String type2) {
			Class<?> c, d;
			ClassLoader classLoader = Launch.classLoader;
			try {
				c = Class.forName(type1.replace('/', '.'), false, classLoader);
				d = Class.forName(type2.replace('/', '.'), false, classLoader);
			} catch (Exception e) {
				throw new RuntimeException(e.toString());
			}
			if (c.isAssignableFrom(d)) {
				return type1;
			}
			if (d.isAssignableFrom(c)) {
				return type2;
			}
			if (c.isInterface() || d.isInterface()) {
				return "java/lang/Object";
			} else {
				do {
					c = c.getSuperclass();
				} while (!c.isAssignableFrom(d));
				return c.getName().replace('.', '/');
			}
		}
	}

	// Basic interface aliases to not have to clutter up the code with generics over and over again
	private interface Transformer extends Function<byte[], byte[]> {
		// NO-OP
	}

	private interface MethodAction extends Predicate<MethodNode> {
		// NO-OP
	}

	private interface NodeFilter extends Predicate<AbstractInsnNode> {
		// NO-OP
	}

	private interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
		// NO-OP
	}

	private interface TransformerAction extends Predicate<ClassNode> {
		// NO-OP
	}

	private interface NewMethodAction extends BiPredicate<ClassNode, MethodVisitor> {
		// NO-OP
	}

	private static TransformerAction forMethod(MethodSignature sig, MethodAction... actions) {
		return new MethodTransformerAction(sig, actions);
	}

	private static TransformerAction inject(MethodSignature sig, MethodAction ifPresent, NewMethodAction... actions) {
		return new MethodInjectorAction(sig, ifPresent, actions);
	}

	private static class MethodTransformerAction implements TransformerAction {
		private final MethodSignature sig;
		private final MethodAction[] actions;

		public MethodTransformerAction(MethodSignature sig, MethodAction[] actions) {
			this.sig = sig;
			this.actions = actions;
		}

		@Override
		public boolean test(ClassNode classNode) {
			boolean didAnything = false;
			log("Applying Transformation to method (" + sig + ")");
			for (MethodAction action : actions)
				didAnything |= findMethodAndTransform(classNode, sig, action, true);
			return didAnything;
		}
	}

	private static class MethodInjectorAction implements TransformerAction {
		private final MethodSignature sig;
		private final MethodAction ifPresent;
		private final NewMethodAction[] actions;

		public MethodInjectorAction(MethodSignature sig, MethodAction ifPresent, NewMethodAction[] actions) {
			this.sig = sig;
			this.ifPresent = ifPresent;
			this.actions = actions;
		}

		@Override
		public boolean test(ClassNode classNode) {

			boolean didAnything = findMethodAndTransform(classNode, sig, ifPresent, false);

			if (!didAnything) {
				log("Could not locate, injecting method (" + sig + ")");
				MethodVisitor method = classNode.visitMethod(ACC_PUBLIC, LoadingPlugin.runtimeDeobfEnabled ? sig.srgName : sig.funcName, sig.funcDesc, null, null);
				for (NewMethodAction action : actions) {
					boolean finish = action.test(classNode, method);
					log("Patch result: " + finish);
				}
			} else {
				log("Patch result: true");
			}

			return true;
		}
	}

}
