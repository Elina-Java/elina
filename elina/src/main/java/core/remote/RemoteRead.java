package core.remote;

import java.io.Serializable;

public class RemoteRead implements Serializable{

	private static final long serialVersionUID = 1L;

	private byte[] b;
	private int size;
	
	public RemoteRead(byte[] aux, int size) {
		this.b=aux;
		this.size=size;
	}

	public int getSize() {
		return size;
	}

	public void copyto(byte[] b,int offset) {
		System.arraycopy(this.b, 0, b, offset, this.size);
		
	}

}
