package core.remote.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

public class UUIDClientSocketFactory implements RMIClientSocketFactory,
		Serializable {

	private static final long serialVersionUID = 1L;

	public UUIDClientSocketFactory(){
	}
	
	
	public Socket createSocket(String host, int port) throws IOException {
		return new UUIDSocket(host,port);
		
	}

}
