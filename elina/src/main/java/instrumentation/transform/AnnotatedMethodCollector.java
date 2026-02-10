package instrumentation.transform;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AnnotatedMethodCollector 
extends ClassVisitor{
	
	//class name
	private String cname;
	//annotated methods
	private Map<String, MethodInfo> methods;
	//all class fields
	private Map<String, ClassField> fields;
	
	private int innerClassesCounter = 0;

	public AnnotatedMethodCollector(String cname) {
		super(Opcodes.ASM4);
		methods = new HashMap<String, MethodInfo>();
		this.fields = new HashMap<String, ClassField>();
		this.cname = cname;
	}
	

	//signature the method's signature. May be null if the method parameters, return type and exceptions do not use generic types.
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) 
	{
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		return new MethodCollector(mv, access, name, desc, signature, exceptions, this);
	}

	//signature - the field's signature. May be null if the field's type does not use generic types.
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String sig, Object value) 
	{
		this.fields.put(name, new ClassField(access, cname, name, desc, sig));
		return super.visitField(access, name, desc, sig, value);
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access)
	{
		innerClassesCounter++;
		super.visitInnerClass(name, outerName, innerName, access);
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, MethodInfo> getAnnotatedMethods() 
	{
		return methods;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, ClassField> getClassFields()
	{
		return fields;
	}

	/**
	 * 
	 * @return
	 */
	public int getInnerClassesCount()
	{
		return innerClassesCounter;
	}
	
	/**
	 * 
	 * @param name
	 * @param desc
	 * @param m_info
	 */
	public void addMethodInfo(String name, String desc, MethodInfo m_info) {
		this.methods.put(name+desc, m_info);
	}
}

