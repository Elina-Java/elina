package TaskExecutor;

/*
 * One of the things I encounter from time to time is the missing handling of the RejectedExecutionException (thrown by the Executor). 
 * This exception can be thrown when the executor:
 *	1. is shutting down. When it is shutting down, you don’t want it to execute accept new task: the shutdown should have a higher priority than the acceptance of new tasks.
 *	2. the executor doesn’t want to accept the task. If you are using a bounded BlockingQueue as workqueue in the ThreadPoolExecutor 
 *	(bounded = good, makes your system degrade gracefully) and the queue is full, the Executor doesn’t block until the task can be placed 
 * (this is something not all developers realize), but rejects the tasks by throwing the RejectedExecutionException.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import service.Future;
import service.IFuture;
import service.IService;
import service.Task;
import core.CLoader;
import core.taskManager.TaskRegistry;
import drivers.Adapters;
import drivers.TaskExecutorDriver;
import elina.Elina;

/**
 * A implementation of the TaskExecutorDriver based on ThreadPoolExecutor
 * 
 * @author Diogo Mourão
 * @author João Saramago
 * 
 * Revised by Hervé Paulino
 * 
 */
public final class TaskExecutorPoolThreads extends ThreadPoolExecutor implements
		TaskExecutorDriver {

	/**
	 * Task registry
	 */
	private TaskRegistry<FutureTask<?>> taskRegistry = new TaskRegistry<FutureTask<?>>();
	
	/**
	 * Number of worker threads - size of pool
	 */
	private int numberOfThreads;
	
	/**
	 * The logger
	 */
	private static Logger logger = Logger.getLogger(TaskExecutorPoolThreads.class);

	/**
	 * Map of futures into tasks
	 */
	private Map<FutureTask<?>, Task<?>> taskmap = new HashMap<FutureTask<?>, Task<?>>();

	/**
	 * Constructor that reserves twice the number of workers requested   
	 * @param numberOfWorkers Numberof workers
	 */

	public TaskExecutorPoolThreads(int numberOfWorkers) {
//		super(numberOfWorkers*2, numberOfWorkers*2, 0,
//				TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(129),
		super(numberOfWorkers, numberOfWorkers, 0,
		TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(2000),
				new ThreadFactory() {

					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setDaemon(true);
						return t;
					}
				});
		allowCoreThreadTimeOut(false);
		prestartAllCoreThreads();
		this.numberOfThreads = numberOfWorkers;  //*2;
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		if (Elina.DEBUG)
			logger.debug("Init Execute task " + r);

		super.beforeExecute(t, r);
		Task<?> task = this.taskmap.get((FutureTask<?>) r);
		
		
		try {
			t.setContextClassLoader(CLoader.getClassLoader(task.getService().getClientId()));
		} catch (NullPointerException e) {

		}

		//TODO - PROFESSOR
		/*
		((ConsoleOut) System.out).Associate(t, task);
		((ConsoleOut) System.err).Associate(t, task);
		((ConsoleIn) System.in).Associate(t, task);
		*/
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		if (Elina.DEBUG)
			logger.debug("End Execute task " + r);
		super.afterExecute(r, t);

		this.taskRegistry.removeTask((FutureTask<?>) r);
	}

	
	//TODO
	@Override
	public <R> IFuture<R> execute(final Task<R> task) {
		if (Elina.DEBUG)
			logger.debug("Submit task [" + task + "]");
		//if (this.getNumberOfAvailableWorkers() > 0) {
			RunnableFuture<R> ftask = newTaskFor(task);
			this.taskmap.put((FutureTask<R>) ftask, task);

			IService service;
			if ((service = task.getService()) != null)
				this.taskRegistry.register(service.getClientId(), (FutureTask<R>) ftask);
			
			execute(ftask);
			
			return new Future<R>(ftask);
			/*
		}
		else
			return new Future<R>(new java.util.concurrent.Future<R>() {

				@Override
				public boolean cancel(boolean mayInterruptIfRunning) {
					return false;
				}

				@Override
				public boolean isCancelled() {
					return false;
				}

				@Override
				public boolean isDone() {
					return true;
				}

				@Override
				public R get() throws ExecutionException {
					try {
						return task.call();
					} catch (Exception e) {
						throw new ExecutionException(e.getMessage(), null);
					}
				}

				@Override
				public R get(long timeout, TimeUnit unit)
						throws InterruptedException, ExecutionException,
						TimeoutException {
					return this.get();
				}

			});
			*/
	}




	@Override
	public int getNumberOfAvailableWorkers() {
		return (this.numberOfThreads - getActiveCount());
	}

	@Override
	public void cancelTasks(UUID clientid) {
		
		List<FutureTask<?>> a = taskRegistry.removeClient(clientid);
		if (a != null)
			for (FutureTask<?> f : a) {
				remove(f);
				f.cancel(false);
				taskRegistry.removeTask(f);
			}
		
	}

	@Override
	public int getNumberOfWorkers() {
		return this.numberOfThreads; // /2;
	}
}
