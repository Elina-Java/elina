package service.aggregator;


import java.util.Random;

import service.IFuture;
import service.IService;
import service.NoSuchMethodException;
import service.Service;

public class DistRedService extends Service {

	private static final long serialVersionUID = 1L;
	protected IService[] services;
	
	public DistRedService(IService[] services){
		this.services=services;
	}
	
	
	/*public <R> IFuture<R> invoke(String methodName, Object[] args,Class<?>[] types) throws NoSuchMethodException{
		Random r = new Random();
		return services[r.nextInt(services.length)].invoke(methodName, args,types);
	}*/
	
	@Override
	public <R> IFuture<R> invoke(String methodName, Object[] args)
			throws NoSuchMethodException {
		Random r = new Random();
		return services[r.nextInt(services.length)].invoke(methodName, args);
	}

	public IService[] getServices() {
		return this.services;
	}
	

}
