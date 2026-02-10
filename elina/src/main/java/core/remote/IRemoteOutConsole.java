package core.remote;

import service.RemoteException;



public interface IRemoteOutConsole{

	void print(String s)throws RemoteException;

	String getIP();

	int getPort();
	
}
