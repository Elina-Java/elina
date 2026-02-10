package core.remote.rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UUIDServerSocket extends ServerSocket {

	public UUIDServerSocket(int port) throws IOException {
		super(port);
	}
	
	@Override
	public Socket accept() throws IOException {
		Socket s = new UUIDSocket();
		super.implAccept(s); 
		return s;
	}

}
