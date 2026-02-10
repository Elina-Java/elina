package core.remote.rmi;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoaderSpi;

import core.CLoader;


public class RMICLoader extends RMIClassLoaderSpi {

	@SuppressWarnings("restriction")
	public Class<?> loadClass(String codebase, String name,
			ClassLoader defaultLoader) throws MalformedURLException,
			ClassNotFoundException {
		Class<?> aux = sun.rmi.server.LoaderHandler.loadClass(codebase, name,CLoader.getClassLoader(Thread.currentThread()));
		return aux;
	}

	@SuppressWarnings("restriction")
	public Class<?> loadProxyClass(String codebase, String[] interfaces,
			ClassLoader defaultLoader) throws MalformedURLException,
			ClassNotFoundException {
		return sun.rmi.server.LoaderHandler.loadProxyClass(codebase,
				interfaces, CLoader.getClassLoader(Thread.currentThread()));
	}

	public ClassLoader getClassLoader(String codebase)
			throws MalformedURLException {
		return CLoader.getClassLoader(Thread.currentThread());
	}

	@SuppressWarnings("restriction")
	public String getClassAnnotation(Class<?> cl) {
		return sun.rmi.server.LoaderHandler.getClassAnnotation(cl);
	}

}
