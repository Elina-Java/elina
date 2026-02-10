package core.taskManager;

import drivers.Adapters;
import drivers.TaskExecutorDriver;

/**
 * The task manager module
 * Manages the execution of tasks submitted to the underlying task executor
 * 
 * @author Joao Saramago
 * 
 * Revised by Hervé Paulino
 *
 */
public class TaskManagerModule  {

	/**
	 * The task executor driver
	 */
	private TaskExecutorDriver executor;
	
	/**
	 * Is executor local to a service?
	 */
	private boolean isLocal;
		
	/**
	 * Creates a task manager associated to the configured task executor
	 */
	public TaskManagerModule() {
		this.executor = Adapters.getTaskExecutor();
		this.isLocal = false;
	}

	/**
	 * Creates a task manager associated to a given instance of the configured task executor
	 * @param taskExecutorDriver The new instance
	 */
	/*private TaskManagerModule(TaskExecutorDriver taskExecutorDriver) {
		this.executor = taskExecutorDriver;
		this.isLocal = true;
	}*/

	
	public void shutdown() {
		this.executor.shutdown();
	}

/*	public <R> IFuture<R> executeTask(final ITask<R> task) {
		if (Middleware.DEBUG)
			logger.debug("Submit task [" + task + "]");
		return this.executor.execute(task);
	}*/

	/*public <R> IFuture<R>[] executeTasks(ITask<R>[] tasks) {
		if (Middleware.DEBUG)
			logger.debug("Submit tasks [" + tasks.toString() + "]");

		@SuppressWarnings("unchecked")
		IFuture<R>[] allRunningTasks = new Future[tasks.length];

		for (int i = 0; i < tasks.length; i++)  
			allRunningTasks[i] = this.executor.execute(tasks[i]);

		return allRunningTasks;
	}*/

/*	public boolean areThereFreeWorkers() {
		return (this.executor.getNumberOfAvailableWorkers() > 0);
	}

	public void cancelTasks(UUID clientid) {
		if (Middleware.DEBUG)
			logger.debug("Cancel Tasks [App ID " + clientid + "]");
		this.executor.cancelTasks(clientid);
	}
*/
	public static TaskExecutorDriver createTaskExecutor(int n_workers) {
		return Adapters.createTaskExecutor(n_workers);
	}
/*
	public boolean isLocal() {
		return this.isLocal;
	}

	public int getNumberOfAvailableWorkers() {
		return this.executor.getNumberOfAvailableWorkers();
	}

*/
}
