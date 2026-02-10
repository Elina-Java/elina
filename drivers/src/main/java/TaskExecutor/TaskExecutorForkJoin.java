package TaskExecutor;

import java.util.UUID;

import service.IFuture;
import service.Task;
import drivers.TaskExecutorDriver;

public class TaskExecutorForkJoin implements TaskExecutorDriver{

	@Override
	public <R> IFuture<R> execute(Task<R> task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfAvailableWorkers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public int getNumberOfWorkers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void cancelTasks(UUID client) {
		// TODO Auto-generated method stub
		
	}




}
