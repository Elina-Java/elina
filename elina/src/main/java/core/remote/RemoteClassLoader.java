package core.remote;

import static elina.Elina.logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import elina.Elina;

/**
 * Class loader that loads classes from a given class path and a client JVM
 * 
 * @author Joao Saramago, Herve Paulino
 *
 */
public class RemoteClassLoader extends URLClassLoader {

	/**
	 * The stub for the classloader of the client JVM
	 */
	private IRemoteClassLoader remoteClassLoader;
	

	/**
	 * Constructs a new RemoteClassLoader given the local classpath and a client JVM's classloader
	 * @param classPath Array of local folders and jars from where classes can be loaded
	 * @param remoteClassLoaderStub The stub for the classloader of the client JVM 
	 * @throws IOException If any of the given paths cannot be accessed
	 */
	public RemoteClassLoader(String[] classPath, IRemoteClassLoader remoteClassLoaderStub) throws IOException {
		super(new URL[0], ClassLoader.getSystemClassLoader());
		if (classPath != null)
			processClassPath(classPath);
		this.remoteClassLoader = remoteClassLoaderStub;
	}
	
	/**
	 * Processes the given array of local folders and jars from where classes can be loaded
	 * @param classPath The array
	 * @throws IOException If the file cannot be accessed
	 */
	private void processClassPath(String[] classPath) throws IOException {
		String baseDir = System.getProperty("user.dir");
		for (String path : classPath) {
			File f = new File(baseDir + File.separator + path);
			if (!f.canRead()) 
				throw new java.io.IOException("Cannot access " + f);
			else 
				addURL(new URL("file://" + f.getCanonicalPath()));		
		}
		if (Elina.DEBUG) {
			logger.debug(RemoteClassLoader.class, "Classpath:");
			for (URL u : super.getURLs())
				logger.debug(RemoteClassLoader.class,"\t" + u);
		}
	}

	
	/**
	 * Overrides findClass in URLClassLoader
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] b;
		try{
			return super.findClass(name);
		}
		catch (Exception e) {
			try {
				return ClassLoader.getSystemClassLoader().loadClass(name);
			}
			catch  (Exception e2) {
				b = remoteClassLoader.loadClass(name);
				if (b != null) 
					return defineClass(name, b, 0, b.length);
				else
					throw new ClassNotFoundException(name);
			}
		}
	}

}
