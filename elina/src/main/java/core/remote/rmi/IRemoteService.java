package core.remote.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import core.Level;
import service.NoSuchMethodException;




public interface IRemoteService extends Remote {
		
	<R> IRemoteFuture<R> invoke(String methodName, Object ...args) throws NoSuchMethodException,RemoteException;

//	<T, R> IRemoteFuture<R> distReduce(Distribution<?>[] distr, Reduction<R> red, String methodName, int index ,Object ...args) throws NoSuchMethodException,RemoteException;

	UUID getClientId()throws RemoteException;

	UUID getID()throws RemoteException;

	void cancel() throws RemoteException;

	Level getLevel();
	
}
