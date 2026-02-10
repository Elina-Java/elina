package instrumentation.transform;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingMethodAdapter;

import instrumentation.Constants;


public class WrapperRemappingMethodAdapter 
extends RemappingMethodAdapter
implements Opcodes {

	private MethodInfo minfo;
	
	private String taskName;
	private String outerCName;
	private String outerCDesc;
	private List<MethodParam> params;
	private int nParams;
	private Map<String, ClassField> fields;
	private String taskFieldName;

	private Map<String, MethodInfo> methods_with_annotation;
	private Set<Integer> futures_no_get;

	private Map<Integer, String> varIndex_method_assoc;
	private Map<Integer, Integer> varIndex_futureIndex;
	private Map<Integer, Type> future_type_assoc;
	private Queue<Integer> futures_index;

	private int nextLocalVarIndex;
	private int invoke_order;
	private Set<Integer> methods_with_no_store;

	private boolean storeValueFromAnnotatedMethod;

	public WrapperRemappingMethodAdapter(int access, String desc, MethodVisitor mv, Remapper renamer, 
			String innerName, String outerCName, MethodInfo minfo, Map<String, 
			ClassField> fields, Map<String, MethodInfo> methods_with_annotation, Map<Integer, String> varIndex_method_assoc, Set<Integer> methods_with_no_store) 
	{
		super(access, desc, mv, renamer);
		this.minfo = minfo;
		this.taskName = innerName;
		this.outerCName = outerCName;
		this.outerCDesc = "L"+outerCName+";";
		this.params = minfo.getParams();
		this.fields = fields;
		this.nParams = this.params.size();

		String fieldOwner = outerCName;			
		String [] tokens = fieldOwner.split("/"); 
		this.taskFieldName = tokens[tokens.length-1].toLowerCase();

		this.methods_with_annotation = methods_with_annotation;

		this.futures_no_get = new HashSet<Integer>();

		this.nextLocalVarIndex = minfo.getNLocals();
		this.invoke_order = 1;
		this.storeValueFromAnnotatedMethod = false;

		this.varIndex_method_assoc = varIndex_method_assoc;
		this.varIndex_futureIndex = new HashMap<Integer, Integer>();
		this.future_type_assoc = new HashMap<Integer, Type>();
		this.futures_index = new ArrayDeque<Integer>();
		this.methods_with_no_store = methods_with_no_store;
	}
	

	private void generateBeginAtomic()
	{
		super.visitVarInsn(ALOAD, 0);
		super.visitFieldInsn(GETFIELD, taskName, taskFieldName, outerCDesc);
		super.visitMethodInsn(INVOKEVIRTUAL, outerCName, "beginAtomic", "()V");
	}
	
	private void generateEndAtomic()
	{
		super.visitVarInsn(ALOAD, 0);
		super.visitFieldInsn(GETFIELD, taskName, taskFieldName, outerCDesc);
		super.visitMethodInsn(INVOKEVIRTUAL, outerCName, "endAtomic", "()V");
	}

	@Override
	public void visitCode()
	{
		super.visitCode();

		Iterator<MethodParam> it = this.params.iterator();

		int membersIndex = -1;
		while(it.hasNext())
		{
			MethodParam par = it.next();
			int parIndex = par.getIndex();
			membersIndex = parIndex - 1;

			super.visitVarInsn(ALOAD, 0);
			super.visitFieldInsn(GETFIELD, taskName, "members", "[Ljava/lang/Object;");

			if(membersIndex < 6)
				super.visitInsn(membersIndex + ICONST_0);
			else
				super.visitIntInsn(BIPUSH, membersIndex);

			super.visitInsn(AALOAD);
			unboxing(par.getType());
			super.visitVarInsn(par.getType().getOpcode(ISTORE), parIndex);
		}

		membersIndex++;
		super.visitVarInsn(ALOAD, 0);
		super.visitVarInsn(ALOAD, 0);
		super.visitFieldInsn(GETFIELD, taskName, "members", "[Ljava/lang/Object;");

		if(membersIndex < 6)
			super.visitInsn(membersIndex + ICONST_0);
		else
			super.visitIntInsn(BIPUSH, membersIndex);

		super.visitInsn(AALOAD);

		super.visitTypeInsn(CHECKCAST, outerCName);
		super.visitFieldInsn(PUTFIELD, taskName, taskFieldName, outerCDesc);

		//if has atomic annotation
		if(minfo.getAnnotations().contains(Constants.ATOMIC_ANNOTATION))
			generateBeginAtomic();
		//Criar futures e inicializar com NULL
		initFutures();
	}
	
	//Só não erro se aqui não devolver null!
	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		return null;
	}

	private void initFutures()
	{
		Iterator<Entry<Integer, String>> it = varIndex_method_assoc.entrySet().iterator();

		while(it.hasNext())
		{
			Entry<Integer, String> entry = it.next();
			int var = entry.getKey();
			String methodNameDesc = entry.getValue();
			MethodInfo method_info = methods_with_annotation.get(methodNameDesc);

			//Future
			super.visitInsn(ACONST_NULL);
			super.visitVarInsn(ASTORE, nextLocalVarIndex);
			this.future_type_assoc.put(nextLocalVarIndex, method_info.getReturnType());
			//Se o future não tiver nenhuma variavel local associada...

			if(var >= 0)
				this.varIndex_futureIndex.put(var, nextLocalVarIndex);

			futures_index.add(nextLocalVarIndex);
			nextLocalVarIndex++;	
		}
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc)
	{
		if(opcode == INVOKEVIRTUAL && owner.equals(outerCName) && !name.equals("sync"))
		{	
			super.visitFieldInsn(GETFIELD, taskName, taskFieldName, outerCDesc);

			if(methods_with_annotation.containsKey(name + desc))
			{				
				MethodInfo minfo = methods_with_annotation.get(name + desc);
				
				if(minfo.getAnnotations().contains(Constants.TASK_ANNOTATION))
				{
					this.invoke_order++;
					this.storeValueFromAnnotatedMethod = false;

					
					int future_index = 0;
					boolean storeFuture = false;
					if(!this.methods_with_no_store.contains(invoke_order))
					{
						future_index = this.futures_index.poll();
						storeFuture = true;
					}

					//Obter Future
					super.visitMethodInsn(INVOKEVIRTUAL, outerCName, minfo.getGetFutureName(), minfo.getGetFutureDesc());
					
					if(storeFuture)
					{
						super.visitVarInsn(ASTORE, future_index);
						this.futures_no_get.add(future_index);
						this.storeValueFromAnnotatedMethod = true;
					}
					else
					{
						super.visitMethodInsn(INVOKEINTERFACE, Constants.IFUTURE_DESC, "get", "()Ljava/lang/Object;");
						unboxing(minfo.getReturnType());
					}
				}
				else
					super.visitMethodInsn(opcode, owner, name, desc);
			}
			else
				super.visitMethodInsn(opcode, owner, name, desc);
		}
		else if(name.equals("sync"))
			invokeGetPendingFutures();
		else
			super.visitMethodInsn(opcode, owner, name, desc);
	}

	/* Invocar get() dos futures que não foram invocados - caso dos futures de tipo void.*/
	private void invokeGetPendingFutures()
	{
		Iterator<Integer> it = futures_no_get.iterator();
		while(it.hasNext())
		{
			int future_index = it.next();
			Label l3 = new Label();
			super.visitVarInsn(ALOAD, future_index);
			super.visitJumpInsn(IFNULL, l3);
			super.visitVarInsn(ALOAD, future_index);
			super.visitMethodInsn(INVOKEINTERFACE, Constants.IFUTURE_DESC, "get", "()Ljava/lang/Object;");
			super.visitInsn(POP);
			super.visitLabel(l3);
		}
	}


	@Override
	public void visitVarInsn(int opcode, int var)
	{
		if(opcode >= ISTORE && opcode <= ASTORE)
		{
			if(varIndex_method_assoc.containsKey(var) && storeValueFromAnnotatedMethod)
			{
				int future_index = this.varIndex_futureIndex.get(var);
				this.varIndex_futureIndex.put(var, future_index);
				this.storeValueFromAnnotatedMethod = false;
			}
			else
				super.visitVarInsn(opcode, var);
		}
		else
		{
			if(opcode >=ILOAD && opcode <= ALOAD)
			{
				if(varIndex_futureIndex.containsKey(var))
				{
					int future_index = varIndex_futureIndex.get(var);

					Label label1 = new Label();

					super.visitVarInsn(ALOAD, future_index);
					super.visitJumpInsn(IFNULL, label1);
					super.visitVarInsn(ALOAD, future_index);
					super.visitMethodInsn(INVOKEINTERFACE, Constants.IFUTURE_DESC, "get", "()Ljava/lang/Object;");
					Type futureType = this.future_type_assoc.get(future_index);
					unboxing(futureType);
					//3 - offset entre *load e *store
					super.visitVarInsn(opcode + 33, var);
					super.visitInsn(ACONST_NULL);
					super.visitVarInsn(ASTORE, future_index);
					super.visitLabel(label1);
					//if future == null
					super.visitVarInsn(opcode, var);

					this.futures_no_get.remove(future_index);
				}
				else
					super.visitVarInsn(opcode, var);
			}
			else
				super.visitVarInsn(opcode, var);
		}
	}


	private void unboxing(Type type)
	{
		switch (type.getSort()) {
		case Type.BOOLEAN:
			super.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
			break;
		case Type.BYTE:
			super.visitTypeInsn(CHECKCAST, "java/lang/Byte");
			super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
			break;
		case Type.CHAR:
			super.visitTypeInsn(CHECKCAST, "java/lang/Character");
			super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
			break;
		case Type.SHORT:
			super.visitTypeInsn(CHECKCAST, "java/lang/Short");
			super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
			break;
		case Type.INT:
			super.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
			break;
		case Type.FLOAT:
			super.visitTypeInsn(CHECKCAST, "java/lang/Float");
			super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
			break;
		case Type.LONG:
			super.visitTypeInsn(CHECKCAST, "java/lang/Long");
			super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
			break;
		case Type.DOUBLE:
			super.visitTypeInsn(CHECKCAST, "java/lang/Double");
			super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
			break;
		case Type.ARRAY:
			super.visitTypeInsn(CHECKCAST, type.getDescriptor());
			break;
		case Type.OBJECT:
			super.visitTypeInsn(CHECKCAST, type.getInternalName());
			break;
		}
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc)
	{
		if(this.fields.containsKey(name))
		{
			ClassField field = this.fields.get(name);
			super.visitFieldInsn(GETFIELD, taskName, taskFieldName, outerCDesc);
			if(opcode == GETFIELD)
			{
				NewMethodInfo getter = field.getGetter();
				super.visitMethodInsn(INVOKEVIRTUAL, owner, getter.getName(), getter.getDesc());
			}
			else if(opcode == PUTFIELD)
			{
				NewMethodInfo setter = field.getSetter();
				super.visitMethodInsn(INVOKEVIRTUAL, owner, setter.getName(), setter.getDesc());
			}
		}
		else
			super.visitFieldInsn(opcode, owner, name, desc);
	}


	@Override
	public void visitInsn(int opcode){

		if (opcode >= IRETURN && opcode <= RETURN)
		{
			if(minfo.getAnnotations().contains(Constants.ATOMIC_ANNOTATION))
				generateEndAtomic();
			
			switch(opcode)
			{
			case RETURN:
			{
				super.visitInsn(ACONST_NULL);
				super.visitInsn(ARETURN);
				break;
			}
			case IRETURN:
			{
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitInsn(ARETURN);
				break;
			}
			case DRETURN:
			{
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				super.visitInsn(ARETURN);
				break;
			}
			case FRETURN:
			{
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
				super.visitInsn(ARETURN);
				break;
			}
			case LRETURN:
			{
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitInsn(ARETURN);
				break;
			}
			case ARETURN: { super.visitInsn(opcode); break;}
			}
		}
		else 
			super.visitInsn(opcode);

	}

	/*
	 * Substituir os getfields por loads na posição correspodenente no array members.
	 */

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
	{
		/*
		 * Os parametros do método original estão guardados no vector members.
		 * Por isso, terei que ignorar todas as variaveis locais que sejam parametros do método original.
		 */
		if(index > nParams)
			super.visitLocalVariable(name, desc, signature, start, end, index);
	}

}

