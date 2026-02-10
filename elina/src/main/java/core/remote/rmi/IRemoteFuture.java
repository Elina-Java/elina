package core.remote.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface IRemoteFuture<R> extends Remote {

	public boolean cancel(boolean mayInterruptIfRunning) throws RemoteException;
	public R get() throws RemoteException;
	public R get(long timeout, TimeUnit unit) throws RemoteException;
	public boolean isCancelled() throws RemoteException;
	public boolean isDone() throws RemoteException;
	public UUID getID() throws RemoteException;
	
}
