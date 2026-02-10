package core.collective;

import drivers.Adapters;


public abstract class AbstractReduction<T> implements Reduction<T> {

	private int nParts = Adapters.getTaskExecutor().getNumberOfWorkers();
	
	@Override
	public void setPartitions(int length) {
		this.nParts = length;
	}
	
	@Override
	public int getPartitions() {
		return this.nParts;
	}
	
}
