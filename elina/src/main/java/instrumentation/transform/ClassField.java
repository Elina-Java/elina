package instrumentation.transform;

import org.objectweb.asm.Opcodes;

public class ClassField {
	private int opcode;
	private String owner;
	private String name;
	private String desc;
	private String sign;
	
	private NewMethodInfo getter;
	private NewMethodInfo setter;
	
	private boolean hasGetter;
	private boolean hasSetter;
	
	public ClassField(int opcode, String owner, String name, String desc, String sign) {

		this.opcode = opcode;
		this.owner = owner;
		this.name = name;
		this.desc = desc;
		this.sign = sign;
		this.getter = null;
		this.setter = null;
		this.hasGetter = false;
		this.hasSetter = false;
	}
	
	public int getOpcode() {
		return opcode;
	}
	public String getOwner() {
		return owner;
	}
	public String getName() {
		return name;
	}
	public String getDesc() {
		return desc;
	}
	
	public String toString()
	{
		return name;
	}
		
	public NewMethodInfo getGetter()
	{
		if(this.getter == null)
		{
			this.hasGetter = true;
			String signature = null;
			if(this.sign != null)
				signature = "()"+sign;
			this.getter = new NewMethodInfo(Opcodes.ACC_PUBLIC, "get_"+name, "()"+desc, signature);
		}
		return this.getter;
	}
	
	public NewMethodInfo getSetter()
	{
		if(this.setter == null)
		{
			this.hasSetter = true;
			String signature = null;
			if(this.sign != null)
				signature = "("+sign+")V";
			this.setter = new NewMethodInfo(Opcodes.ACC_PUBLIC, "set_"+name, "("+desc+")V", signature);
		}
		return this.setter;
	}

	public boolean hasGetter() {
		return hasGetter;
	}

	public boolean hasSetter() {
		return hasSetter;
	}
}

