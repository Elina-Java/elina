package core.taskManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import core.PrimitiveTypesMap;
import service.ExecutionException;
import service.IService;
import service.Task;

/**
 * A task for the concurrent execution of service method invocations (operation invoke)
 * 
 * @author Diogo Mourão
 * 
 * @param <R> Type of the task's result
 */

public class InvokeHandlerTask<R> extends Task<R> {

	private static final long serialVersionUID = -5233023622592679671L;
	
	/**
	 * Name of the target method
	 */
	private final String name;
	
	/**
	 * Types of the parameters
	 */
	private Class<?>[] parameterTypes;
	
	/**
	 * Values of the parameters
	 */
	private final Object[] parameterValues;

	/**
	 * Constructor with no type information
	 * 
	 * @param service Target service
	 * @param name Name of the method
	 * @param args Values for the parameters
	 */
	public InvokeHandlerTask(IService service, String name, Object... parameterValues) {
		this(name, service, parameterValues);
		this.parameterTypes = new Class<?>[parameterValues.length];
		for (int i = 0; i < parameterValues.length; i++)
			this.parameterTypes[i] = parameterValues[i].getClass();
		unboxTypes();
	}

	/**
	 * Constructor with explicitly supplied type information
	 * 
	 * @param service Target service
	 * @param name Name of the method
	 * @param parameterTypes Types of the parameters
	 * @param parameterValues Values for the parameters
	 */
	public InvokeHandlerTask(IService service, String name, Class<?>[] parameterTypes, Object... parameterValues) {
		this(name, service, parameterValues);
		this.parameterTypes = parameterTypes;
		unboxTypes();
	}

	/**
	 * Auxiliary constructor
	 * 
	 * @param name
	 * @param service
	 * @param parameterValues
	 */
	private InvokeHandlerTask(String name, IService service, Object... parameterValues) {
		this.name = name;
		this.parameterValues = parameterValues;
		this.service = service;
	}

	@SuppressWarnings("unchecked")
	public R call() throws java.lang.NoSuchMethodException, ExecutionException {
		try {
			try {
				Method task = this.service.getClass().getMethod(this.name, this.parameterTypes);
				return (R) task.invoke(this.service, this.parameterValues);
			} catch (java.lang.NoSuchMethodException e) {
				// Handling generics
				// TODO: improve performance

				for (Method m : this.service.getClass().getMethods()) {
					Class<?>[] mParameterTypes;
					if (m.getName().equals(name)
							&& (mParameterTypes = m.getParameterTypes()).length == parameterTypes.length) {
						int i = 0;
						Class<?>[] tParameterTypes = new Class<?>[this.parameterTypes.length];
						
						for (Class<?> c : mParameterTypes) {
							tParameterTypes[i] = c.equals(Object.class) ?
									Object.class :
									this.parameterTypes[i];	
							i++;	
						}
						try {
							Method task = this.service.getClass().getMethod(name, tParameterTypes);
							return (R) task.invoke(this.service, parameterValues);
						} catch (java.lang.NoSuchMethodException e2) {
							continue;
						}
					}
				}
			}
			throw new java.lang.NoSuchMethodException(this.service.getClass() + "." + this.name);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new ExecutionException("Illegal Argument:" + e.getLocalizedMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new ExecutionException("Illegal Access:" + e.getLocalizedMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new ExecutionException("Invocation error:" + e.getLocalizedMessage());
		}
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public InvokeHandlerTask<R> clone() {
		return new InvokeHandlerTask<R>(service, name);
	}


	/**
	 * Unbox the types of the parameters 
	 */
	public void unboxTypes() {
		for (int i = 0; i < this.parameterTypes.length; i++) {
			Class<?> unboxedType = PrimitiveTypesMap.get(this.parameterTypes[i]);
			if (unboxedType != null) 
				this.parameterTypes[i] = unboxedType;
		}
	}
}
