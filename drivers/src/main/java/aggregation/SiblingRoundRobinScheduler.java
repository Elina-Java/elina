package aggregation;

import java.util.concurrent.Phaser;

import service.IFuture;
import service.SOMDTask;
import service.Task;
import core.collective.Reduction;
import drivers.Adapters;
import drivers.HierarchyLevel;
import drivers.HierarchyReadDriver;
import drivers.SchedulingDriver;
import drivers.TaskExecutorDriver;

public class SiblingRoundRobinScheduler<R> implements SchedulingDriver<R> {

	@Override
	public IFuture<R> schedule(SOMDTask<R> task, Object[][] partitions,
			int[][] combs, Reduction<R> red, R[] results, int nWorkers) {
		
		HierarchyReadDriver hierarchyReader = Adapters.getHierarchyReadDriver();
		HierarchyLevel root = hierarchyReader.getHierarchyRoot();
		
		int slotsInLLC = (int)(root.size/Adapters.getPartitioningDriver().getCriteriaSize());
		int tasksPerLLC = results.length < slotsInLLC ? results.length : slotsInLLC + 
				(slotsInLLC%root.siblings[0].length != 0 ? (root.siblings[0].length-slotsInLLC%root.siblings[0].length) : 0);
		//Avoid an unequal load distribution
		
		Phaser completed = new Phaser(nWorkers+1);
		
		TaskExecutorDriver taskManager = Adapters.getTaskExecutor();
				
		//ciclo passa para #threads
		for (int i = 0; i < nWorkers; i++) 
		{
			Executor e = new Executor();
			e.setParams(results, partitions,combs,tasksPerLLC,root.siblings[0].length,completed,task);
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
		private int tasksPerLLC;
		private int length;
		private Phaser completed;
		private SOMDTask<R> task;
		
		
		public void setParams(R[] results, Object[][] partitions,
				int[][] combs, int tasksPerLLC, int length, Phaser completed, SOMDTask<R> task) {
			partition = new Object[partitions.length];
			this.results=results;
			this.partitions=partitions;
			this.combs=combs;
			this.tasksPerLLC=tasksPerLLC;
			this.length=length;
			this.completed=completed;
			this.task=task;
		}

		@Override
		public R call() throws Exception {
			String tname = Thread.currentThread().getName();
			int nDistrs = partitions.length;
			
			int tid = Integer.valueOf(tname.substring(tname.indexOf('-')+1, tname.length()));
			final int etid = tid-3;
			final int nworkers = completed.getRegisteredParties()-1;
			final int myGroup = etid/length;
			
			final int lastIndexIncrement = (tasksPerLLC/length + (tasksPerLLC%length - etid%length > 0 ? 1 : 0) - 1) * length; //checked
			
			int index = myGroup*tasksPerLLC + etid%length; //checked
			final int lastTask = (nworkers/length)*tasksPerLLC*(results.length/((nworkers/length)*tasksPerLLC)-1)+
					myGroup*tasksPerLLC + etid%length + lastIndexIncrement;
			
			int remainder = (results.length%((nworkers/length)*tasksPerLLC))%nworkers;
			int tasksPerThread = (results.length%((nworkers/length)*tasksPerLLC))/nworkers;
			
			final int increment =  (nworkers/length-1)*tasksPerLLC +
					(tasksPerLLC%length-etid%length > 0 
							? -length + tasksPerLLC%length - etid%length 
							: -etid%length + tasksPerLLC%length) +
							etid%length;
			
			try {
				if(combs==null)
				{
					int lastIndex;
					while(index<=lastTask)
					{
						lastIndex = index+lastIndexIncrement;
						while(index<=lastIndex)
						{
							for(int d=0;d<nDistrs;d++)
								partition[d] = partitions[d][index];
							results[index] = task.call(partition);
							index+=length;
						}
						index+=increment;
					}
					
					index = results.length - (results.length%((nworkers/length)*tasksPerLLC));
					index += (remainder-etid >= 0 ? (tasksPerThread+1)*etid : (tasksPerThread+1)*remainder+tasksPerThread*(etid-remainder));
					lastIndex = index + tasksPerThread + (remainder-etid > 0 ? 1 : 0);
					
					while(index<lastIndex)
					{
						for(int d=0;d<nDistrs;d++)
							partition[d] = partitions[d][index];
						results[index] = task.call(partition);
						index++;
					}
				}
				else
				{
					int lastIndex;
					while(index<=lastTask)
					{
						lastIndex = index+lastIndexIncrement;
						while(index<=lastIndex)
						{
							int[] comb = combs[index];
							for(int d=0;d<nDistrs;d++)
								partition[d] = partitions[d][comb[d]];
							results[index] = task.call(partition);
							index+=length;
						}
						index+=increment;
					}
					
					index = results.length - (results.length%((nworkers/length)*tasksPerLLC));
					index += (remainder-etid >= 0 ? (tasksPerThread+1)*etid : (tasksPerThread+1)*remainder+tasksPerThread*(etid-remainder));
					lastIndex = index + tasksPerThread + (remainder-etid > 0 ? 1 : 0);
					
					while(index<lastIndex)
					{
						int[] comb = combs[index];
						for(int d=0;d<nDistrs;d++)
							partition[d] = partitions[d][comb[d]];
						results[index] = task.call(partition);
						index++;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			completed.arriveAndAwaitAdvance();
			return null;
		}
	}
}
