package core.communication;

import java.lang.reflect.Field;
import java.util.UUID;

import core.CLoader;
import core.Utils;
import elina.Elina;
import service.IService;
import service.Task;



public class LocalRemoteTask<R> extends Task<R>{

	private static final long serialVersionUID = 1L;
	private Task<R> task;

	private UUID remoteid;
	private UUID id;
	@SuppressWarnings("rawtypes")
	private Node remote;
	
	
	public LocalRemoteTask(Task<R> t, IService s, UUID sendingObject) {
		this.task=t;
		this.service=s;
		this.remoteid=sendingObject;
		this.id=UUID.randomUUID();
	}

	@SuppressWarnings("rawtypes")
	public LocalRemoteTask(Task<R> t, IService s, UUID sendingObject,
			Node client) {
		this(t, s, sendingObject);
		this.remote=client;
	}

	
	public R call() throws Exception {
		
		try {
			Field f = getField(task.getClass(),"service");
			f.setAccessible(true);
			f.set(task, this.service);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		Thread.currentThread().setContextClassLoader(CLoader.getClassLoader(this.service.getClientId()));
		
		R aux = task.call();
		CommunicationModule com = Elina.getCommunicationModule();
		com.sendMessage(Utils.toByteArray(aux), remoteid, id, MessageTag.TASK_RESULT,remote);
		return aux;
	}

	
	private Field getField(Class<?> c, String string) throws NoSuchFieldException{
		try{
			return c.getDeclaredField(string);
		}catch (NoSuchFieldException e) {
			if(c.getSuperclass()!=null){
				Class<?> aux = c.getSuperclass();
				return getField(aux,string);
			}
		}
		throw new NoSuchFieldException();
	}

	

	public UUID getID() {
		return id;
	}

	

}
