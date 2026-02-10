package service;

import static elina.Elina.logger;

import java.io.ObjectStreamException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import core.Level;
import core.Place;
import core.collective.Distribution;
import core.collective.DistributionsCombination;
import core.collective.Reduction;
import core.collective.SOMDExecutor;
import core.communication.CommunicationModule;
import core.communication.ServiceIdentifier;
import core.remote.ClientUUIDFactory;
import core.taskManager.InvokeHandlerTask;
import drivers.Adapters;
import drivers.TaskExecutorDriver;
import elina.Elina;
import elina.utils.StubLoader;

/**
 * Abstract class that defines the core functionalities of a Elina service
 *  
 * @author Diogo Mourão
 * @author João Saramago
 * @author Hervé Paulino
 *  
 */

public abstract class Service implements IService {

	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Service unique identifier 
	 */
	private final UUID id;
	
	
	private final UUID clientid;
	
	
	/**
	 * List of services whose affinity relationship with the current service that have been explicitly specified 
	 */
	private final List<UUID> exAffinities = new ArrayList<UUID>();
	
	
	/**
	 * List of services whose affinity relationship with the current service that have been implicitly inferred 
	 */
	private final List<UUID> imAffinities = new ArrayList<UUID>();
	
	/**
	 * The level on which the service is running
	 */
	protected Level level;
	
	/**
	 * The task executor adapter
	 */
	private transient TaskExecutorDriver taskExecutor;
	
	/**
	 * The SOMD executor
	 */
	private transient SOMDExecutor somdExecutor = new SOMDExecutor();
	
	/**
	 * Place where the service is running
	 */
	protected transient Place place;


	// Constructors
	
	/**
	 * Service constructor with given identifier
	 * @param id
	 */
	public Service(UUID id) {
		this.id = UUID.randomUUID();
		this.clientid = id;
		this.level = Level.Node;
		this.taskExecutor = Adapters.getTaskExecutor();
		this.place = (Place) Elina.getPlace();
	}

	/**
	 * Service constructor with automatically generated identifier
	 */
	public Service() {
		this(ClientUUIDFactory.getUUID());
	}
	
	
	// Methods
	
	/** 
	 * Start service
	 */
	public void init() {
		this.place = (Place) Elina.getPlace();
		exploreFields(this.getClass());
	}
	
	

	protected void sync() {
		// purposely blank
	}


	@Override
	public <R> IFuture<R> invoke(String methodName, Object[] args) throws NoSuchMethodException {
		if(args==null)
			args = new Object[]{};
		
		InvokeHandlerTask<R> task = new InvokeHandlerTask<R>(this, methodName, args);

		if (Elina.DEBUG)
			logger.debug(Service.class, "Submit task [" + task + "]");
				
		return this.taskExecutor.execute(task);
	}

	@Override
	public <R> IFuture<R> invoke(String methodName, Object[] args, Class<?>[] types) throws NoSuchMethodException{
		
		if(args==null)
			args=new Object[]{};
		if(types==null)
			types=new Class<?>[]{};

		InvokeHandlerTask<R> task = new InvokeHandlerTask<R>(this, methodName, types, args);
		
		if (Elina.DEBUG)
			logger.debug(Service.class,"Submit task [" + task + "]");
		
		return this.taskExecutor.execute(task);
	}

	
	//TODO: list of pairs
	
		
	protected <T> T copy(T elem) {
		// return this.middleware.copy(elem);
		return Elina.copy(elem);
	}

	
	// SOMD Execution
	
	
	/*public <T, R> IFuture<R> distReduce(Distribution<?>[] distr, Reduction<R> red,
			String methodName, int index, Object... args) throws NoSuchMethodException {

		//TODO
		return (IFuture<R>) this.somdExecutor.getResult(distr, red, methodName, args, index, this);
	}*/
	
	public <R> void dist(SOMDTask<R> task, Distribution<?>... distr) {
		this.somdExecutor.getResult(distr, null, task, null, this.level);
	}
	

	/**
	 * 
	 * Execute a task under to the SOMD execution model
	 * 
	 * @param task - Task to execute
	 * @param red - Reduction to apply
	 * @param comb - Combinations
	 * @param distr - Distributions to apply to the multiple partitionable arguments
	 * @return
	 */
	public <R> IFuture<R> distReduce(SOMDTask<R> task, Reduction<R> red, DistributionsCombination comb, Distribution<?>... distr) {
		task.setService(this);
		return this.somdExecutor.getResult(distr, red, task, comb, this.level);
	}

