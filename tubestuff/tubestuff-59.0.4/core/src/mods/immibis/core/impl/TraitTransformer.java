package mods.immibis.core.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import mods.immibis.core.api.traits.ITrait;
import mods.immibis.core.api.traits.TraitClass;
import mods.immibis.core.api.traits.TraitMethod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class TraitTransformer implements net.minecraft.launchwrapper.IClassTransformer {
	
	public static final boolean DEBUG = Boolean.getBoolean("mods.immibis.core.debugTraits");
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		
		try {
			if(bytes == null)
				return bytes;
			
			ClassNode cn = new ClassNode();
			new ClassReader(bytes).accept(cn, 0);
			
			{
				boolean hasUsesTraitsAnnotation = false;
				
				if(cn.invisibleAnnotations != null)
				for(AnnotationNode an : (List<AnnotationNode>)cn.invisibleAnnotations) {
					if(an.desc.equals("Lmods/immibis/core/api/traits/UsesTraits;"))
						hasUsesTraitsAnnotation = true;
				}
				
				if(!hasUsesTraitsAnnotation)
					return bytes;
			}
			
			if(DEBUG)
				System.out.println("[Immibis Core] processing traits for class '"+name+"'");
			
			class InitializationListEntry {
				String fieldName;
				String fieldDesc;
				String className;
			}
			
			// List of fields to be initialized in the constructor
			List<InitializationListEntry> initializationList = new ArrayList<InitializationListEntry>();
			
			for(FieldNode fn : (List<FieldNode>)cn.fields) {
				AnnotationNode traitFieldAnno = null;
				if(fn.invisibleAnnotations != null)
					for(AnnotationNode an : (List<AnnotationNode>)fn.invisibleAnnotations) {
						if(an.desc.equals("Lmods/immibis/core/api/traits/TraitField;")) {
							traitFieldAnno = an;
							break;
						}
					}
				
				if(traitFieldAnno == null)
					continue;
				
				Class<? extends ITrait> traitClass;
				try {
					traitClass = Class.forName(Type.getType(fn.desc).getClassName()).asSubclass(ITrait.class);
				} catch(RuntimeException e) {
					throw e;
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
				
				boolean isTraitFieldInterface = false;
				
				Class<? extends ITrait> traitFieldType = traitClass;
				
				if(traitClass.isInterface()) {
					if(!ITrait.knownInterfaces.containsKey(traitClass))
						throw new RuntimeException("Trait interface not registered: "+traitClass);
					traitClass = ITrait.knownInterfaces.get(traitClass);
					isTraitFieldInterface = true;
				}
				
				addTrait(cn, traitClass, traitFieldType, fn.name, fn.desc, isTraitFieldInterface);
				
				InitializationListEntry ile = new InitializationListEntry();
				ile.className = traitClass.getName().replace('.','/');
				ile.fieldName = fn.name;
				ile.fieldDesc = fn.desc;
				initializationList.add(ile);
			}
			
			// add initialization to constructors, except those that call another constructor
			for(MethodNode mn : (List<MethodNode>)cn.methods) {
				if(!mn.name.equals("<init>"))
					continue;
				
				boolean callsOtherConstructor = false;
				AbstractInsnNode insertAfter = null;
				{
					AbstractInsnNode cur = mn.instructions.getFirst();
					while(cur != null) {
						if(cur instanceof MethodInsnNode) {
							MethodInsnNode min = (MethodInsnNode)cur;
							if(min.name.equals("<init>")) {
								if(min.owner.equals(cn.name)) {
									callsOtherConstructor = true;
									break;
								} else if(min.owner.equals(cn.superName)) {
									insertAfter = cur;
									break;
								}
							}
						}
						cur = cur.getNext();
					}
				}
				
				if(callsOtherConstructor)
					continue;
				
				if(mn.maxStack < 4)
					mn.maxStack = 4;
				
				InsnList newInsns = new InsnList();
				
				for(InitializationListEntry ile : initializationList) {
					boolean alreadyAssigned = false;
					
					{
						AbstractInsnNode cur = mn.instructions.getFirst();
						while(cur != null) {
							if(cur instanceof FieldInsnNode) {
								FieldInsnNode fin = (FieldInsnNode)cur;
								if(fin.getOpcode() == Opcodes.PUTFIELD && fin.name.equals(ile.fieldName) && fin.desc.equals(ile.fieldDesc) && fin.owner.equals(cn.name)) {
									alreadyAssigned = true;
									break;
								}
							}
							cur = cur.getNext();
						}
					}
					
					if(alreadyAssigned)
						continue;
					
					newInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
					newInsns.add(new TypeInsnNode(Opcodes.NEW, ile.className));
					newInsns.add(new InsnNode(Opcodes.DUP));
					newInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
					newInsns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ile.className, "<init>", "(Ljava/lang/Object;)V"));
					newInsns.add(new FieldInsnNode(Opcodes.PUTFIELD, cn.name, ile.fieldName, ile.fieldDesc));
				}
				
				mn.instructions.insert(insertAfter, newInsns);
			}
			
			ClassWriter cw = new ClassWriter(0);
			cn.accept(cw);
			return cw.toByteArray();
		
		} catch(Throwable t) {
			// LaunchClassLoader (which calls this) swallows exceptions, because Mojang.
			// So at least output the stack trace.
			t.printStackTrace();
			throw t;
		}
	}
	
	/*private Set<Class<?>> getAllInterfaces(Class<?> c) {
		if(c == null)
			return new HashSet<Class<?>>();
		
		Set<Class<?>> rv = getAllInterfaces(c.getSuperclass());
		for(Class<?> iface : c.getInterfaces()) {
			rv.add(iface);
			rv.addAll(getAllInterfaces(iface));
		}
		return rv;
	}*/
	
	private boolean isMethodPresent(ClassNode cn, String methodName, Type methodType) {
		String methodDesc = methodType.getDescriptor();
		
		for(MethodNode mn : (List<MethodNode>)cn.methods) {
			if(mn.name.equals(methodName) && mn.desc.equals(methodDesc))
				return true;
		}
		
		return false;
	}
	
	private boolean isMethodPresent(Class<?> cn, String methodName, Type methodType) {
		
		for(Method mn : cn.getMethods()) {
			if(mn.getName().equals(methodName) && Type.getType(mn).equals(methodType))
				return true;
		}
		
		return false;
	}

	private void addTrait(ClassNode cn, Class<? extends ITrait> traitClass, Class<? extends ITrait> traitFieldType, String traitFieldName, String traitFieldDesc, boolean isTraitFieldInterface) {
		TraitClass tca = traitClass.getAnnotation(TraitClass.class);
		for(Class<?> intrface : tca.interfaces()) {
			cn.interfaces.add(intrface.getName().replace('.', '/'));
		}
		
		if(DEBUG)
			System.out.println("[Immibis Core] adding trait '"+traitClass.getName()+"' to '"+cn.name+"' through trait field '"+traitFieldName+"' of type '"+traitFieldDesc+"'");
		
		for(Method m : traitClass.getMethods()) {
			if(m.isAnnotationPresent(TraitMethod.class)) {
				if(isMethodPresent(cn, m.getName(), Type.getType(m))) {
					if(DEBUG)
						System.out.println("[Immibis Core] '"+m+"' already exists on '"+cn.name+"', not adding");
				} else {
					if(DEBUG)
						System.out.println("[Immibis Core] adding '"+m+"' to '"+cn.name+"'");
					
					Type mdesc = Type.getType(m);
					
					MethodNode mn = new MethodNode();
					
					mn.name = m.getName();
					mn.desc = mdesc.getDescriptor();
					
					// push this.traitField
					mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, traitFieldName, traitFieldDesc));
					
					String methodOwner = traitFieldDesc.substring(1, traitFieldDesc.length() - 1); // remove L and ;
					int methodOpcode = isTraitFieldInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
					
					// if the method isn't in the field type, add a cast
					if(isTraitFieldInterface && !isMethodPresent(traitFieldType, mn.name, mdesc))
					{
						methodOwner = traitClass.getName().replace('.', '/');
						methodOpcode = Opcodes.INVOKEVIRTUAL;
						
						mn.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, methodOwner));
					}
					
					// push arguments
					Type[] argtypes = mdesc.getArgumentTypes();
					int argsize = 1;
					for(int k = 0; k < argtypes.length; k++) {
						switch(argtypes[k].getSort()) {
						case Type.ARRAY: case Type.OBJECT:
							mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, argsize));
							argsize += 1;
							break;
						case Type.BOOLEAN: case Type.BYTE: case Type.CHAR: case Type.INT: case Type.SHORT:
							mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, argsize));
							argsize += 1;
							break;
						case Type.DOUBLE:
							mn.instructions.add(new VarInsnNode(Opcodes.DLOAD, argsize));
							argsize += 2;
							break;
						case Type.FLOAT:
							mn.instructions.add(new VarInsnNode(Opcodes.FLOAD, argsize));
							argsize += 1;
							break;
						case Type.LONG:
							mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, argsize));
							argsize += 2;
							break;
						default:
							throw new RuntimeException("For method '"+m+"' in '"+traitClass+"': argument "+k+" has unknown sort "+argtypes[k].getSort());
						}
					}
					
					// call the method
					mn.instructions.add(new MethodInsnNode(methodOpcode,
						methodOwner,
						mn.name,
						mn.desc));
					
					// return
					
					int retsize;
					switch(mdesc.getReturnType().getSort()) {
					case Type.ARRAY: case Type.OBJECT:
						mn.instructions.add(new InsnNode(Opcodes.ARETURN));
						retsize = 1;
						break;
					case Type.BOOLEAN: case Type.BYTE: case Type.CHAR: case Type.INT: case Type.SHORT:
						mn.instructions.add(new InsnNode(Opcodes.IRETURN));
						retsize = 1;
						break;
					case Type.DOUBLE:
						mn.instructions.add(new InsnNode(Opcodes.DRETURN));
						retsize = 2;
						break;
					case Type.FLOAT:
						mn.instructions.add(new InsnNode(Opcodes.FRETURN));
						retsize = 1;
						break;
					case Type.LONG:
						mn.instructions.add(new InsnNode(Opcodes.LRETURN));
						retsize = 2;
						break;
					case Type.VOID:
						mn.instructions.add(new InsnNode(Opcodes.RETURN));
						retsize = 0;
						break;
					default:
						throw new RuntimeException("For method '"+m+"' in '"+traitClass+"': return type has unknown sort "+mdesc.getReturnType().getSort());
					}
					
					mn.maxLocals = argsize + 1;
					mn.maxStack = Math.max(argsize + 1, retsize);
					
					mn.access = Opcodes.ACC_PUBLIC;
					
					{
						mn.exceptions = new ArrayList<String>();
						for(Class<?> ne : m.getExceptionTypes())
							mn.exceptions.add(Type.getType(ne).getDescriptor());
					}
					
					cn.methods.add(mn);
				}
			}
		}
	}

}
