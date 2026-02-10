package core;


import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;



public class CLoader extends ClassLoader {


	private static Map<UUID,ClassLoader> classLoaders = new ConcurrentHashMap<UUID, ClassLoader>();
	
	private static Map<Thread,UUID> threadsIds = new ConcurrentHashMap<Thread, UUID>();
	
	public static void registerClassLoader(UUID id,ClassLoader clas){
		
		classLoaders.put(id, clas);
		
	}
	
	public static void  registerThread(Thread t,UUID id){
		threadsIds.put(t, id);
		
	}
	
	public static ClassLoader getClassLoader(Thread t){
		UUID aux=threadsIds.get(t);
		if(aux==null)
			return null;
		return classLoaders.get(aux);
	}
	
	public CLoader(ClassLoader parent) {
		super(parent);
	}

	
	/**
	 * Every request for a class passes through this method. If the class is in
	 * com.journaldev package, we will use this classloader or else delegate the
	 * request to parent classloader.
	 * 
	 * 
	 * @param name
	 *            Full class name
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			return super.loadClass(name);
		} catch (ClassNotFoundException e) {
			//return getClass(name);
			try{
				return classLoaders.get(threadsIds.get(Thread.currentThread())).loadClass(name);
			}catch (NullPointerException ex) {
				return super.loadClass(name);
			}
		}
	}

	public static void unregisterClassLoader(UUID id) {
		
		classLoaders.remove(id);
		
		
		
		for (Entry<Thread, UUID> entry : threadsIds.entrySet()) {
			if(entry.getValue().equals(id)){
				threadsIds.remove(entry.getKey());
			}
		}
		
	}

	public Class<?> load(UUID clientid, byte[] bytecode) {
		// FIXME:
		class Mycloader extends ClassLoader {

			public Mycloader(ClassLoader classLoader) {
				super(classLoader);
			}

			public Class<?> load(byte[] bytecode) {
				return super.defineClass(null,bytecode, 0, bytecode.length);
			}

		}
		
		
		Mycloader a = new Mycloader(classLoaders.get(clientid));
		return a.load(bytecode);
	}

	public static String threadStatus() {
		return threadsIds.toString();
	}

	public static ClassLoader getClassLoader(UUID clientId) {
		return classLoaders.get(clientId);
	}

}
