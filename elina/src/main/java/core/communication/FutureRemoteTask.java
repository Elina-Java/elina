package core.communication;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import elina.Elina;

/**
 * Classe que representa um futuro remoto
 * 
 * @author João Saramago
 * 
 * @param <V>
 *            Tipo de dados do retorno do futuro
 */
public class FutureRemoteTask<V> implements RunnableFuture<V> {
	private Callable<V> callable;
	private RemoteFuture<V> result;
	@SuppressWarnings("rawtypes")
	private Node host;
	private UUID id;

	/**
	 * Construtor
	 * @param callable Tarefa a executar
	 * @param host Identificador do nó que irá executar a tarefa 
	 */
	@SuppressWarnings("rawtypes")
	public FutureRemoteTask(Callable<V> callable, Node host) {
		this.callable = callable;
		this.host = host;
		this.id = UUID.randomUUID();
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return this.result.cancel(mayInterruptIfRunning);
	}

	public V get() throws InterruptedException, ExecutionException {
		return this.result.get();
	}

	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return this.result.get(timeout, unit);
	}

	public boolean isCancelled() {
		return this.result.isCancelled();
	}

	public boolean isDone() {
		return this.result.isDone();
	}

	public void run() {

		CommunicationModule con = Elina.getCommunicationModule();

		con.sendMessage(this.callable, host, id, MessageTag.ADDTASK);
	}
}
