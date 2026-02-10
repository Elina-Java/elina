package elina;

import static naming.ServiceRegistry.registryID;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import core.CLoader;
import core.MultipleFutures;
import core.Place;
import core.PrimitiveTypesMap;
import core.Utils;
import core.communication.CommunicationModule;
import core.communication.LocalRemoteTask;
import core.communication.Message;
import core.communication.MessageTag;
import core.communication.Node;
import core.communication.RemoteOperation;
import core.communication.ResultRemoteOperation;
import core.init.Configuration;
import core.init.ConfigurationException;
import core.remote.ConsoleIn;
import core.remote.ConsoleOut;
import core.remote.IRemoteClassLoader;
import core.remote.IRemoteInConsole;
import core.remote.IRemoteOutConsole;
import core.remote.RemoteClassLoader;
import core.scheduling.ApplicationScheduler;
import core.scheduling.SchedulingInfo;
import core.taskManager.InvokeHandlerTask;
import core.taskManager.TaskManagerModule;
import drivers.Adapters;
import drivers.Logger;
import naming.Registry;
import service.ActiveService;
import service.Application;
import service.DummyApplication;
import service.IFuture;
import service.IService;
import service.NoSuchMethodException;
import service.Service;
import service.ServiceStub;
import service.Task;

/**
 * 
 * Classe que fornece suporte para todas as interacções necessárias para a
 * execução de tarefas em paralelo. Algumas das operações são delegadas para os
 * drivers no qual o middleware faz uso.
 * 
 * <br/>
 * 
 * @author Diogo Mourão <br>
 * @author João Saramago
 */

public final class Elina {

	static final String ELINA_SERVICE_NAME = "/ElinaServer";
	
	
	public static Logger logger;

	public static boolean DEBUG = false;

	public static Registry registry;

	/**
	 * Mapping from client to running applications
	 */
	private static Map<UUID, Application> applications = new HashMap<UUID, Application>();

	private static Map<UUID, List<Message<Object[]>>> tasksWainting = new HashMap<UUID, List<Message<Object[]>>>();

	private static Place place;

	private static CommunicationModule comm;
	private static ApplicationScheduler sch;
	private static TaskManagerModule taskManager;

	/**
	 * Default constructor
	 */
	Elina() {
		this(Configuration.DefaultConfigurationFile);
	}

