package service.aggregator;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import core.Level;
import service.IFuture;
import service.IReconfigurable;
import service.IService;
import service.NoSuchMethodException;
import service.Service;

/**
 * Class the implements the base behavior for a pool of Elina services
 * 
 * @author Joao Saramago
 *
 */
public abstract class ServicePool<S extends IService> extends Service implements Serializable, IReconfigurablePool {

	
	private static final long serialVersionUID = 1L;

	/**
	 * The service pool
	 */
	private List<S> services;
	private int countReplacements;
	
	/**
	 * The scheduling algorithm
	 */
	private IServiceScheduler scheduler;
	
	
	/**
	 * Construct a pool with a given set of services and scheduling policy
	 * 
	 * @param services The service set
	 * @param scheduler The scheduling policy
	 */
	public ServicePool(S[] services, IServiceScheduler scheduler){
		super();
		this.level = Level.Cluster;
		this.services = new ArrayList<S>();
		for (S service : services)
			this.services.add(service);
		
		this.scheduler	= scheduler;
		this.scheduler.setSize(services.length);
		this.countReplacements = 0;
	}

	/**
	 * @see service.Service#invoke(java.lang.String, java.lang.Object[], java.lang.Class[])
	 */
	@Override
	public <R> IFuture<R> invoke(String methodName, Object[] args,Class<?>[] types) throws NoSuchMethodException{
		int i = scheduler.getIndex();		
		return services.get(i).invoke(methodName, args,types);
	}
	
	
	/**
	 * @see service.Service#invoke(java.lang.String, java.lang.Object[])
	 */
	@Override
	public <R> IFuture<R> invoke(String methodName, Object[] args) throws NoSuchMethodException{
		int i = scheduler.getIndex();		
		return services.get(i).invoke(methodName, args);
	}

/*
	public <T, R> IFuture<R> distReduce(Distribution<?>[] distr, Reduction<R> red,
			String methodName, int index, Object... args) throws NoSuchMethodException {
		int i=scheduler.getIndex();
		return services[i].distReduce(distr, red, methodName, index, args);
	}
	*/
	
	/**
	 * @see service.Service#cancel()
	 */
	@Override
	public void cancel() {
		for (IService i : this.services) {
			i.cancel();
		}	
	}

	/**
	 * Return the services that compose the pool
	 * @return An array containg the services
	 */
	public List<S> getServices() {
		return this.services;
	}

	// Methods from IReconfigurable interface

	/**
	 * @see service.IReconfigurable#replaceFromSystem()
	 */
	@Override
	public void replaceFromSystem() {
		((IReconfigurable) this.services.get(countReplacements++%services.size())).replaceFromSystem();
	}

	/**
	 * @see service.IReconfigurable#replace(java.net.URL)
	 */
	@Override
	public void replace(URL url) {
		((IReconfigurable) this.services.get(countReplacements++%services.size())).replace(url);		
	}

	/**
	 * @see service.IReconfigurable#replace(service.IReconfigurable)
	 */
	@Override
	public <R> void replace(R newProvider) {
		((IReconfigurable) this.services.get(countReplacements++%services.size())).replace(newProvider);
	}
	
	/**
	 * @see service.IReconfigurable#pause()
	 */
	@Override
	public void pause() {
		for(S s : this.services){
			((IReconfigurable) s).pause();
		}
	}
	
	/**
	 * @see service.IReconfigurable#resume()
	 */
	@Override
	public void resume() {
		for(S s : this.services){
			((IReconfigurable) s).resume();
		}	
	}
	
	/**
	 * @see service.IReconfigurable#add(service.IReconfigurable)
	 */
	@Override
	public <R> void add(R provider) {	
		this.services.add((S) ((Service) provider).createStub());
	    countReplacements++;
	    this.scheduler.setSize(this.services.size());
	}
	
	/**
	 * @see service.IReconfigurable#remove(service.IReconfigurable)
	 */
	@Override
	public <R> void remove(R provider) {
		this.services.remove(provider);
		countReplacements--;
	    this.scheduler.setSize(this.services.size());
	}
	
	public <R> void removeAll(R provider) {
		//TODO: remove all of the same type as provider
	    this.scheduler.setSize(this.services.size());
	}
}
