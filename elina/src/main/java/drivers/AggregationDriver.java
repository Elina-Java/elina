package drivers;

import java.util.concurrent.Phaser;

import service.SOMDTask;
import service.Task;

public abstract class AggregationDriver<R> extends Task<R> implements Cloneable {
	
	protected SOMDTask<R> task;
	
	protected Object[][] partitions;
	protected Object[] partition;
	
	protected R[] results;
	protected int[][] combins;
	
	protected int tasksPerThread;
	protected int remainder;
	protected int ndistrs;
	
	protected Phaser completed;
	
	public void setTask(SOMDTask<R> task) {
		this.task=task;
	}
	
	public SOMDTask<R> getTask() {
		return task;
	}

	public void setParams(R[] results, Object[][] partitions,int[][] combins,int tasksPerThread,int remainder, Phaser p) {
		this.results=results;
		this.partitions=partitions;
		this.combins=combins;
		this.tasksPerThread=tasksPerThread;
		this.remainder=remainder;
		ndistrs=partitions.length;
		this.partition = new Object[ndistrs];
		this.completed=p;
	}
	
	@Override
	public Object clone()
	{
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
