
package elina;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import core.Utils;
import core.init.Configuration;
import core.remote.ClientUUIDFactory;
import core.remote.ConsoleIn;
import core.remote.ConsoleOut;
import core.remote.IRemoteClassLoader;
import core.remote.IRemoteInConsole;
import core.remote.IRemoteOutConsole;
import core.remote.InConsoleStub;
import core.remote.OutConsoleStub;
import core.remote.RemoteClassLoaderStub;
import core.remote.RemoteRead;
import core.remote.rmi.IServerInterface;
import service.ActiveService;
import service.Application;
import service.IFuture;
import service.IService;
import service.RemoteException;

public class ApplicationLauncher {

	private static final String RUN = "run";

	private static IServerInterface server;
	
//	private static Elina middleware;
	@SuppressWarnings("unused")
//	private static List<IFuture<Void>> activeMains;
	protected static remoteclass classLoader = new remoteclass();
	protected static MyOutConsole out = new MyOutConsole(System.out);
	protected static MyOutConsole err = new MyOutConsole(System.err);
	protected static Thread tout;

	static {
		System.getProperties().put("java.security.policy", "policy.all");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		Thread tcl = new Thread(classLoader);
		tcl.setDaemon(true);
		tcl.start();

		tout = new Thread(out);
		tout.setDaemon(true);
		tout.start();

		Thread terr = new Thread(err);
		terr.setDaemon(true);
		terr.start();

	}

	
	public static void connect(String server) throws MalformedURLException, RemoteException {
		
		System.setProperty("isMiddleware", "false");
		System.setProperty("toSend","true");
		
		try {
			ApplicationLauncher.server = (IServerInterface) Naming.lookup("//" + server + "/Launcher");
		} catch (java.rmi.RemoteException e) {
			throw new RemoteException(e.getLocalizedMessage());
		} catch (NotBoundException e) {
			throw new RemoteException(e.getLocalizedMessage());
		}
		
	}

	/**
	 * M��todo que inicia o suporte de execu����o. Assume que todos os servi��os ter��o
	 * a sua pr��pria pool de workers.
	 */
	public static void init() {
		init(Configuration.DefaultConfigurationFile);
	}


	public static void init(String confFileName) {
		System.setOut(new ConsoleOut(System.out));
		System.setErr(new ConsoleOut(System.err));
		System.setIn(new ConsoleIn(System.in));

	//	middleware = 
		new Elina(confFileName);
	//	activeMains = new LinkedList<IFuture<Void>>();

	//	if(Middleware.DEBUG)
	//		PropertyConfigurator.configure("log4j.properties");
		
	}
	
	
	/**
	 * M��todo que encerra o ambiente de execu����o.
	 * 
	 * @throws RemoteException
	 */
	public static void stop(Application i) throws RemoteException {
		if (server != null) {
			try {
				server.shutdown(ClientUUIDFactory.getUUID());
				tout.interrupt();
			} catch (java.rmi.RemoteException e) {
				e.printStackTrace();
				throw new RemoteException(e.getLocalizedMessage());
			}
		}
		System.exit(0);
	}

	/**
	 * Deploy an application 
	 * @param app The application
	 * @throws RemoteException 
	 */
	public static void deploy(Application app) throws RemoteException {
		if (server != null) {
			try {		
				app.setOutConsole(new OutConsoleStub(out));
				app.setErrConsole(new OutConsoleStub(err));
				app.setInConsole(new InConsoleStub(new MyInConsole(System.in)));
				app.setRemoteClassLoader(new RemoteClassLoaderStub(classLoader));
									
				server.addClassLoader(ClientUUIDFactory.getUUID(), app.getRemoteClassLoader(), app.getClassPath());
				server.deploy(ClientUUIDFactory.getUUID(),Utils.toByteArray(app)).get();
			} catch (java.rmi.RemoteException e) {
				e.printStackTrace();
				throw new RemoteException(e.getLocalizedMessage());
			}
		}
		else {
			Elina.addAplication(app);
					
		//	for (IService p : app.getServices()) 
		//		((Service) p).init();
	
			List<IFuture<Object>> futures = new ArrayList<IFuture<Object>>();
			
			for (IService p : app.getServices()) {
				if(p instanceof ActiveService)
					//((ActiveService)p).run();
					futures.add(p.invoke(RUN, null));
			}
			
			for (IFuture<Object> iFuture : futures) 
				iFuture.get();
		}
	}
}

