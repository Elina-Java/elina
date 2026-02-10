package core.remote.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import service.IFuture;



public class RMIFuture<R> extends UnicastRemoteObject implements IRemoteFuture<R>{

	private static final long serialVersionUID = 1L;
	private IFuture<R> future;
	
	
	public RMIFuture(IFuture<R> future) throws RemoteException{
		this.future=future;
	}


	
	
		
	public boolean cancel(boolean mayInterruptIfRunning) throws RemoteException {
		return future.cancel(mayInterruptIfRunning);
	}


	
	public R get() throws RemoteException {
		return future.get();
	}


	
	public R get(long timeout, TimeUnit unit) throws RemoteException {
		return future.get(timeout, unit);
	}


	
	public boolean isCancelled() throws RemoteException {
		return future.isCancelled();
	}


	
	public boolean isDone() throws RemoteException {
		return future.isDone();
	}




	
	public UUID getID() throws RemoteException {
		return future.getID();
	}

}
