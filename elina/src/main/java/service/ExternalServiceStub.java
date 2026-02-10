package service;

import java.io.Serializable;
import java.util.UUID;

import core.Level;
import core.communication.Node;
import core.communication.RemoteFuture;
import core.taskManager.InvokeHandlerTask;

/**
 * 
 * Esta classe funciona como um proxy, onde ela representa uma localidade fora
 * do contexto de execução da mesma, ou seja, esta classe é vista como um stub,
 * em que contém a mesma interface da localidade mas a implementação dos métodos
 * são delegados para a implementação concreta da classe, que estará alojada
 * remotamente.
 * 
 * @author Diogo Mourão
 * 
 */

public class ExternalServiceStub implements IService, Serializable {

	private static final long serialVersionUID = 1L;
	private UUID clientid;
	private Node location;



	public ExternalServiceStub(UUID clientid) {
		this.clientid = clientid;
	}

	public void setLocation(Node location) {
		this.location = location;
	}

	@Override
	public <R> IFuture<R> invoke(String methodName, Object[] args,Class<?>[] types) throws NoSuchMethodException{
		return new RemoteFuture<R>(new InvokeHandlerTask<R>(this, methodName, types, args),
				this.location, this.clientid).init();
	}
	
	@Override
	public <R> IFuture<R> invoke(String methodName, Object[] args) throws NoSuchMethodException {
		return new RemoteFuture<R>(new InvokeHandlerTask<R>(this, methodName, args),
				this.location, this.clientid).init();
	}

	
	

	@Override
	public UUID getClientId() {
		return this.clientid;
	}
	
	@Override
	public void cancel() {
		new RemoteFuture<Void>(new InvokeHandlerTask<Void>(this, "cancel"),
				this.location, this.clientid).init();
	}

	@Override
	public UUID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAffinity(IService p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setImpAffinity(IService p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Level getLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
