package instrumentation.transform;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import instrumentation.Constants;


public class ModifyMethodAdapter 
extends AdviceAdapter{

	private String cname;
	private List<MethodParam> params;
	private Set<String> annotations;

	protected ModifyMethodAdapter(MethodVisitor mv, int access, String name, String desc, String ownerName, List<MethodParam> params, Set<String> annotations) {
		super(Opcodes.ASM4,mv, access, name, desc);
		this.cname = ownerName;
		this.params = params;
		this.annotations = annotations;
	}

	@Override
	public void onMethodEnter()
	{
		if(annotations.contains(Constants.CALLBYVALUE_ANNOTATION))
			generateCopyCode();	

		if(annotations.contains(Constants.ATOMIC_ANNOTATION))
		{
			super.loadThis();
			super.visitMethodInsn(INVOKEVIRTUAL, cname, "beginAtomic", "()V");
		}
	}

	@Override
	protected void onMethodExit(int opcode)
	{
		if(annotations.contains(Constants.ATOMIC_ANNOTATION))
		{
			if(opcode >= IRETURN && opcode <= RETURN)
			{
				super.loadThis();
				super.visitMethodInsn(INVOKEVIRTUAL, cname, "endAtomic", "()V");
			}
		}
	}


	private void generateCopyCode()
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

				super.visitVarInsn(ALOAD, 0);
				super.visitVarInsn(paramType.getOpcode(ILOAD), index);
				super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, cname, "copy", "(Ljava/lang/Object;)Ljava/lang/Object;");
				this.checkCast(mv, paramType);
				super.visitVarInsn(paramType.getOpcode(ISTORE), index);
			}
		}
	}

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
}
