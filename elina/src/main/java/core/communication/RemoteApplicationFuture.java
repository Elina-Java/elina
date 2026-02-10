package core.communication;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import elina.Elina;
import service.IFuture;

/**
 * Classe que representa um futuro para a execução duma aplicação remota
 * @author João Saramago
 *
 * @param <R> 
 */
public class RemoteApplicationFuture<R> implements IFuture<R>,IComEvent {

	
	private CommunicationModule con;
	private UUID id;
	
	private boolean result;

	/**
	 * Construtor
	 */
	public RemoteApplicationFuture() {
		this.con=Elina.getCommunicationModule();
		this.id=UUID.randomUUID();
		con.registerEvent(id, this);
	}

	public UUID getID() {
		return this.id;
	}

	
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	
	public synchronized R get() {
		try{
			while(!this.result)
				wait();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	public R get(long timeout, TimeUnit unit) {
		return this.get();
	}

	
	public boolean isCancelled() {
		return false;
	}

	
	public boolean isDone() {
		return false;
	}

	
	public synchronized <C> void processMessage(Message<C> sms) {
		switch (sms.getType()) {
		case APP_END:
			this.result=true;
				notifyAll();
			break;

		default:
			break;
		}
		
	}

}