	/**
	 * 
	 * Execute a task under to the SOMD execution model
	 * 
	 * @param task - Task to execute
	 * @param red - Reduction to apply
	 * @param distr - Distributions to apply to the multiple partitionable arguments
	 * @return
	 */
	public <R> IFuture<R> distReduce(SOMDTask<R> task, Reduction<R> red, Distribution<?>... distr) {
		task.setService(this);
		return this.somdExecutor.getResult(distr, red, task, null, this.level);
	}
	
	
	public ServiceStub createStub() {
		try {
					
			String stubclassName = this.getClass().getCanonicalName() + "Stub";
			Class<?> cc = null;
			try {
				cc = Thread.currentThread().getContextClassLoader().loadClass(stubclassName);//ClassLoader.getSystemClassLoader().loadClass(this.getClass().getCanonicalName() + "Stub");
			} catch (NullPointerException e) {
				cc = new StubLoader().loadClass(stubclassName, true);
			}
			
			Constructor<?> cons = cc.getConstructor(id.getClass(), clientid.getClass(), this.level.getClass());
			
			ServiceStub stub =  (ServiceStub) cons.newInstance(id, clientid, this.level);
			stub.setService(this);
			return stub;
		} catch (ClassNotFoundException | 
				InvocationTargetException | 
				InstantiationException | 
				java.lang.NoSuchMethodException |
				SecurityException |
				IllegalAccessException e) {
			throw new RuntimeException(e.getMessage());
		} 
	}
	
	public ServiceStub createStub(WebServiceStubGenerator wssg){
		try {
			Class<?> cc = Thread.currentThread().getContextClassLoader().
					loadClass(this.getClass().getCanonicalName() + "Stub");//ClassLoader.getSystemClassLoader().loadClass(this.getClass().getCanonicalName() + "Stub");
			Constructor<?> cons = cc.getConstructor(id.getClass(), clientid.getClass(), this.level.getClass(), wssg.getClass().getInterfaces()[0]);
			
			ServiceStub stub =  (ServiceStub) cons.newInstance(id, clientid, this.level, wssg);
			stub.setService(this);
			return stub;
		} catch (ClassNotFoundException | 
				InvocationTargetException | 
				InstantiationException | 
				java.lang.NoSuchMethodException |
				SecurityException |
				IllegalAccessException e) {
			throw new RuntimeException(e.getMessage());
		} 
	}

	public UUID getClientId() {
		return this.clientid;
	}

	public void cancel() {
		taskExecutor.cancelTasks(this.clientid);
	}

	public UUID getID() {
		return this.id;
	}

	public void setAffinity(IService p) {
		int me=this.hashCode();
		int other=p.hashCode();
		if(me<other)
			this.exAffinities.add(p.getID());
		else
			p.setAffinity(this);
	}

	public List<UUID> getAffinity() {
		return this.exAffinities;
	}

	public List<UUID> getImpAffinity() {
		return this.imAffinities;
	}

	public void setImpAffinity(IService p) {
		int me=this.hashCode();
		int other=p.hashCode();
		if(me<other)
			this.imAffinities.add(p.getID());
		else
			p.setImpAffinity(this);
	}

	public void detectAffinity() {
		Class<? extends Service> c = this.getClass();
		for (Field f : c.getDeclaredFields()) {
			try {
				f.setAccessible(true);
				Object o = f.get(this);

				if (o instanceof Service) {
					Service p = (Service) o;
					if (!this.exAffinities.contains(p)
							&& !this.imAffinities.contains(p)) {
						this.setImpAffinity(p);
					}
					// p.detectAffinity();
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}
	
	public Object writeReplace() throws ObjectStreamException{
		if(System.getProperty("isMiddleware").equals("true") && System.getProperty("toSend").equals("false"))
			return new ServiceIdentifier(id, clientid,this.getClass().getCanonicalName());
		else
			return this;
	}
	
	/**
	 * Registry
	 */
	
	
	public final void register() throws RemoteException {		
		try {
			ServiceStub stub= createStub();
			stub.setLocation(CommunicationModule.getLocalNode());
			Elina.registry.register(this.getClass().getName(), stub);
		} catch(NullPointerException e) {
			throw new RemoteException("Registry not found.");
		}
		catch (java.rmi.RemoteException e) {
			throw new RemoteException(e.getLocalizedMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends IService> T lookup(String service) throws RemoteException {
		try {
			return (T) Elina.registry.lookup(service);
		} catch (java.rmi.RemoteException e) {
			throw new RemoteException(e.getLocalizedMessage());
		}
	}	
	
	@Override
	public Level getLevel() {
		return this.level;
	}
	
	public int getNumberOfWorkers() {
		return taskExecutor.getNumberOfWorkers();
	}
	
	
	// Private Methods
	
	/**
	 * Collect the service closure of a class by recursively exploring its fields 
	 * 
	 * @param clazz The class to explore
	 */
	private void exploreFields(Class<? extends Service> clazz) {
		for (Field f : clazz.getDeclaredFields()) {
			try {
				f.setAccessible(true);
				Object o = f.get(this);

				if (o instanceof Service) {
					UUID id = ((IService) o).getID();
					IService p = Elina.getService(this.getClientId(), id);

					f.set(this, f.getType().cast(p));
				}
				if (o instanceof Service[] || o instanceof IService[]) {
					IService[] orig = (IService[]) o;
					IService[] aux = new IService[orig.length];
					for (int i = 0; i < aux.length; i++) {
						UUID id = orig[i].getID();
						aux[i] = Elina.getService(this.getClientId(), id);
					}
					f.set(this, aux);

				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			try {
				exploreFields(superClass.asSubclass(Service.class));
			} catch (ClassCastException e) {
				// ignored
			}
		}
	}
}
