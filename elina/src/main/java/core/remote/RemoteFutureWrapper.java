package core.remote;

import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import core.remote.rmi.IRemoteFuture;
import service.IFuture;


public class RemoteFutureWrapper<R> implements IFuture<R> {

	private IRemoteFuture<R> future; 
	
	public RemoteFutureWrapper(IRemoteFuture<R> spawn) {
		this.future=spawn;
	}

	
	public boolean cancel(boolean mayInterruptIfRunning) {
		try {
			return this.future.cancel(mayInterruptIfRunning);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}

	
	public R get() {
		try {
			return this.future.get();
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}


	public R get(long timeout, TimeUnit unit) {
		try {
			return this.future.get(timeout, unit);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}


	public boolean isCancelled() {
		try {
			return this.future.isCancelled();
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}


	public boolean isDone() {
		try {
			return this.future.isDone();
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}


	public UUID getID() {
		try {
			return this.future.getID();
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

}
