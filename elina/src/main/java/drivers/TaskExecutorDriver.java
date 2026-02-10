package drivers;

import java.util.UUID;

import service.IFuture;
import service.Task;


/**
 *
 * Interface for the Task Executor Driver
 * 
 * @author Diogo Mourão
 * @author João Saramago
 * 
 * revised by Hervé Paulino
 *
 */

public interface TaskExecutorDriver {
	
	<R> IFuture<R> execute(Task<R> task);
	
	/**
	 * Obtain the number of available workers
	 * @return
	 */
	int getNumberOfAvailableWorkers();
	
	int getNumberOfWorkers();
	
	void shutdown();
	
	void cancelTasks(UUID client);

	//void init(int nworkers);

}
