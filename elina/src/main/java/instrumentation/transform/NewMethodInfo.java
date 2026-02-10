package instrumentation.transform;

public class NewMethodInfo {

	private int access;
	private String name;
	private String desc;
	private String signature;
	
	
	public NewMethodInfo(int access, String name, String desc, String signature)
	{
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
	}


	public int getAccess() {
		return access;
	}


	public String getName() {
		return name;
	}


	public String getDesc() {
		return desc;
	}


	public String getSignature() {
		return signature;
	}
	
	
}
