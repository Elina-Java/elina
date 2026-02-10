package instrumentation.transform;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import instrumentation.Constants;


public class MethodsModifier 
extends ClassVisitor 
implements Opcodes{

	private String cname;
	private String cdesc;
	private Map<String, ClassField> fields;
	private Map<String, MethodInfo> annotated_methods;
	private Map<String, String> method_task_assoc;

	public MethodsModifier(ClassVisitor cv, String cname, Map<String, ClassField> fields, 
			Map<String, MethodInfo> annotated_methods, Map<String, String> method_task_assoc) {
		super(Opcodes.ASM4,cv);
		this.cname = cname;
		this.fields = fields;
		this.annotated_methods = annotated_methods;
		this.method_task_assoc = method_task_assoc;
		this.cdesc = "L"+cname+";";
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		String key = name + desc;
		
		if(annotated_methods.containsKey(key)) 
		{	
			MethodInfo minfo = this.annotated_methods.get(key);
			Set<String> annotations = minfo.getAnnotations();
			
			//Se o método tiver anotação task, criar método que devolve future...
			if(annotations.contains(Constants.TASK_ANNOTATION))
			{
				
				String innerCName = this.method_task_assoc.get(name + desc);
				Type returnType = Type.getReturnType(desc);
				super.visitInnerClass(innerCName, null, null, 0);

				generateGetMethodFuture(minfo.getGetFutureName(), minfo.getGetFutureDesc(), 
						minfo.getGetFutureSign(),
						innerCName, cdesc, minfo);

				generateNewBody(access, name, desc, signature, exceptions, returnType,  minfo);

				return null;
			}
			else
				if(annotations.contains(Constants.ATOMIC_ANNOTATION) || annotations.contains(Constants.CALLBYVALUE_ANNOTATION))
				{
					MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
					return new ModifyMethodAdapter(mv, access, name, desc, cname, minfo.getParams(), annotations);
				}
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}


	private void generateCopyCode(MethodVisitor mv, List<MethodParam> params)
	{
		Iterator<MethodParam> iterator = params.iterator();

		while(iterator.hasNext())
		{
			MethodParam param = iterator.next();
			boolean isAnnotated = param.isAnnotated();
			if(isAnnotated)
			{
				int index = param.getIndex();
				Type paramType = param.getType();

				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(paramType.getOpcode(ILOAD), index);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, cname, "deepCopy", "(Ljava/lang/Object;)Ljava/lang/Object;");
				this.checkCast(mv, paramType);
				mv.visitVarInsn(paramType.getOpcode(ISTORE), index);
			}
		}
	}

	/**
	 * 
	 * Cria método que devolve um Future.
	 * 1. Cria tarefa que executa o conteudo do método original.
	 * 2. Invoca spawn da tarefa e devolve o Future.
	 * 
	 * @param getTaskName
	 * @param getTaskDesc
	 * @param getTaskSign
	 * @param innerCName
	 * @param outerCDesc
	 * @param info
	 */
	private void generateGetMethodFuture(String getTaskName, String getTaskDesc, String getTaskSign,
			String innerCName, String outerCDesc, MethodInfo info)
	{
		List<MethodParam> params = info.getParams();

		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getTaskName, getTaskDesc, getTaskSign, null);
		mv.visitCode();

		mv.visitVarInsn(ALOAD, 0);

		mv.visitTypeInsn(NEW, innerCName);
		mv.visitInsn(DUP);
		initializeParameterArray(mv, params);
		mv.visitMethodInsn(INVOKESPECIAL, innerCName, "<init>", "("+outerCDesc+"[Ljava/lang/Object;)V");
		mv.visitMethodInsn(INVOKEVIRTUAL, cname, "spawn", "("+Constants.ITASK+")"+Constants.IFUTURE);
		mv.visitInsn(ARETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void generateNewBody(int access, String name, String desc, String signature, String [] exceptions, 
			Type returnType, MethodInfo info){

		List<MethodParam> params = info.getParams();
		String get_task_method_name = info.getGetFutureName();
		String get_task_method_desc = info.getGetFutureDesc();
		Set<String> annotations = info.getAnnotations();
		//

		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		mv.visitCode();

		if(annotations.contains(Constants.CALLBYVALUE_ANNOTATION))
			generateCopyCode(mv, params);

		mv.visitVarInsn(ALOAD, 0);
		for(MethodParam param: params)
		{
			Type paramType = param.getType();
			int index = param.getIndex();
			mv.visitVarInsn(paramType.getOpcode(ILOAD), index);
		}

		mv.visitMethodInsn(INVOKEVIRTUAL, cname, get_task_method_name, get_task_method_desc);

		//		if(annotations.contains(Constants.TASK_ANNOTATION))
		mv.visitMethodInsn(INVOKEINTERFACE, Constants.IFUTURE_DESC, "get", "()Ljava/lang/Object;");

		treatReturn(mv, returnType);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void initializeParameterArray(MethodVisitor mv, List<MethodParam> params)
	{
		mv.visitVarInsn(ALOAD, 0);

		int nParams = params.size();
		int taskTotalParams = nParams + 1; //+1 - objecto this

		if(taskTotalParams < 6)
		{
			int iconst = ICONST_0 + taskTotalParams;
			mv.visitInsn(iconst);
		}
		else
			mv.visitIntInsn(BIPUSH, taskTotalParams);

		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

		treatParameters(mv, params);
		//		}

		//Empilhar this
		mv.visitInsn(DUP);

		int index = params.size();
		if(index < 6)
		{
			int iconst = ICONST_0 + index;
			mv.visitInsn(iconst);
		}
		else
			mv.visitIntInsn(BIPUSH, index);

		//this - inserir o this como parametro da tarefa
		mv.visitVarInsn(ALOAD, 0);
		//Guardar no array members o this
		mv.visitInsn(AASTORE);
	}

	private void treatParameters(MethodVisitor mv, List<MethodParam> params)
	{
		int index = 0;

		if(!params.isEmpty())
		{
			Iterator<MethodParam> it = params.iterator();
			while(it.hasNext())
			{
				mv.visitInsn(DUP);
				MethodParam param = it.next();
				int paramIndex = param.getIndex();
				Type paramType = param.getType();
				loadTaskArgs(mv, index, paramIndex, paramType);
				index++;
			}
		}
	}

	private void loadTaskArgs(MethodVisitor mv, int index, int param, Type paramType)
	{		
		if(index < 6)
		{
			int iconst = index + ICONST_0;
			mv.visitInsn(iconst);
		}
		else
			mv.visitIntInsn(BIPUSH, index);

		unboxing(mv, paramType, param);
	}

	private void unboxing(MethodVisitor mv, Type paramType, int param)
	{
		switch(paramType.getSort())
		{
		case Type.BOOLEAN:
		{
			mv.visitVarInsn(ILOAD, param);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
			break;
		}
		case Type.BYTE: {
			mv.visitVarInsn(ILOAD, param);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
			break;
		}
		case Type.CHAR: {
			mv.visitVarInsn(ILOAD, param);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
			break;
		}
		case Type.DOUBLE: {
			mv.visitVarInsn(DLOAD, param);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
			break;
		}
		case Type.FLOAT:{
			mv.visitVarInsn(FLOAD, param);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
			break;
		}
		case Type.INT: {
			mv.visitVarInsn(ILOAD, param);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			break;
		}
		case Type.LONG: {
			mv.visitVarInsn(LLOAD, param);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
			break;
		}
		case Type.SHORT: {
			mv.visitVarInsn(ILOAD, param);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
			break;
		}
		case Type.ARRAY:
		{
			mv.visitVarInsn(ALOAD, param);
			break;
		}
		case Type.OBJECT:
		{
			mv.visitVarInsn(ALOAD, param);
			break;
		}
		}

		mv.visitInsn(AASTORE); //Guardar no array o parametro.
	}


	/*
	private void invokeAtomicBlock(MethodVisitor mv, Type returnType)
	{
		mv.visitMethodInsn(INVOKEVIRTUAL, cname, "atomicBlock", "(Lmiddleware/taskManager/ConcurrentTask;)Ljava/lang/Object;");
		treatReturn(mv, returnType);
	}
	 */

	private void checkCast(MethodVisitor mv, Type type)
	{
		if(!type.equals(Type.getObjectType("java/lang/Object")))
			typeInsn(Opcodes.CHECKCAST, type, mv);
	}

	private void typeInsn(int opcode, Type type, MethodVisitor mv) {
		String desc;
		if(type.getSort() == Type.ARRAY)
		{
			desc = type.getDescriptor();
		}
		else
		{
			desc = type.getInternalName();
		}
		mv.visitTypeInsn(opcode, desc);
	}

	private void treatReturn(MethodVisitor mv, Type methodReturnType)
	{
		int sort = methodReturnType.getSort();

		switch(sort)
		{
		case Type.BOOLEAN:
		{
			mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
			mv.visitInsn(IRETURN);
			break;
		}
		case Type.BYTE: {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
			mv.visitInsn(IRETURN);
			break;
		}
		case Type.CHAR: {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
			mv.visitInsn(IRETURN);
			break;
		}
		case Type.DOUBLE: {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
			mv.visitInsn(DRETURN);
			break;
		}
		case Type.FLOAT:{
			mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
			mv.visitInsn(FRETURN);
			break;
		}
		case Type.INT: {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
			mv.visitInsn(IRETURN);
			break;
		}
		case Type.LONG: {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
			mv.visitInsn(LRETURN);
			break;
		}
		case Type.SHORT: {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
			mv.visitInsn(IRETURN);
			break;
		}
		case Type.VOID: {
			mv.visitInsn(POP);
			mv.visitInsn(RETURN);
			break;
		}
		case Type.ARRAY:
		{
			mv.visitTypeInsn(CHECKCAST, methodReturnType.getDescriptor());
			mv.visitInsn(ARETURN);
			break;
		}
		case Type.OBJECT:
		{
			mv.visitTypeInsn(CHECKCAST, methodReturnType.getInternalName());
			mv.visitInsn(ARETURN);
			break;
		}
		}
	}


	@Override
	public void visitEnd()
	{
		Iterator<Entry<String, ClassField>> it = this.fields.entrySet().iterator();
		while(it.hasNext())
		{
			ClassField field = it.next().getValue();
			String fieldName = field.getName();
			String fieldOwner = field.getOwner();
			String fieldDesc = field.getDesc();

			if(field.hasGetter())
			{
				NewMethodInfo getter = field.getGetter();
				createGetter(getter.getName(), getter.getDesc(), getter.getSignature(), fieldName, fieldOwner, fieldDesc);
			}

			if(field.hasSetter())
			{
				NewMethodInfo setter = field.getSetter();
				createSetter(setter.getName(), setter.getDesc(), setter.getSignature(), fieldName, fieldOwner, fieldDesc);
			}
		}

		super.visitEnd();
	}

	private void createGetter(String method_name, String method_desc, String method_sign, String fieldName, String fieldOwner, String fieldDesc)
	{
		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, method_name, method_desc, method_sign, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, fieldOwner, fieldName, fieldDesc);
		mv.visitInsn(Type.getReturnType(method_desc).getOpcode(IRETURN));
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void createSetter(String method_name, String method_desc, String method_sign, String fieldName, String fieldOwner, String fieldDesc)
	{
		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, method_name, method_desc, method_sign, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(Type.getType(fieldDesc).getOpcode(ILOAD), 1);//Depende do tipo do campo!
		mv.visitFieldInsn(PUTFIELD, fieldOwner, fieldName, fieldDesc);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
}

