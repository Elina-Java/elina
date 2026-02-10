package core.communication;

import java.io.Serializable;

public class ResultRemoteOperation implements Serializable{

	private static final long serialVersionUID = 1L;
	private String op;
	private Object result;
	
	public ResultRemoteOperation(String op, Object result) {
		this.op=op;
		this.result=result;
				
	}

	public String getOp() {
		return op;
	}

	public Object getResult() {
		return result;
	}

}
