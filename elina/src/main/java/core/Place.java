package core;

import core.collective.SOMDExecutor;
import core.synchronization.SyncModule;
import core.taskManager.TaskManagerModule;
import drivers.Adapters;
import drivers.TaskExecutorDriver;
import service.ConditionCode;
import service.IBarrier;
import service.ICondition;
import service.IFuture;
import service.IMonitor;
import service.Task;

/**
 * Classe que representa um place
 * @author João Saramago
 *
 */
public class Place  {

	public SOMDExecutor dr;
	public TaskManagerModule taskManager;
	public SyncModule syncModule;
	public IMonitor monitor;
	
	private TaskExecutorDriver taskExecutor = Adapters.getTaskExecutor();
	
	public Place() {
		taskManager = new TaskManagerModule();
		dr = new SOMDExecutor();
		
		this.syncModule = new SyncModule();
		this.monitor = syncModule.createMonitor();
	}

	public <R> IFuture<R> spawn(Task<R> task) {
		return taskExecutor.execute(task);
	}

	/*public <R> IFuture<R>[] spawn(ITask<R>[] tasks) {
		@SuppressWarnings("unchecked")
		IFuture<R>[] out = new IFuture[tasks.length];
		
		for (int i = 0; i < tasks.length; i++) {
			
			out[i]=this.spawn(tasks[i]);
		}
		return out;
	}*/

	public void beginAtomic() {
		this.monitor.beginAtomic();
	}

	public void endAtomic() {
		this.monitor.endAtomic();
	}

	public ICondition newCondition(ConditionCode code) {
		return this.monitor.newCondition(code);
	}


	public IBarrier createBarrier() {
		return this.syncModule.createBarrier();
	}

	public IBarrier createBarrier(IBarrier b) {
		return this.syncModule.createBarrier(b);
	}
	
/*	public boolean freeThreads() {
		return taskManager.areThereFreeWorkers();
	}

	public void cancelTasks(UUID clientid) {
		taskManager.cancelTasks(clientid);
	}*/

/*	public void shutdown() {
		taskManager.shutdown();
	}*/

	/*public <R> IFuture<R> distReduce(Distribution<?>[] distr,
			Reduction<R> red, String method, Object[][] params, int index,
			IService service) {
		return (IFuture<R>)dr.getResult(distr, red, method, params, index, service);
	}
	
	public <R> IFuture<R> distReduce(Distribution<?>[] distr,
			Reduction<R> red, SOMDTask<R> task, DistributionsCombination combs) {
		return (IFuture<R>)dr.getResult(distr, red, task, combs);
	}*/
	
	
}
