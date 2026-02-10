package core.remote.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import core.remote.IRemoteClassLoader;


public interface IServerInterface extends Remote{

	
	
	public IRemoteFuture<Void> deploy(UUID id, byte[] a) throws RemoteException; 
	
	
	public void addClassLoader(UUID clientID, IRemoteClassLoader remoteClassLoader, String[] classPath) throws RemoteException;
		
	
	
	public void shutdown(UUID id) throws RemoteException;
}
