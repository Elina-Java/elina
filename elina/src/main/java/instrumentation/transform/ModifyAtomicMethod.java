package instrumentation.transform;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class ModifyAtomicMethod 
extends AdviceAdapter{

	private String cname;
	
	protected ModifyAtomicMethod(MethodVisitor mv, int access, String name, String desc, String ownerName) {
		super(Opcodes.ASM4,mv, access, name, desc);
		this.cname = ownerName;
	}

	@Override
	public void onMethodEnter()
	{
		super.loadThis();
		super.visitMethodInsn(INVOKEVIRTUAL, cname, "beginAtomic", "()V");
	}
	
	@Override
	protected void onMethodExit(int opcode)
	{
		if(opcode >= IRETURN && opcode <= RETURN)
		{
			super.loadThis();
			super.visitMethodInsn(INVOKEVIRTUAL, cname, "endAtomic", "()V");
		}
	}
	
}
