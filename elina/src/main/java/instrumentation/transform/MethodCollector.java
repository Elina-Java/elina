package instrumentation.transform;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import instrumentation.Constants;


public class MethodCollector 
extends MethodVisitor  {
	
	//Method info
	private int access;
	private String name;
	private String desc;
	private String signature;
	private String [] exceptions;

	private boolean hasAnnotations;
	
	private Type [] params;
	private int n_params;
	
	private Set<Integer> annotated_parameters;
	
	private AnnotatedMethodCollector callback;
	private MethodInfo m_info;
	
	int nLocalVars;

	public MethodCollector(MethodVisitor mv, int access, String name, String desc, String signature, 
			String [] exceptions, AnnotatedMethodCollector callback) {
		super(Opcodes.ASM4,mv);
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
		
		m_info = new MethodInfo(this.access, this.name, this.desc, this.signature, this.exceptions);
		
		this.params = Type.getArgumentTypes(this.desc);
		this.n_params = params.length;
		this.annotated_parameters = new HashSet<Integer>();
		
		this.callback = callback;
		
		this.hasAnnotations = false;
		this.nLocalVars = 0;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		if(desc.equals(Constants.TASK_ANNOTATION) || desc.equals(Constants.ATOMIC_ANNOTATION))
		{
			hasAnnotations = true;
			this.m_info.addAnnotation(desc);
		}

		return super.visitAnnotation(desc, visible);
	}
	
	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		
		if(desc.equals(Constants.CALLBYVALUE_ANNOTATION))
		{
			hasAnnotations = true;
			annotated_parameters.add(parameter);
			this.m_info.addAnnotation(desc);
		}
		
		return super.visitParameterAnnotation(parameter, desc, visible);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
	{
		if(hasAnnotations)
		{
			int i = index - 1; //Index = 0 -> this (se o método não for estático)

			if(i < n_params && i >= 0)
			{
				boolean isAnnotated = false;
				if(this.annotated_parameters.contains(i))
					isAnnotated = true;
				m_info.addParam(index, name, desc, isAnnotated);
			}

			this.nLocalVars++;
		}

		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitEnd()
	{
		if(hasAnnotations)
		{
//			m_info.setMethodAnnotation(method_annotated);
//			m_info.setParameterAnnotation(!annotated_parameters.isEmpty());
			m_info.setNumberOfLocalVars(nLocalVars);
			callback.addMethodInfo(this.name, this.desc, m_info);
		}
		super.visitEnd();
	}
}

