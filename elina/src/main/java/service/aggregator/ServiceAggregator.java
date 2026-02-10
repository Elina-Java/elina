package service.aggregator;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import elina.Elina;
import service.IService;
import service.Service;

/**
 * 
 * @author João Saramago
 * @since Dec 6, 2011 4:06:29 PM
 * 
 */
public class ServiceAggregator {

	
	@SuppressWarnings("unchecked")
	private static <I extends IService> I[] getServices(Class<? extends I> service, int number) {
		I[] out = (I[]) Array.newInstance(service, number);
		for (int i = 0; i < out.length; i++) {
			try {
				out[i] = (I) service.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
        }
		return (I[]) out;
	}
	
	private static Constructor<?> getClass(String name,Class<?>[] types){
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		try {
			Class<?> a = cl.loadClass(name);
			Constructor<?> c = a.getConstructor(types);
			//Constructor<?> c = a.getConstructors()[0];
			return c;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <I extends IService, T extends IService> I distRed(T service, int number) {
		I[] out = (I[]) Array.newInstance(service.getClass(), number);
		for (int i = 1; i < out.length; i++) {
			out[i] = (I) Elina.clone(service);
		}
		out[0]=(I)service;
		
		return distRed(out);
	}
	
	public static <I extends IService, T extends IService> I distRed(
			Class<T> service, int number) {
		@SuppressWarnings("unchecked")
		I[] aux = getServices((Class<I>)service, number);
		return distRed(aux);
	}

	/**
	 * Aggregate services that implement a Distribution/Reduction annotated
	 * interface (I). All the interface's methods must be annotated.
	 * 
	 * @param services
	 *            The list of services to aggregate
	 * @return A service of type I
	 */
	@SuppressWarnings("unchecked")
	public static <I extends IService> I distRed(I[] services) {

		Class<?> aux = services[0].getClass();
		try {
			Service out=(Service)getClass(aux.getCanonicalName() + "DistRed",new Class[]{IService[].class}).newInstance(new Object[]{services});
			for (I i : services) {
				out.setImpAffinity((Service)i);
			}
			
			out.setAffinity((IService)services[0]);
			
			return (I) out;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <I extends IService> I servicePool(Class<? extends I> service,int number) {
		I[] aux = getServices(service, number);
		return servicePool(aux);
	}
	
	public static <I extends IService> I servicePool(IServiceScheduler s,Class<? extends I> service, int number) {
		I[] aux = getServices(service, number);
		return servicePool(s, (I[]) aux);
	}

	public static <I extends IService> I servicePool(I[] services) {
		return servicePool(new RoundRobinScheduler(), services);
	}

	@SuppressWarnings("unchecked")
	public static <I extends IService> I servicePool(IServiceScheduler s, I[] services) {
			
		Class<?> aux = null;
		for (Class<?> c : services[0].getClass().getInterfaces()) {
	//		System.out.println(" --- " + c);
			aux = getServiceInterface(c);
	//		System.out.println(" --- " + aux);
			if (aux != null)
				break;
		}
		try {
			Service out = (Service) getClass(
					aux.getCanonicalName() + "Pool",
					new Class[]{IService[].class,IServiceScheduler.class}).newInstance(services, s);
			
			for (I i : services) 	
				out.setImpAffinity((IService)i);
			out.setAffinity((IService)services[0]);
			
			return (I) out;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static <K, V, I extends Service & PartitionedMem<K, V>> I partMem(IPartitioner<K,V> p,Class<? extends I> service,int number) {
		I[] aux = getServices(service, number);
		return partMem(p,aux);
	}
	
	public static <K, V, I extends Service & PartitionedMem<K, V>> I partMem(Class<? extends I> service,int number) {
		I[] aux = getServices(service, number);
		return partMem(aux);
	}
	
	public static <K, V, I extends Service & PartitionedMem<K, V>> I partMem(I[] services) {
		return partMem(new DefaultPartitioner<K,V>(), services);
	}
	

	@SuppressWarnings("unchecked")
	public static <K, V, I extends Service & PartitionedMem<K, V>> I partMem(IPartitioner<K,V> p, I[] services) {
		Class<?> aux = null;
		for (Class<?> c : services[0].getClass().getInterfaces()) {
			if (Service.class.isAssignableFrom(c)) {
				aux = c;
				break;
			}
		}
		try {
			Service out=(Service)getClass(aux.getCanonicalName() + "PartMem",new Class[]{AbstractPartitionedMemService[].class,IPartitioner.class}).newInstance(services, p);
			for (I i : services) {
				out.setImpAffinity((Service)i);
			}
			
			out.setAffinity((IService)services[0]);
			
			return (I) out;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <I extends IService, J extends IService> Facade<I, J> facade(
			I p1, J p2) {
		return new Facade(p1, p2);
	}
	
	/** 
	 * Retrieve the given class' (or interface's) base service interface,
	 * if the class (or interface) implements (or extends) a service interface
	 * 
	 * @param clazz The class (or interface) to inspect
	 *  
	 * @return The implemented service interface or null, if no service interface is implemented
	 */
	private static Class<?> getServiceInterface(Class<?> clazz) {
		
		for (Class<?> interf : clazz.getInterfaces()) {
			// The interface is itself a base service interface
			if (interf.equals(IService.class)) 
				return clazz;	
		}
			
		for (Class<?> interf : clazz.getInterfaces()) {
			// Class (or interface) implements (or extends) an interface that is a service interface
			if (IService.class.isAssignableFrom(interf))
				return getServiceInterface(interf);
		}
		
		return null;
	}

}