class MyInConsole implements IRemoteInConsole, Runnable {

	transient private InputStream i;
	private String ip;
	private ServerSocket server;
	private int port;

	protected MyInConsole(InputStream i) {
		this.i = i;
		ip = System.getProperty("java.rmi.server.hostname");
		try {
			this.port = 7362;
			server = new ServerSocket(this.port);
			port = server.getLocalPort();
			server.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int read() throws IOException {
		return i.read();
	}

	public RemoteRead read(int off, int len) throws java.rmi.RemoteException,
			IOException {
		byte[] aux = new byte[off + len];
		int size = i.read(aux, off, len);
		return new RemoteRead(aux, size);
	}

	@Override
	public String getIP() {
		return this.ip;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public void run() {
		Socket aux;
		while (true) {
			try {
				aux = server.accept();
				DataInputStream dis = new DataInputStream(aux.getInputStream());
				ObjectOutputStream dos = new ObjectOutputStream(
						aux.getOutputStream());
				int op = dis.readInt();
				switch (op) {
				case 1:
					dos.writeInt(this.read());
					break;
				case 2:
					int off = dis.readInt();
					int len = dis.readInt();
					dos.writeObject(this.read(off, len));
					break;
				}
				dos.flush();

				dos.close();
				dis.close();
				aux.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

class MyOutConsole implements IRemoteOutConsole, Runnable {

	transient private PrintStream p;
	private String ip;
	// private ServerSocket server;
	transient private ServerSocketChannel server;
	private int port;
	transient private Selector selector;
	transient private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

	public MyOutConsole(PrintStream p) {
		this.p = p;
		ip = System.getProperty("java.rmi.server.hostname");
		try {

			Selector socketSelector = SelectorProvider.provider()
					.openSelector();

			server = ServerSocketChannel.open();
			server.socket().bind(null);
			server.configureBlocking(false);
			this.port = server.socket().getLocalPort();

			server.register(socketSelector, SelectionKey.OP_ACCEPT);
			this.selector = socketSelector;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void print(String s) {
		p.print(s);
	}

	@Override
	public String getIP() {
		return this.ip;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public void run() {
		while (true) {
			try {
				// Wait for an event one of the registered channels
				this.selector.select(10000);

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector
						.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}

					// Check what event is available and deal with it
					if (key.isAcceptable()) {
						this.accept(key);
					} else if (key.isReadable()) {
						this.read(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// Clear out our read buffer so it's ready for new data
		this.readBuffer.clear();

		// Attempt to read off the channel
		int numRead;
		try {
			numRead = socketChannel.read(this.readBuffer);
		} catch (IOException e) {
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			key.channel().close();
			key.cancel();
			return;
		}

		this.print(new String(this.readBuffer.array(),0,numRead));
		
		this.readBuffer.position(0);

	}

	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket
		// channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
				.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		//Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(this.selector, SelectionKey.OP_READ);

	}

}

class remoteclass implements IRemoteClassLoader, Runnable {

	private String ip;
	private ServerSocket server;
	private int port;

	public remoteclass() {
		ip = System.getProperty("java.rmi.server.hostname");
		try {
			server = new ServerSocket(0);
			port = server.getLocalPort();
			server.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] loadClass(String name) {
		return readClassFile(name);
	}

	private byte[] readClassFile(String classs) {
		try {
			String name = classs.replace('.', File.separatorChar) + ".class";

			InputStream stream = ClassLoader.getSystemClassLoader()
					.getResourceAsStream(name);

			if(stream==null)
				return null;
			
			int size = stream.available();
			byte buff[] = new byte[size];
			DataInputStream in = new DataInputStream(stream);
			in.readFully(buff);
			in.close();
			return buff;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void run() {
		Socket aux;
		while (true) {
			try {
				aux = server.accept();
				DataOutputStream dos = new DataOutputStream(
						aux.getOutputStream());
				DataInputStream dis = new DataInputStream(aux.getInputStream());

				byte[] data = loadClass(dis.readUTF());

				if(data==null){
					dos.writeInt(-1);
				}else{
					dos.writeInt(data.length);
					dos.flush();
					dos.write(data);
				}
				dos.flush();
				dos.close();
				dis.close();
				aux.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
