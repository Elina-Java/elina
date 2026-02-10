package service;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;

import core.Level;
import core.communication.Node;
import core.communication.RemoteFuture;
import core.taskManager.InvokeHandlerTask;
import drivers.Adapters;
import service.aggregator.IReconfigurablePool;

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

public class ServiceStub<S extends IService> implements IService, IReconfigurablePool,
		Serializable {

	private static final long serialVersionUID = 1L;
	protected UUID id;
	protected UUID clientid;
	@SuppressWarnings("rawtypes")
	protected Node location;
	protected Level level;
	protected WebServiceStubGenerator wssg;
	private boolean paused;
	@SuppressWarnings("rawtypes")
	private Queue<RemoteFuture> executionsQueue;

	/**
	 * The original service. Only available if both proxy and service are in the
	 * same node
	 */
	protected transient S service;

	// Referencia para o módulo de comunicação
	// CommModule
	@SuppressWarnings("rawtypes")
	public void setLocation(Node location) {
		this.location = location;
	}

	protected ServiceStub() {
		paused = false;
	}

	public ServiceStub(UUID id, UUID clientid, Level l) {
		this.id = id;
		this.clientid = clientid;
		this.level = l;
		this.paused = false;
	}

	public ServiceStub(UUID id, UUID clientid, Level l, WebServiceStubGenerator wssg) {
		this.id = id;
		this.clientid = clientid;
		this.level = l;
		this.wssg = wssg;
		this.paused = false;
	}

	@Override
	public <R> IFuture<R> invoke(String methodName, Object[] args, Class<?>[] types) throws NoSuchMethodException {
		if (this.paused) {
			RemoteFuture<R> _rf = new RemoteFuture<R>(
						new InvokeHandlerTask<R>(this, methodName, types, args), this.location, this.clientid);
			executionsQueue.add(_rf);
			return _rf;

		}
		if (this.service == null)
			return new RemoteFuture<R>(
					new InvokeHandlerTask<R>(this, methodName, types, args), this.location, this.clientid).init();

		return this.service.invoke(methodName, args, types);
	}

	@Override
	public <R> IFuture<R> invoke(String methodName, Object[] args)
			throws NoSuchMethodException {
		if (paused) {
			RemoteFuture<R> _rf = new RemoteFuture<R>(new InvokeHandlerTask<R>(
					this, methodName, args), this.location, this.clientid);
			executionsQueue.add(_rf);
			return _rf;
		}
		if (this.service == null)
			return new RemoteFuture<R>(new InvokeHandlerTask<R>(this,
					methodName, args), this.location, this.clientid).init();

		return this.service.invoke(methodName, args);
	}

	@Override
	public UUID getClientId() {
		return this.clientid;
	}

	@Override
	public UUID getID() {
		return this.id;
	}

	@Override
	public void cancel() {
		new RemoteFuture<Void>(new InvokeHandlerTask<Void>(this, "cancel"),
				this.location, this.clientid).init();
	}

	@Override
	public Level getLevel() {
		return this.level;
	}

	@Override
	public void setAffinity(IService p) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setImpAffinity(IService p) {
		// TODO Auto-generated method stub

	}

	public void setService(S service) {
		this.service = service;
	}

	/*
	 * @Override public <T, R> IFuture<R> distReduce(Distribution<?>[] distr,
	 * Reduction<R> red, String methodName, int index, Object... args) throws
	 * NoSuchMethodException { // TODO Auto-generated method stub return null; }
	 */

	// Methods from IReconfigurable interface

	@Override
	public void replaceFromSystem() {
		// TODO Auto-generated method stub

	}

	@Override
	public void replace(URL url) {
		try {
			System.out.println(url.toString());
			this.setService(this.wssg.<S> getStub(url.toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> void replace(R newProvider) {
		boolean canReplace = true;
		try {
			@SuppressWarnings("unused")
			S _newProvider = (S) newProvider;
		} catch (ClassCastException e) {
			canReplace = false;
			e.printStackTrace();
		}

		if (canReplace) 
			this.setService((S) newProvider);
	//	return canReplace;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public synchronized void pause() {
		if (this.paused)
			return;
		this.paused = true;
		executionsQueue = new ArrayDeque<RemoteFuture>();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public synchronized void resume() {
		if (!this.paused)
			return;
		
		this.paused = false;
		for (RemoteFuture exec : executionsQueue) {
			if (this.service == null)
				exec.init();
			else {
				try {
					Task task = exec.getTask();
					task.setService(this.service);
					exec.set(Adapters.getTaskExecutor().execute(task).get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public <R> void add(R provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <R> void remove(R provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <R> void removeAll(R provider) {
		// TODO Auto-generated method stub
		
	}

}
