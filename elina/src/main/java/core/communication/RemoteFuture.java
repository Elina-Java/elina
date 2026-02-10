package core.communication;

import java.io.EOFException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import core.CLoader;
import core.Utils;
import elina.Elina;
import service.IFuture;
import service.Task;

/**
 * Class that represents a future for an operation submitted to a remotely located service 
 * via a proxy 
 * 
 * @author João Saramago
 *
 * @param <R> The type of the result
 */
public class RemoteFuture<R> implements IFuture<R>, Serializable, IComEvent {

	private static final long serialVersionUID = 1L;
	
	private Task<R> task;
	@SuppressWarnings("rawtypes")
	private Node host;
	private UUID id;
	private UUID remote_id;
	private UUID client_id;
	private CommunicationModule con;
	
	private Boolean cancel;
	private Boolean isCancelled;
	private Boolean isDone;
	
	
	private R result;
	private boolean hasresult=false;
	
	/**
	 * Constructor
	 * @param task The task to execute
	 * @param location The node that will execute the task
	 * @param client_id The identifier of the service that is invoking the task 
	 */
	@SuppressWarnings("rawtypes")
	public RemoteFuture(Task<R> task, Node location, UUID client_id) {
		this.task = task;
		this.host = location;
		//this.client_id=ClientUUIDFactory.getUUID();
		this.client_id=client_id;
		this.con=Elina.getCommunicationModule();
		this.id=UUID.randomUUID();
	}

	
	public synchronized boolean cancel(boolean mayInterruptIfRunning) {

		try{
			while(this.remote_id==null)
				wait();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		con.sendMessage(new RemoteOperation(remote_id,client_id,"cancel",mayInterruptIfRunning),host, id, MessageTag.FUTURE_OP);
		
		try{
			while(this.cancel==null)
				wait();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		boolean tosend=this.cancel;
		
		this.cancel=null;
		return tosend;
	}

	
	public synchronized R get() {
		try{
			while(!hasresult)
				wait();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public R get(long timeout, TimeUnit unit) {
		return this.get();
	}

	public synchronized boolean isCancelled() {
		try{
			while(this.remote_id==null)
				wait();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		con.sendMessage(new RemoteOperation(remote_id,client_id,"isCancelled"), host, id, MessageTag.FUTURE_OP);
		
		
		
		
		try{
			while(this.isCancelled==null)
				wait();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		boolean tosend=this.isCancelled;
		
		this.isCancelled=null;
		return tosend;
	}

	
	public synchronized boolean isDone() {
		try{
			while(this.remote_id==null)
				wait();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		con.sendMessage(new RemoteOperation(remote_id,client_id,"isDone"), host, id, MessageTag.FUTURE_OP);
		

		try{
			while(this.isCancelled==null)
				wait();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		boolean tosend=this.isDone;
		
		this.isDone=null;
		return tosend;
	}

	/**
	 * Inicialização do futuro. Consiste em registar este objecto no driver de comunicação para receber mensagens e de enviar a tarefa a executar.
	 * @return Retorna o proprio
	 */
	public IFuture<R> init() {
		
		con.registerEvent(id, this);
		
		
		
		con.sendMessage(new Object[]{this.client_id,Utils.toByteArray(this.task)}, host, id, MessageTag.ADDTASK);
		
		
		return this;
	}
	
	
	public synchronized void set(R result) {
		this.result = result; 
		this.hasresult = true;
		notifyAll();
	}

	
	public UUID getID() {
		return this.id;
	}
	
	public Task<R> getTask(){
		return this.task;
	}

	@SuppressWarnings("unchecked")
	public synchronized <C> void processMessage(Message<C> sms) {
		switch (sms.getType()) {
		case REMOTE_FUTURE:
			this.remote_id=(UUID)sms.getContent();
			break;
		case TASK_RESULT:
			Thread.currentThread().setContextClassLoader(CLoader.getClassLoader(this.client_id));
			try {
				R result = (R) Utils.toObject((byte[]) sms.getContent(), CLoader.getClassLoader(this.client_id));
			} catch (EOFException e1) {
				e1.printStackTrace();
			}	
			set(result);
			break;
		case OP_RESULT:
			ResultRemoteOperation r=(ResultRemoteOperation)sms.getContent();
			try {
				Field f = this.getClass().getDeclaredField(r.getOp());
				f.set(this, r.getResult());
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			notifyAll();
			break;
		default:
			break;
		}
		
	}
	
}