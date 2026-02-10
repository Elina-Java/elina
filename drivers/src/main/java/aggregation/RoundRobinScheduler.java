package aggregation;

import java.util.concurrent.Phaser;

import service.IFuture;
import service.SOMDTask;
import service.Task;
import core.collective.Reduction;
import drivers.Adapters;
import drivers.SchedulingDriver;
import drivers.TaskExecutorDriver;

public class RoundRobinScheduler<R> implements SchedulingDriver<R> {

	@Override
	public IFuture<R> schedule(SOMDTask<R> task, Object[][] partitions,
			int[][] combs, Reduction<R> red, R[] results, int nWorkers) {
		
		int tasksPerThread = results.length/nWorkers;
		int remainder = results.length%nWorkers;
		
		Phaser completed = new Phaser(nWorkers+1);
		
		TaskExecutorDriver taskManager = Adapters.getTaskExecutor();
		
		for (int i = 0; i < nWorkers; i++) 
		{
			Executor e = new Executor();
			e.setParams(results,partitions,combs,tasksPerThread,remainder,task,completed);
			taskManager.execute(e);
		}

		if (red == null) {
			completed.arriveAndAwaitAdvance();
			return null;
		}
		else	
			return new DistRedFuture<R>(completed,(R[]) results, red);
	}

	private class Executor extends Task<R>{
		
		private Object[][] partitions;
		private Object[] partition;
		private R[] results;
		private int[][] combs;
		private int tasksPerThread;
		private int remainder;
		private Phaser completed;
		private SOMDTask<R> task;
		
		public void setParams(R[] results, Object[][] partitions,
				int[][] combs, int tasksPerThread, int remainder, SOMDTask<R> task, Phaser completed) {
			
			partition = new Object[partitions.length];
			this.results=results;
			this.partitions=partitions;
			this.combs=combs;
			this.tasksPerThread=tasksPerThread;
			this.remainder=remainder;
			this.completed=completed;
			this.task=task;
		}
		
		@Override
		public R call() throws Exception {
			String tname = Thread.currentThread().getName();
			
			int nDistrs = partitions.length;
			
			int tid = Integer.valueOf(tname.substring(tname.indexOf('-')+1, tname.length()));
			int etid = tid-3;
			
			int nWorkers = results.length/tasksPerThread;
			int nIters = tasksPerThread + (remainder - etid > 0 ? 1 : 0);
			int fIndex = etid + nIters*nWorkers;
			
			try {
				if(combs==null)
					while(etid<fIndex)
					{
						for(int d=0;d<nDistrs;d++)
							partition[d] = partitions[d][etid];
						results[etid] = task.call(partition);
						etid+=nWorkers;
					}
				else
				{
					while(etid<fIndex)
					{
						int[] comb = combs[etid];
						for(int d=0;d<nDistrs;d++)
							partition[d] = partitions[d][comb[d]];
						results[etid] = task.call(partition);
						etid+=nWorkers;
					}
				}
				completed.arriveAndAwaitAdvance();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
