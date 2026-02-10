package core.remote;

import java.rmi.RemoteException;
import java.util.UUID;

import core.Level;
import core.remote.rmi.IRemoteService;
import service.IFuture;
import service.IService;
import service.NoSuchMethodException;


public class RemoteServiceWrapper implements IService {

	private static final long serialVersionUID = 1L;
	private IRemoteService service;
	
	public RemoteServiceWrapper(IRemoteService service) {
		this.service=service;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R> IFuture<R> invoke(String methodName, Object[] args) throws NoSuchMethodException {
		try {
			return (IFuture<R>)new RemoteFutureWrapper<Object>(this.service.invoke(methodName, args));
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <R> IFuture<R> invoke(String methodName, Object[] args,Class<?>[] types) throws NoSuchMethodException{
		try {
			return (IFuture<R>)new RemoteFutureWrapper<Object>(this.service.invoke(methodName, args,types));
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public UUID getClientId() {
		try {
			return this.service.getClientId();
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public UUID getID() {
		try {
			return this.service.getID();
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public void cancel() {
		try {
			this.service.cancel();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}


	@Override
	public Level getLevel() {
		return this.service.getLevel();
	}


	@Override
	public void setAffinity(IService p) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setImpAffinity(IService p) {
		// TODO Auto-generated method stub
		
	}

/*	@Override
	public <T, R> IFuture<R> distReduce(Distribution<?>[] distr,
			Reduction<R> red, String methodName, int index, Object... args)
			throws NoSuchMethodException {
		try {
			return new RemoteFutureWrapper<R>(this.service.distReduce(distr, red, methodName, index, args));
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}*/
	

}
