package core.communication;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Node<R> implements Externalizable {

	private static final long serialVersionUID = 1L;

	
	
	private R node;
	private String addr;
	private int port;
	private boolean isLocal;
	private int rank;

	
	
	public Node() { }
	
	public Node(String addr, int port) {
		this.addr = addr;
		this.port = port;
		this.isLocal = false;
	}

	public R getNode() {
		return this.node;
	}

	public void setNode(R node) {
		this.node = node;
	}

	public String getAddr() {
		return this.addr;
	}

	public int getPort() {
		return this.port;
	}

	public void setLocal() {
		this.isLocal = true;
	}

	public boolean isLocal() {
		return this.isLocal;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return this.rank;
	}
	

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(node);
		out.writeUTF(addr);
		out.writeInt(port);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		node = (R) in.readObject();
		addr = in.readUTF();
		port = in.readInt();

	}

}
