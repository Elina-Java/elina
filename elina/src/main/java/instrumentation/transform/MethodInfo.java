package instrumentation.transform;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Type;

import instrumentation.Constants;

public class MethodInfo {

	private int access;
	private String name;
	private String desc;
	private String signature;
	private String [] exceptions;

	private List<MethodParam> params;
	private int nLocals;
	private Set<String> annotations;
	
	private int lastIndex;
	
//	private boolean has_method_annotation;
//	private boolean has_parameter_annotation;

	private String getFutureName;
	private String getFutureDesc;
	private String getFutureSign;

	public MethodInfo(int access, String name, String desc, String signature, String [] exceptions)
	{
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
		this.params = new LinkedList<MethodParam>();
		this.annotations = new HashSet<String>();

		this.getFutureName = "get_future_"+name;
		String args_desc = desc.substring(0, desc.lastIndexOf(")")+1 );
		this.getFutureDesc = args_desc + Constants.IFUTURE;

		String type = "<"+this.getTypeDesc(Type.getReturnType(desc))+">;";
		this.getFutureSign = args_desc+Constants.IFUTURE.substring(0,Constants.IFUTURE.length()-1)+type;
	}

	public String[] getExceptions() {
		return exceptions;
	}

	public int getNLocals()
	{
		return nLocals;
	}

	public int getAccess() {
		return access;
	}

	
	public String getSignature() {
		return signature;
	}

	public String getGetFutureName() {
		return getFutureName;
	}

	public String getGetFutureDesc() {
		return getFutureDesc;
	}

	public String getGetFutureSign() {
		return getFutureSign;
	}
	
	public void addAnnotation(String annotation)
	{
		this.annotations.add(annotation);
	}
	
	public Set<String> getAnnotations()
	{
		return annotations;
	}

	public void addParam(int index, String name, String desc, boolean isAnnotated)
	{
		this.params.add(new MethodParam(index, name, Type.getType(desc), isAnnotated));
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public List<MethodParam> getParams() {
		return params;
	}

	public Type getReturnType()
	{
		return Type.getReturnType(this.desc);
	}

	public String toString()
	{
		return name+","+desc;
	}

	public void setNumberOfLocalVars(int nLocalVars)
	{
		this.nLocals = nLocalVars;
	}

//	public void setMethodAnnotation(boolean hasMethodAnnotation)
//	{
//		this.has_method_annotation = hasMethodAnnotation;
//	}
//	
//	public void setParameterAnnotation(boolean hasParameterAnnotation)
//	{
//		this.has_parameter_annotation = hasParameterAnnotation;
//	}
//	
//	public boolean hasMethodAnnotation()
//	{
//		return this.has_method_annotation;
//	}
//	
//	public boolean hasParameterAnnotation()
//	{
//		return this.has_parameter_annotation;
//	}
	
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

	public void setLastIndex(int i) {
		this.lastIndex=i;
	}
	
	public int getLastIndex() {
		return this.lastIndex;
	}
}

