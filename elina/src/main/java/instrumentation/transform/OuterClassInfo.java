package instrumentation.transform;

public class OuterClassInfo {
	
	private int outerCAccess;
	private String outerCName;
	private String outerCSign;
	
	private String outerMName;
	private String outerMDesc;
	private String outerMSignature;
	private String [] outerMExceptions;
	
	public OuterClassInfo(int outerCAccess, String outerCName, String outerCSign,
			String outerMName, String outerMDesc,
			String outerMSignature, String[] outerMExceptions) {
	
		this.outerCAccess = outerCAccess;
		this.outerCName = outerCName;
		this.outerCSign = outerCSign;
		
		this.outerMName = outerMName;
		this.outerMDesc = outerMDesc;
		this.outerMSignature = outerMSignature;
		this.outerMExceptions = outerMExceptions;
	}

	public String getOuterCSign() {
		return outerCSign;
	}

	public int getOuterCAccess() {
		return outerCAccess;
	}

	public String getOuterCName() {
		return outerCName;
	}

	public String getOuterMName() {
		return outerMName;
	}

	public String getOuterMDesc() {
		return outerMDesc;
	}

	public String getOuterMSignature() {
		return outerMSignature;
	}

	public String[] getOuterMExceptions() {
		return outerMExceptions;
	}
	
	@Override
	public String toString()
	{
		return outerCAccess+", "+outerCName+", "+outerMName+","+outerMDesc+", "+outerMSignature;
	}
}