	/**
	 * Constructor with a specific configuration file
	 * 
	 * @param configurationFile
	 *            The path for the configuration file
	 */
	Elina(String configurationFile) {
		try {
			Configuration conf = new Configuration(configurationFile);

			// Init drivers
			Adapters.selectdrivers(conf);
	
			// Get logger
			logger = Adapters.getLogger();
			
			// Locate registry
			try {
				registry = (Registry) Naming.lookup(registryID);
			} catch (MalformedURLException e) {
				logger.warning(Elina.class, "Registry not found: " + e.getLocalizedMessage());
			} catch (RemoteException e) {
				logger.warning(Elina.class, "Registry not found: " + e.getLocalizedMessage());
			} catch (NotBoundException e) {
				logger.warning(Elina.class, "Registry not found: " + e.getLocalizedMessage());
			}
			
			// Init modules
			comm = new CommunicationModule(conf);
			sch = new ApplicationScheduler(comm.getNodes());
			taskManager = new TaskManagerModule();
			
			place = new Place();

		} catch (SocketException e) {
			logger.fatal(Elina.class, e.getLocalizedMessage());
			// e.printStackTrace();
			System.exit(1);
		} catch (ConfigurationException e) {
			System.err.println("Configuration file verification failed: " + e.getLocalizedMessage());
			logger.fatal(Elina.class, "Configuration file verification failed: " + e.getLocalizedMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error reading configuration file: " + e.getLocalizedMessage());
			logger.fatal(Elina.class, "Error reading configuration file: " + e.getLocalizedMessage());
			System.exit(1);
		}
		
		logger.info(Elina.class, "Middleware initiated");

	}

	/**
	 * Método responsável por encerrar a plataforma.
	 */
	public void shutdown() {

		for (Application a : applications.values()) {
			a.cancel();
		}

		// TODO - shutdown ao middleware
		taskManager.shutdown();
		System.gc();
	}

	public static <T> T copy(T elem) {
		return Adapters.getCloningDriver().copy(elem);
	}

	@SuppressWarnings("rawtypes")
	public static void shutdown(UUID id) {

		Application app = applications.remove(id);
		if (app != null) {
			app.cancel();

			SchedulingInfo info = app.getSchedulingInfo();
			if (info != null) {
				Set<Node> nodes = info.getNodes();
				for (Node i : nodes) {
					if (!i.isLocal())
						comm.sendMessage(id, i, null, MessageTag.SHUTDOWN_APP);
				}
			}

			System.gc();
		}
		logger.info(Elina.class, "Middleware terminated");
	}

	public static UUID addRemoteTask(IFuture<?> spawn, UUID clientid) {
		applications.get(clientid).addRemoteTask(spawn);
		return spawn.getID();
	}

	public static IFuture<?> getRemoteTask(UUID clientID, UUID id) {
		return applications.get(clientID).getRemoteTask(id);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void invokeService(Message<Object[]> msg, Node client, Application app) {

		Object[] aux = (Object[]) msg.getContent();

		Thread.currentThread().setContextClassLoader(
				CLoader.getClassLoader((UUID) aux[0]));

		Task<Object> t = null;
		try {
			t = (InvokeHandlerTask<Object>) Utils.toObject((byte[]) aux[1], Thread
					.currentThread().getContextClassLoader());
			((InvokeHandlerTask<Object>) t).unboxTypes();
		} catch (Exception e) {
			try {
				t = (Task<Object>) Utils.toObject((byte[]) aux[1], Thread
						.currentThread().getContextClassLoader());
			} catch (EOFException e1) {
				e1.printStackTrace();
			}
		}

		final IService s = app.getService(t.getService().getID());

		LocalRemoteTask<Object> task = new LocalRemoteTask<Object>(t, s,
				msg.getSendingObject(), client);

		// this.send(new
		// Message<UUID>(MessageTag.REMOTE_FUTURE,Middleware.addRemoteTask(p.spawn(task),
		// p.getClientId()),this.getClient(socketChannel)));

		Message<UUID> tosend = new Message<UUID>(MessageTag.REMOTE_FUTURE,
				Elina.addRemoteTask(place.spawn(task), s.getClientId()),
				task.getID(), client);
		tosend.setDestination_object(msg.getSendingObject());

		comm.send(tosend);
	}

	public static void registerOutConsole(UUID id,
			IRemoteOutConsole remoteConsole) {
		((ConsoleOut) System.out).registerRemoteConsole(id, remoteConsole);
	}

	public static void registerErrConsole(UUID id,
			IRemoteOutConsole remoteConsole) {
		((ConsoleOut) System.err).registerRemoteConsole(id, remoteConsole);
	}

	public static void registerInConsole(UUID id, IRemoteInConsole remoteConsole) {
		((ConsoleIn) System.in).registerRemoteConsole(id, remoteConsole);
	}

	public static void addClassLoader(UUID clientID,
			IRemoteClassLoader remoteClassLoader, String[] classPath)
			throws IOException {
		CLoader.registerClassLoader(clientID, new RemoteClassLoader(classPath,
				remoteClassLoader));
	}

	public static IFuture<Void> deploy(Application app) throws IOException {

		CLoader.registerClassLoader(
				app.getAppID(),
				new RemoteClassLoader(app.getClassPath(), app
						.getRemoteClassLoader()));

		Thread.currentThread().setContextClassLoader(
				CLoader.getClassLoader(app.getAppID()));

		for (IService p : app.getServices()) {
			((Service) p).detectAffinity();
		}

		List<IFuture<Void>> mains = new ArrayList<IFuture<Void>>();

		SchedulingInfo info = sch.schedule(app);

		if (DEBUG)
			logger.debug(Elina.class, "Scheduling " + info);

		if (info == null) {
			Elina.registerErrConsole(app.getAppID(), app.getErrConsole());
			Elina.registerInConsole(app.getAppID(), app.getInConsole());
			Elina.registerOutConsole(app.getAppID(), app.getOutConsole());

			mains.add(Elina.startApp(app));
		} else {
			info.finish();
			System.setProperty("toSend", "true");
			for (@SuppressWarnings("rawtypes")
			Node n : info.getNodes()) {

				Application a = Application.newInstance(app.getAppID());// clone(app);//new
																		// Application(app.getAppID());
				a.setSchedulingInfo(info);
				for (Service p : info.getSchedule(n)) {
					a.addService(Elina.clone(p), false);
				}
				for (ServiceStub p : info.getStubSchedule(n)) {
					a.addService(Elina.clone(p), false);
				}

				a.setErrConsole(app.getErrConsole());
				a.setInConsole(app.getInConsole());
				a.setOutConsole(app.getOutConsole());
				a.setRemoteClassLoader(app.getRemoteClassLoader());

				if (!n.isLocal()) {
					mains.add(comm.sendApplication(a, n));
				} else {
					Elina.registerErrConsole(app.getAppID(),
							app.getErrConsole());
					Elina.registerInConsole(app.getAppID(),
							app.getInConsole());
					Elina.registerOutConsole(app.getAppID(),
							app.getOutConsole());

					mains.add(Elina.startApp(a));

					List<Message<Object[]>> list = tasksWainting.remove(a
							.getAppID());
					if (list != null) {
						for (Message<Object[]> message : list) {
							invokeService(message, message.getSending_host(), a);
						}
					}

				}
			}
			System.setProperty("toSend", "false");
		}

		return new MultipleFutures<Void>(mains);
	}

	@SuppressWarnings("unchecked")
	public static <R> R clone(R obj) {
		try {
			return (R) Utils.toObject(Utils.toByteArray(obj), Thread
					.currentThread().getContextClassLoader());
		} catch (EOFException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static void startApp(Application a, final Node n, final UUID oid) {
		applications.put(a.getAppID(), a);

		Service service = null;

		Elina.registerErrConsole(a.getAppID(), a.getErrConsole());
		Elina.registerInConsole(a.getAppID(), a.getInConsole());
		Elina.registerOutConsole(a.getAppID(), a.getOutConsole());

		final List<ActiveService> as = new ArrayList<ActiveService>();
		final List<IFuture<Void>> mains = new ArrayList<IFuture<Void>>();
		for (IService p : a.getServices()) {
			if (p instanceof ActiveService) {
				if (service == null)
					service = (Service) p;
				as.add((ActiveService) p);
			} else if (p instanceof Service) {
				((Service) p).init();
				if (service == null)
					service = (Service) p;
			}
		}

		final Service aux = service;

		for (ActiveService activeService : as) {
			activeService.init();
		}

		Task<Void> task = new Task<Void>() {
			private static final long serialVersionUID = 1L;

			public Void call() throws Exception {
				for (ActiveService activeService : as) {
					try {
						IFuture<Void> aux = activeService.invoke("run", null);
						mains.add(aux);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}

				for (IFuture<Void> future : mains) {
					future.get();
				}

				Message<Void> sms = new Message<Void>(MessageTag.APP_END, null,
						null);
				sms.setDestination_host(n);
				sms.setDestination_object(oid);

				comm.send(sms);
				return null;
			}

			public IService getService() {
				return aux;
			}

			public void setService(IService service) {

			}
		};

		place.spawn(task);

	}

	private static IFuture<Void> startApp(Application a) {
		applications.put(a.getAppID(), a);

		List<ActiveService> as = new ArrayList<ActiveService>();
		List<IFuture<Void>> mains = new ArrayList<IFuture<Void>>();
		for (IService p : a.getServices()) {
			if (p instanceof ActiveService) {
				as.add((ActiveService) p);
			} else if (p instanceof Service) {
				((Service) p).init();
			}
		}

		for (ActiveService activeService : as) {
			activeService.init();
			try {
				if (!activeService.getClass().getName()
						.startsWith("service.Application$")
						|| activeService instanceof DummyApplication) {
					IFuture<Void> aux = activeService.invoke("run", null);
					mains.add(aux);
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		return new MultipleFutures<Void>(mains);
	}

	public static IService getService(UUID clientId, UUID id) {
		return applications.get(clientId).getService(id);
	}

	public static void addAplication(Application app) {
		applications.put(app.getClientId(), app);

	}

	/*
	 * public static int getNWorkers() { return conf.getNworkers(); }
	 */

	/*
	 * @SuppressWarnings("rawtypes") public static Node getNode() { return
	 * comm.getLocalNode(); }
	 */

	public static CommunicationModule getCommunicationModule() {
		return comm;
	}

	public static Place getPlace() {
		return place;
	}

	// Incomming Message handling

	public static <C> void processMessage(Message<C> msg) {
		switch (msg.getType()) {
		case APPLICATION: {
			Object[] aux = (Object[]) msg.getContent();

			try {
				CLoader.registerClassLoader((UUID) aux[0],
						new RemoteClassLoader((String[]) aux[1],
								(IRemoteClassLoader) aux[2]));
			} catch (IOException e1) {
				// TODO; send exception to client
				e1.printStackTrace();
			}

			// CLoader.registerThread(Thread.currentThread(), (UUID) aux[0]);
			Thread.currentThread().setContextClassLoader(
					CLoader.getClassLoader((UUID) aux[0]));

			Application a = null;
			try {
				a = (Application) Utils.toObject((byte[]) aux[1], Thread
						.currentThread().getContextClassLoader());
			} catch (EOFException e) {
				e.printStackTrace();
			}
			Elina.startApp(a, msg.getSending_host(),
					msg.getSendingObject());

			List<Message<Object[]>> list = tasksWainting.remove(a.getAppID());
			if (list != null) {
				for (Message<Object[]> message : list) {
					invokeService(message, message.getSending_host(), a);
				}
			}

			break;
		}
		case ADDTASK: {

			Object[] aux = (Object[]) msg.getContent();

			// ClassLoader cl=CLoader.getClassLoader((UUID)aux[0]);
			Application app = applications.get((UUID) aux[0]);

			if (app == null) {
				List<Message<Object[]>> list = tasksWainting.get((UUID) aux[0]);
				if (list == null)
					list = new ArrayList<Message<Object[]>>();

				list.add((Message<Object[]>) msg);

				tasksWainting.put((UUID) aux[0], list);
			} else {
				invokeService((Message<Object[]>) msg, msg.getSending_host(),
						app);
			}

			break;
		}
		case SHUTDOWN_APP:
			UUID id=(UUID)msg.getContent();
			Application app=applications.remove(id);
			app.cancel();
			
			
			System.gc();
			System.gc();
			System.gc();
			System.gc();
			break;

		case FUTURE_OP:
			try {
				RemoteOperation op = (RemoteOperation) msg.getContent();

				IFuture<?> f = Elina.getRemoteTask(op.getClientID(), op.getId());

				Class<?>[] parametersType = new Class<?>[op.getArgs().length];
				for (int i = 0; i < parametersType.length; i++) {
					if (PrimitiveTypesMap.contains(op.getArgs()[i].getClass())) {
						parametersType[i] = PrimitiveTypesMap
								.get(op.getArgs()[i].getClass());
					} else {
						parametersType[i] = op.getArgs()[i].getClass();
					}
				}

				Method m = f.getClass().getMethod(op.getOp(), parametersType);
				Object o = m.invoke(f, op.getArgs());

				Message<ResultRemoteOperation> send = new Message<ResultRemoteOperation>(
						MessageTag.OP_RESULT, new ResultRemoteOperation(
								op.getOp(), o), null, msg.getSending_host());
				send.setDestination_object(msg.getSendingObject());
				comm.send(send);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		default:
			break;
		}

	}
}
