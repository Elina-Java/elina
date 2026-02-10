package core.remote.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import core.Level;
import core.collective.Distribution;
import core.collective.DistributionsCombination;
import service.NoSuchMethodException;
import service.SOMDTask;
import service.Service;





public class RMIService extends UnicastRemoteObject implements IRemoteService {

	private Service s;
	
	public RMIService(Service s) throws RemoteException {
		super();
		this.s=s;
	}

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public <R> IRemoteFuture<R> invoke(String methodName, Object... args)
			throws NoSuchMethodException, RemoteException {
		return (RMIFuture<R>)new RMIFuture<Object>(s.invoke(methodName, args));
	}

	
	public <R> void dist(Distribution<?>[] distribution, SOMDTask<R> task, DistributionsCombination combs)
			throws RemoteException {
		this.s.dist(task, combs.getDistributions());
		
	}

	
	public UUID getClientId() throws RemoteException {
		return this.s.getClientId();
	}

	
	public UUID getID() throws RemoteException {
		return this.s.getID();
	}

	@Override
	public void cancel() throws RemoteException {
		s.cancel();
		
	}

	@Override
	public Level getLevel() {
		return this.s.getLevel();
	}


	/*@Override
	public <T, R> IRemoteFuture<R> distReduce(Distribution<?>[] distr,
			Reduction<R> red, String methodName, int index, Object... args)
			throws NoSuchMethodException, RemoteException {
		return new RMIFuture<R>(s.distReduce(distr, red, methodName, index, args));
	}
*/
	

}
