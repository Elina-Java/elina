package core.remote.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

import core.remote.ClientUUIDFactory;




public class UUIDSocket extends Socket {

	
	private InputStream in = null; 
    private OutputStream out = null;
	
	public UUIDSocket(String host, int port) throws UnknownHostException,
			IOException {
		super(host, port);
	}

	public UUIDSocket() {
		super();
	}

	@Override
	public synchronized InputStream getInputStream() throws IOException {
		
		if(this.in!=null)
			return this.in;
		
		InputStream in = super.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(in);
		try {
			UUID id = (UUID) ois.readObject();
			this.in=new UUIDInputStream(in, id);
			return this.in;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public synchronized OutputStream getOutputStream() throws IOException {
		if(out!=null)
			return out;
		
		
		OutputStream out = super.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);

		oos.writeObject(ClientUUIDFactory.getUUID());
		this.out=new UUIDOutputStream(out, ClientUUIDFactory.getUUID());
		return this.out;
	}

}
