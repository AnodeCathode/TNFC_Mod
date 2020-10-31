package mods.immibis.core.multipart;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.launchwrapper.IClassTransformer;

public class MultipartCoreHookTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {
		if(arg2 != null && arg1.equals("net.minecraft.client.multiplayer.PlayerControllerMP")) {
			// We want to hook onPlayerDestroyBlock and clickBlock since these don't fire events (like they do on the server)
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			new ClassReader(arg2).accept(new ClassVisitor(Opcodes.ASM5, cw) {
				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
					
					if((name.equals("clickBlock") || name.equals("func_78743_b")) && desc.equals("(IIII)V")) {
						mv = new MethodVisitor(Opcodes.ASM5, mv) {
							
							@Override
							public void visitCode() {
								super.visitCode();
								super.visitMethodInsn(Opcodes.INVOKESTATIC, "mods/immibis/core/multipart/MCTHooks", "client_onBlockClicked", "()V", false);
							}
						};
					}
					
					if((name.equals("onPlayerDestroyBlock") || name.equals("func_78751_a")) && desc.equals("(IIII)Z")) {
						mv = new MethodVisitor(Opcodes.ASM5, mv) {
							@Override
							public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
								if(opcode == Opcodes.INVOKEVIRTUAL && owner.equals("net/minecraft/block/Block") && name.equals("removedByPlayer") && desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;III)Z")) {
									
									// replace block.removedByPlayer(...) with
									//   !MCTHooks.client_onBlockBreak(x,y,z) && block.removedByPlayer(...)
									// (while still evaluating the ...)
									
									
									// ILOAD param1 (x)
									// ILOAD param2 (y)
									// ILOAD param3 (z)
									// INVOKESTATIC MCTHooks.client_onBlockBreak (III)Z
									// IFNE skip_normal_processing
									// <original instruction>
									// GOTO end
									// skip_normal_processing:
									// POP2
									// POP2
									// POP2
									// ICONST_0
									// end:
									
									Label end = new Label(), skip_normal = new Label();
									super.visitVarInsn(Opcodes.ILOAD, 1);
									super.visitVarInsn(Opcodes.ILOAD, 2);
									super.visitVarInsn(Opcodes.ILOAD, 3);
									super.visitMethodInsn(Opcodes.INVOKESTATIC, "mods/immibis/core/multipart/MCTHooks", "client_onBlockBreak", "(III)Z", false);
									super.visitJumpInsn(Opcodes.IFNE, skip_normal);
									super.visitMethodInsn(opcode, owner, name, desc, itf);
									super.visitJumpInsn(Opcodes.GOTO, end);
									super.visitLabel(skip_normal);
									super.visitInsn(Opcodes.POP2);
									super.visitInsn(Opcodes.POP2);
									super.visitInsn(Opcodes.POP2);
									super.visitInsn(Opcodes.ICONST_0);
									super.visitLabel(end);
									
								} else
									super.visitMethodInsn(opcode, owner, name, desc, itf);
							}
						};
					}
					
					return mv;
				}
			}, 0);
			return cw.toByteArray();
		}
		
		if(arg2 != null && arg1.equals("net.minecraft.server.management.ItemInWorldManager")) {
			// needs wrapper around removeBlock. If the block sets itself to air, we may need to re-set it to a cover system container.
			ClassWriter cw = new ClassWriter(0);
			new ClassReader(arg2).accept(new ClassVisitor(Opcodes.ASM5, cw) {
				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
					
					if(name.equals("removeBlock") && desc.equals("(IIIZ)Z")) {
						mv = new MethodVisitor(Opcodes.ASM5, mv) {
							@Override
							public void visitCode() {
								super.visitCode();
								super.visitVarInsn(Opcodes.ALOAD, 0);
								super.visitVarInsn(Opcodes.ILOAD, 1);
								super.visitVarInsn(Opcodes.ILOAD, 2);
								super.visitVarInsn(Opcodes.ILOAD, 3);
								super.visitMethodInsn(Opcodes.INVOKESTATIC, "mods/immibis/core/multipart/MCTHooks", "server_removeBlockStart", "(Lnet/minecraft/server/management/ItemInWorldManager;III)V", false);
							}
							
							@Override
							public void visitInsn(int opcode) {
								if(opcode == Opcodes.IRETURN) {
									super.visitVarInsn(Opcodes.ALOAD, 0);
									super.visitVarInsn(Opcodes.ILOAD, 1);
									super.visitVarInsn(Opcodes.ILOAD, 2);
									super.visitVarInsn(Opcodes.ILOAD, 3);
									super.visitMethodInsn(Opcodes.INVOKESTATIC, "mods/immibis/core/multipart/MCTHooks", "server_removeBlockEnd", "(Lnet/minecraft/server/management/ItemInWorldManager;III)V", false);
								}
								super.visitInsn(opcode);
							}
							
							@Override
							public void visitMaxs(int maxStack, int maxLocals) {
								if(maxStack < 5)
									maxStack = 5;
								
								super.visitMaxs(maxStack, maxLocals);
							}
						};
					}

					if((name.equals("tryHarvestBlock") || name.equals("func_73084_b")) && desc.equals("(III)Z")) {
						mv = new MethodVisitor(Opcodes.ASM5, mv) {
							@Override
							public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
								if(opcode == Opcodes.INVOKEVIRTUAL && owner.equals("net/minecraft/block/Block") && (name.equals("harvestBlock") || name.equals("func_149636_a"))) {
									// descriptor: (world, entityplayer, int, int, int, int) -> void
									
									super.visitMethodInsn(Opcodes.INVOKESTATIC, "mods/immibis/core/multipart/MCTHooks", "server_harvestStart", "()V", false);
									super.visitMethodInsn(opcode, owner, name, desc, itf);
									super.visitMethodInsn(Opcodes.INVOKESTATIC, "mods/immibis/core/multipart/MCTHooks", "server_harvestEnd", "()V", false);
									
								} else
									super.visitMethodInsn(opcode, owner, name, desc, itf);
							};
						};
					}
					
					return mv;
				}
			}, 0);
			return cw.toByteArray();
		}
		
		if(arg2 != null && arg1.equals("net.minecraft.client.renderer.RenderBlocks")) {
			ClassWriter cw = new ClassWriter(0);
			new ClassReader(arg2).accept(new ClassVisitor(Opcodes.ASM5, cw) {
				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
					
					if(name.equals("renderBlockByRenderType") || name.equals("func_147805_b")) {
						mv = new MethodVisitor(Opcodes.ASM5, mv) {
							@Override
							public void visitInsn(int opcode) {
								if(opcode == Opcodes.IRETURN) {
									super.visitVarInsn(Opcodes.ALOAD, 0); // this
									super.visitVarInsn(Opcodes.ALOAD, 1); // block
									super.visitVarInsn(Opcodes.ILOAD, 2); // x
									super.visitVarInsn(Opcodes.ILOAD, 3); // y
									super.visitVarInsn(Opcodes.ILOAD, 4); // z
									super.visitMethodInsn(Opcodes.INVOKESTATIC, "mods/immibis/core/multipart/MCTHooks", "client_postRenderBlockInWorld", "(Lnet/minecraft/client/renderer/RenderBlocks;Lnet/minecraft/block/Block;III)Z", false);
									super.visitInsn(Opcodes.IOR);
								}
								super.visitInsn(opcode);
							}
							@Override
							public void visitMaxs(int maxStack, int maxLocals) {
								if(maxStack < 6)
									maxStack = 6;
								super.visitMaxs(maxStack, maxLocals);
							}
						};
					}
					
					return mv;
				}
			}, 0);
			return cw.toByteArray();
		}
		
		
		return arg2;
	}

}
