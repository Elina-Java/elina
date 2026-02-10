package instrumentation.transform;

import org.objectweb.asm.Type;

public class MethodParam {
	private int index;
	private String name;
	private Type type;
	private boolean isAnnotated;
	
	public MethodParam(int index, String name, Type type, boolean isAnnotated)
	{
		this.index = index;
		this.name = name;
		this.type = type;
		this.isAnnotated = isAnnotated;
	}

	public int getIndex()
	{
		return index;
	}
	
	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}
	
	public boolean isAnnotated()
	{
		return this.isAnnotated;
	}
	
	public String toString()
	{
		return "Param: "+name+", Type: "+type+", "+index+", "+isAnnotated;
	}
}

