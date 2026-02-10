package naming;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import core.init.Configuration;
import core.init.ConfigurationException;
import drivers.Adapters;
import drivers.Logger;
import service.IService;
import service.ServiceStub;


public class ServiceRegistry implements Registry {

	/** 
	 * RMI Identifier
	 */
	public static final String registryID = "/Registry";
	
	/**
	 * The logger
	 */
	private static Logger logger;  
	
	/**
	 * The registry map
	 */
	private final HashMap<String, ServiceStub> registry = new HashMap<>();
	
	
	ServiceRegistry() throws ConfigurationException, IOException {
		this(Configuration.DefaultConfigurationFile);
	}
	
	ServiceRegistry(String configurationFile) throws ConfigurationException, IOException {
		super();
		Adapters.selectdrivers(new Configuration(configurationFile));
		logger = Adapters.getLogger();
	}
	
	@Override
	public <T extends ServiceStub> void register(String id, T resource) {
		logger.info(ServiceRegistry.class, "Register " + id + ":" + resource);
		this.registry.put(id, resource);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IService> T lookup(String id) {
		logger.info(ServiceRegistry.class, "Lookup " + id + ":" + ((T) this.registry.get(id)));
		return (T) this.registry.get(id);
	}
	
	public static void main(String[] args) throws URISyntaxException {
	
		System.setProperty("isMiddleware", "true");
		System.setProperty("toSend", "false");

		logger.debug(ServiceRegistry.class,"Starting registry");
		
		
		System.getProperties().put("java.security.policy", "policy.all");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try { // start rmiregistry
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			// registry previously launched
			logger.debug(ServiceRegistry.class, e.getLocalizedMessage());
		}

		try {
			Registry registry = (Registry) UnicastRemoteObject.exportObject(new ServiceRegistry());
			
			Naming.rebind(registryID, registry);
			System.out.println("Registry bound");
		} catch (Exception e) {
			logger.fatal(ServiceRegistry.class, e);
		}

	}

}
