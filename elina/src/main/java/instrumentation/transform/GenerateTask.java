package instrumentation.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import instrumentation.Constants;
import service.Task;



public class GenerateTask 
implements Opcodes{

	private OuterClassInfo outer;
	private MethodNode mn;

	//Informação para a criação da tarefa
	private String taskName;
	//private String taskDesc; //Descrição
	private String taskInternalName; //Nome interno (middleware/taskManager/Task)
	private String taskSign; //Assinature da tarefa
	private String returnTypeDesc; //Tipo de retorno da tarefa - igual ao retorno do método original
	//
	private Map<String, ClassField> class_fields;
	private Map<String, MethodInfo> methods_with_annotation;
	private MethodInfo minfo;

	@SuppressWarnings("unused")
	private LabelNode label1;
	@SuppressWarnings("unused")
	private LabelNode label2;

	public GenerateTask(String inner, OuterClassInfo outer, MethodNode mn, 
			MethodInfo minfo, Map<String, ClassField> class_fields, Map<String, MethodInfo> methods_with_annotation)
	{
		this.taskName = inner;
		this.outer = outer;
		this.mn = mn;

	//	String taskDesc = Type.getDescriptor(Task.class);
	// taskInternalName = Type.getInternalName(Task.class);

		String desc = Type.getDescriptor(Task.class);
		desc = desc.replace(';', '<');

		this.minfo = minfo;
		this.returnTypeDesc = this.getTypeDesc(minfo.getReturnType());
		this.taskSign = desc.concat(returnTypeDesc+">;");

		this.class_fields = class_fields;

		this.methods_with_annotation = methods_with_annotation;
	}

	public byte[] createTask() 
	throws Exception
	{
		//TODO - Mudar para compute frames quando tiver tudo correcto.
//		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		FieldVisitor fv;
		MethodVisitor mv;

		//Outer class info
		String outerCName = outer.getOuterCName();
		String outerCDesc = "L"+outerCName+";";
		String outerMName = outer.getOuterMName();
		String outerMDesc = outer.getOuterMDesc();

		String [] tokens = outerCName.split("/"); 
		String taskFieldName = tokens[tokens.length-1].toLowerCase();
		//

		//Task Class Header
		cw.visit(Constants.VERSION, ACC_SUPER, taskName, this.taskSign, this.taskInternalName, null);
		cw.visitOuterClass(outerCName, outerMName, outerMDesc);
		cw.visitInnerClass(taskName, null, null, 0);

		//Task Class Fields
		//TODO - Pode ser usado o field criado a seguir (this$0), caso a classe continue interna.
		cw.visitField(ACC_PRIVATE, taskFieldName, outerCDesc, null, null).visitEnd();
		//

		fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$0", outerCDesc, null, null);
		fv.visitEnd();


		//Class Constructor
		mv = cw.visitMethod(ACC_VARARGS, "<init>", "("+outerCDesc+"[Ljava/lang/Object;)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(PUTFIELD, taskName, "this$0", outerCDesc);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKESPECIAL, this.taskInternalName, "<init>", "([Ljava/lang/Object;)V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
		//Service getService()
		mv = cw.visitMethod(ACC_PUBLIC, "getService", "()"+Constants.ISERVICE, null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, taskName, "this$0", outerCDesc);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
		//Call()
		mv = cw.visitMethod(ACC_PUBLIC, "call", "()"+returnTypeDesc, null, null);

		Map<Integer, String> method_varIndex_assoc = new HashMap<Integer, String>();
		Set<Integer> invocations_with_no_store = new HashSet<Integer>();

		mn.instructions.resetLabels();
		@SuppressWarnings("unchecked")
		Iterator<AbstractInsnNode> it = mn.instructions.iterator();

//		solveRecursiveParallelism(mn.instructions);

		int index_void = -1;
		int invoke_order = 1;
		boolean toRemove = false;

		while(it.hasNext())
		{
			AbstractInsnNode node = it.next();

			if(toRemove)// && node.getOpcode() == POP)
			{
				mn.instructions.remove(node);
				toRemove = false;
			}
			else
			{
				if(node.getOpcode() == INVOKEVIRTUAL)
				{
					MethodInsnNode min = (MethodInsnNode) node;

					if(min.name.equals("sync"))
						mn.instructions.remove(node.getPrevious()); //POP do ALOAD_0
					else
					{
						if(methods_with_annotation.containsKey(min.name + min.desc))
						{
							invoke_order++;

							AbstractInsnNode next = node.getNext();

							if(next.getOpcode() >= ISTORE && next.getOpcode() <= ASTORE)
							{
								VarInsnNode varInsn = (VarInsnNode) next;
								method_varIndex_assoc.put(varInsn.var, min.name + min.desc);
							}
							else
							{	
								if(next.getOpcode() == POP || Type.getReturnType(min.desc).getSort() == Type.VOID)
								{
									method_varIndex_assoc.put(index_void, min.name + min.desc);
									index_void--;

									if(next.getOpcode() == POP)
										toRemove = true;
								}
								else
									invocations_with_no_store.add(invoke_order);
							}
						}	
					}
				}
				else
					if(node.getOpcode() == Opcodes.ALOAD)
					{
						//Substituir ALOAD_0; DUP. Por: ALOAD_0; ALOAD_0
						VarInsnNode ins_node = (VarInsnNode) node;
						if(ins_node.getNext().getOpcode() == DUP)
						{
							VarInsnNode aload_node = new VarInsnNode(ALOAD, 0);
							mn.instructions.set(ins_node.getNext(), aload_node);
						}
					}		
			}
		}

		mn.accept(new WrapperRemappingMethodAdapter(ACC_PUBLIC, "()"+returnTypeDesc, mv, new SimpleRemapper(null, null),
				taskName, outerCName, minfo, class_fields, methods_with_annotation, method_varIndex_assoc, invocations_with_no_store));

		//Call() Generic
		mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "call", "()Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKEVIRTUAL, taskName, "call", "()"+returnTypeDesc);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		cw.visitEnd();

		byte [] code = cw.toByteArray();

		byte [] bytecode = changeMethodBody(code);
		//CheckClassAdapter.verify(new ClassReader(bytecode), true, new PrintWriter(System.out));
		
		return bytecode;
	}

	private byte [] changeMethodBody(byte [] bytecode)
	{
		ClassReader reader = new ClassReader(bytecode);
		ClassNode cNode = new ClassNode();
		reader.accept(cNode, ClassReader.SKIP_DEBUG);

		/* Trocar as linhas de código. */

		@SuppressWarnings("unchecked")
		Iterator<MethodNode> it = cNode.methods.iterator();
		MethodNode method = null;
		while(it.hasNext())
		{
			method = it.next();
			if(method.name.equals("call") && !method.desc.equals("()Ljava/lang/Object;"))
				break;
		}

		InsnList instr = method.instructions;
		swapInstrSetMethod(instr);
//		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cNode.accept(writer);

		return writer.toByteArray();
	}

	
	//Procurar por invocações recursivas - Invocação do mesmo método (nome + descrição)
//	private void solveRecursiveParallelism(InsnList instr) {
//
//		@SuppressWarnings("unchecked")
//		Iterator<AbstractInsnNode> it = instr.iterator();
//		Stack<VarInsnNode> stack = new Stack<VarInsnNode>();
//
//		String method_name = minfo.getName();
//		String method_desc = minfo.getDesc();
//
//		while(it.hasNext())
//		{
//			AbstractInsnNode ain = it.next();
//			
//			if(ain.getOpcode() == ALOAD)
//			{
//				VarInsnNode aload = (VarInsnNode) ain;
//				if(aload.var == 0)
//					stack.add(aload);
//			}
//			else
//			{
//				if(ain.getOpcode() == INVOKEVIRTUAL)
//				{
//					MethodInsnNode min = (MethodInsnNode) ain;
//					if(min.name.equals(method_name) && min.desc.equals(method_desc))
//						createIfThenElseCode(instr, stack.pop(), min);
//				}
//			}
//		}
//	}

//	ESTOU A RETIRAR OS METODOS CLONED
//	private void createIfThenElseCode(InsnList instrList, VarInsnNode aload, MethodInsnNode min)
//	{
//		VarInsnNode aload_0 = aload;
//		addIfClause(instrList, aload_0);
//
//		//3. No fim do código, acrescentar VisitLabel l2.
//		InsnList clone = cloneMethodFrame(aload_0, min);
//
//		//Inserir clone...
//		AbstractInsnNode next = min;
//		if(min.getNext().getOpcode() >= ISTORE && min.getNext().getOpcode() <= ASTORE ||
//				min.getNext().getOpcode() == POP)
//			next = min.getNext();
//			
//		instrList.insert(next, clone);
//	}

//	ESTOU A RETIRAR OS METODOS CLONED
//	private void addIfClause(InsnList instrList , VarInsnNode aload_0)
//	{
//		InsnList newList = new InsnList();
//		VarInsnNode newALoad0 = new VarInsnNode(ALOAD, 0);
//		newList.add(newALoad0);
//
//		String outerName = outer.getOuterCName();			
//
//		MethodInsnNode mnode = new MethodInsnNode(INVOKEVIRTUAL, outerName,"freeThreads" , "()Z");
//		newList.add(mnode);
//		label1 = new LabelNode();
//		JumpInsnNode jump = new JumpInsnNode(IFEQ, label1);
//		newList.add(jump);
//
//		instrList.insertBefore(aload_0, newList);
//	}

//	ESTOU A RETIRAR OS METODOS CLONED
//	private InsnList cloneMethodFrame(VarInsnNode aload, MethodInsnNode min)
//	{
//		InsnList newMethodFrame = new InsnList();
//		AbstractInsnNode current = aload;
//		label2 = new LabelNode();
//
//		JumpInsnNode jump = new JumpInsnNode(GOTO, label2);
//		newMethodFrame.add(jump);
//		newMethodFrame.add(label1);
//
//		while(!current.equals(min))
//		{
//			newMethodFrame.add(current.clone(null));
//			current = current.getNext();
//		}
//		
//
//		MethodInsnNode newInvocation = new MethodInsnNode(min.getOpcode(), min.owner, min.name+"__cloned", min.desc);
//		newMethodFrame.add(newInvocation);
//
//		if( (min.getNext().getOpcode() >= ISTORE && min.getNext().getOpcode() <= ASTORE) || min.getNext().getOpcode() == POP)
//			newMethodFrame.add(min.getNext().clone(null));
//
//		newMethodFrame.add(label2);
//
//		return newMethodFrame;
//	}



	@SuppressWarnings("unchecked")
	private void swapInstrSetMethod(InsnList instr)
	{
		Iterator<AbstractInsnNode> j = instr.iterator();
		int index = 0;

		Stack<VarInsnNode> aloads_zero = new Stack<VarInsnNode>();

		while (j.hasNext()) 
		{
			AbstractInsnNode in =  j.next();
			int op = in.getOpcode();
			//Caso op seja um putfield, então trocar as linhas n-1 com a n-2

			if(op == Opcodes.ALOAD)
			{
				VarInsnNode ins_node = (VarInsnNode) in;
				if(ins_node.var == 0)
					aloads_zero.add(ins_node);
			}
			else
				if(op == Opcodes.INVOKEVIRTUAL)
				{
					MethodInsnNode methodNode = (MethodInsnNode) in;
					if(methodNode.owner.equals(outer.getOuterCName()))
					{
						AbstractInsnNode getField = instr.get(index - 1);
						instr.remove(getField);
						instr.insert(aloads_zero.pop(), getField);
					}
				}
			index ++;
		}
	}

	private String getTypeDesc(Type type)
	{
		int sort = type.getSort();
		switch(sort)
		{
		case Type.BOOLEAN: return Type.getType(Boolean.class).getDescriptor();
		case Type.BYTE: return Type.getType(Byte.class).getDescriptor();
		case Type.CHAR: return Type.getType(Character.class).getDescriptor();
		case Type.DOUBLE: return Type.getType(Double.class).getDescriptor();
		case Type.FLOAT: return Type.getType(Float.class).getDescriptor();
		case Type.INT: return Type.getType(Integer.class).getDescriptor();
		case Type.LONG: return Type.getType(Long.class).getDescriptor();
		case Type.SHORT: return Type.getType(Short.class).getDescriptor();
		case Type.VOID: return Type.getType(Void.class).getDescriptor();

		default: return type.getDescriptor();
		}
	}
}

