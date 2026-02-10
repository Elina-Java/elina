package core.remote.rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

public class UUIDServerSocketFactory implements RMIServerSocketFactory {

	
	

	public ServerSocket createServerSocket(int port) throws IOException {
		return new UUIDServerSocket(port);
	}

}
