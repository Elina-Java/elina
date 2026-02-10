package service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import core.CLoader;
import core.remote.ClientUUIDFactory;
import core.remote.ConsoleIn;
import core.remote.ConsoleOut;
import core.remote.IRemoteClassLoader;
import core.remote.IRemoteInConsole;
import core.remote.IRemoteOutConsole;
import core.scheduling.SchedulingInfo;
import service.aggregator.DistRedService;
import service.aggregator.ServicePool;

public abstract class Application extends ActiveService {

	private static final long serialVersionUID = 1L;
	private UUID app_id;
	private Map<UUID, IService> services = new HashMap<UUID, IService>();
	private List<IFuture<?>> remoteFutures = new ArrayList<IFuture<?>>();
	private SchedulingInfo sch;
	private ArrayList<String> classPath = new ArrayList<String>();

	private IRemoteOutConsole system_out;
	private IRemoteOutConsole system_err;
	private IRemoteInConsole system_in;

	private IRemoteClassLoader classLoader;

	protected Application(UUID id) {
		this.app_id = id;
		this.addService(this);
	}
	
	protected Application() {
		this(ClientUUIDFactory.getUUID());
	}

	public void addService(IService service) {
		this.addService(service, true);
	}

	public void addService(IService service, boolean expand) {
	
		this.services.put(service.getID(), service);

		if (expand) {
			if (service instanceof ServicePool) {
				ServicePool pool = (ServicePool) service;
				for (IService p : ((List<IService>) pool.getServices())) {
					this.addService(p);
				}
			}
			if (service instanceof DistRedService) {
				DistRedService mapRed = (DistRedService) service;
				for (IService p : mapRed.getServices()) {
					this.addService(p);
				}
			}
		}
		// }
	}

	public void addToClassPath(String path) {
		this.classPath.add(path);
	}
	
	
	public String[] getClassPath() {
		int size = this.classPath.size();
		return size == 0 ? 
				null : 
				this.classPath.toArray(new String[size]);
	}
	
	
	
	public void setRemoteClassLoader(IRemoteClassLoader rcl) {
		this.classLoader = rcl;
	}

	public void setOutConsole(IRemoteOutConsole myOutConsole) {
		this.system_out = myOutConsole;
	}

	public void setErrConsole(IRemoteOutConsole myErrConsole) {
		this.system_err = myErrConsole;
	}

	public void setInConsole(IRemoteInConsole myInConsole) {
		this.system_in = myInConsole;
	}

	public IRemoteOutConsole getErrConsole() {
		return this.system_err;
	}

	public UUID getAppID() {
		return this.app_id;
	}

	public IRemoteOutConsole getOutConsole() {
		return this.system_out;
	}

	public IRemoteInConsole getInConsole() {
		return this.system_in;
	}

	public Collection<IService> getServices() {
		return this.services.values();
	}

	public IRemoteClassLoader getRemoteClassLoader() {
		return this.classLoader;
	}

	public void cancel() {
		for (IService p : this.services.values()) {
			if (p instanceof DummyApplication)
				continue;
			if (p instanceof Service)
				((Service) p).cancel();
		}

		CLoader.unregisterClassLoader(this.app_id);
		((ConsoleOut) System.out).unregisterRemoteConsole(this.app_id);
		((ConsoleOut) System.err).unregisterRemoteConsole(this.app_id);
		((ConsoleIn) System.in).unregisterRemoteConsole(this.app_id);

	}

	public void addRemoteTask(IFuture<?> spawn) {
		this.remoteFutures.add(spawn);
	}

	public IFuture<?> getRemoteTask(UUID id) {
		for (IFuture<?> f : this.remoteFutures) {
			if (f.getID().equals(id))
				return f;
		}
		return null;
	}

	public IService getService(UUID id) {
		
		
		return this.services.get(id);
	}

	public void setSchedulingInfo(SchedulingInfo info) {
		this.sch = info;
	}

	public SchedulingInfo getSchedulingInfo() {
		return this.sch;
	}

	public static Application newInstance() {
		return new DummyApplication(ClientUUIDFactory.getUUID()) ;
	}

	public static Application newInstance(UUID appID) {
		return  new DummyApplication(appID) ;
		
	}

	

}
