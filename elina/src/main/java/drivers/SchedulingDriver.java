package drivers;

import core.collective.Reduction;
import service.IFuture;
import service.SOMDTask;

public interface SchedulingDriver<R> {
	
	/**
	 * Schedules the workingsets for the task to be executed among the
	 * workers available on the middleware's worker pool. 
	 * @param task task to be executed
	 * @param partitions partitions of the different input arguments
	 * @param combs combinations of partitions from different input arguments
	 * @param red reduction to be applied to the partial results produced
	 * @param results array where results from different task executions will be placed
	 * @param nWorkers number of workers that will execute 
	 * @return Future for access to the final result once it is produced
	 */
	IFuture<R> schedule(SOMDTask<R> task, Object[][] partitions, int[][] combs, 
			Reduction<R> red, R[] results, int nWorkers);

}
