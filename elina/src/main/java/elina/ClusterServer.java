package elina;

import static elina.Elina.logger;

import java.io.EOFException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import core.CLoader;
import core.Utils;
import core.remote.ConsoleIn;
import core.remote.ConsoleOut;
import core.remote.IRemoteClassLoader;
import core.remote.rmi.IRemoteFuture;
import core.remote.rmi.IServerInterface;
import core.remote.rmi.RMIFuture;
import core.remote.rmi.UUIDClientSocketFactory;
import core.remote.rmi.UUIDServerSocketFactory;
import service.Application;
import service.IFuture;

/**
 * 
 * Classe Launcher é responsável por iniciar o ambiente de execução, inicializar
 * as localidades que estarão presentes na computação que serão executadas em
 * cima do ambiente de execução e o encerramento do ambiente de execução.
 * 
 * @author Diogo Mourão
 * @author João Saramago
 */

public final class ClusterServer implements IServerInterface {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	
	
	
	//private static Middleware middleware;

	public ClusterServer() throws RemoteException {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Método que inicia o suporte de execução. Assume que todos os services terão
	 * a sua própria pool de workers.
	 */
	public static void init() {
		new Elina();
	}



	public static void main(String[] args) {
				
		System.setProperty("isMiddleware", "true");
		System.setProperty("toSend", "false");
		
		System.setOut(new ConsoleOut(System.out));
		System.setErr(new ConsoleOut(System.err));
		System.setIn(new ConsoleIn(System.in));
		
		System.getProperties().put("java.security.policy", "policy.all");
		if (System.getSecurityManager() == null) 
			System.setSecurityManager(new SecurityManager());

		try { // start rmiregistry
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			// do nothing – already started with “rmiregistry”
		}
		
		ClusterServer.init();

		try {
			ClusterServer launch = new ClusterServer();
			IServerInterface stub = (IServerInterface) UnicastRemoteObject.exportObject(launch, 0, new UUIDClientSocketFactory(),new UUIDServerSocketFactory());
			
			Naming.rebind(Elina.ELINA_SERVICE_NAME, stub);
			System.out.println("Launcher bound");
		} catch (Exception e) {
			System.err.println("Launcher exception:");
			e.printStackTrace();
		}

	}

	
	public void addClassLoader(UUID clientID,IRemoteClassLoader remoteClassLoader, String[] classPath) throws RemoteException{
		if(Elina.DEBUG)
			logger.debug(ClusterServer.class, "Add ClassLoader [Client ID "+clientID+"] - classPath: " + classPath);
		
		try {
			Elina.addClassLoader(clientID, remoteClassLoader, classPath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RemoteException(e.getLocalizedMessage());
		}
	}

	
	public void shutdown(UUID id) throws RemoteException {
		
		Elina.shutdown(id);
		
//		CLoader.unregisterClassLoader(id);
//		((ConsoleIn)System.in).unregisterRemoteConsole(id);
//		((ConsoleOut)System.err).unregisterRemoteConsole(id);
//		((ConsoleOut)System.out).unregisterRemoteConsole(id);
		
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
	}



	
	public IRemoteFuture<Void> deploy(UUID id, byte[] a) throws RemoteException {
		if(Elina.DEBUG)
			logger.debug(ClusterServer.class, "Deploy [Client ID "+id+"]");
		
		Thread.currentThread().setContextClassLoader(CLoader.getClassLoader(id));
		Application app = null;
		try {
			app = (Application) Utils.toObject(a,Thread.currentThread().getContextClassLoader());
		} catch (EOFException e) {
			e.printStackTrace();
		}
		
		IFuture<Void> future;
		try {
			future = Elina.deploy(app);
		} catch (IOException e) {
			throw new RemoteException(e.getLocalizedMessage());
		}
		return new RMIFuture<Void>(future);
	}
	
//	public IRemoteFuture<Void> deploy(Application app) throws RemoteException {
//		return new RMIFuture<Void>(Middleware.deploy(app));
//	}

}
