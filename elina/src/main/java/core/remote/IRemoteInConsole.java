package core.remote;

import java.io.IOException;

import service.RemoteException;

public interface IRemoteInConsole  {

	int read() throws IOException,RemoteException;
	RemoteRead read(int off, int len) throws IOException,RemoteException;
	String getIP();
	int getPort();
	
}
