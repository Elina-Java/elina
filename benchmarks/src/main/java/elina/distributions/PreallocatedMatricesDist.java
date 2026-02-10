package elina.distributions;

import core.collective.AbstractDistribution;

public class PreallocatedMatricesDist extends AbstractDistribution<int[][]> {
	
	private int[][][] matrices;
	private int numberOfElements;

	public PreallocatedMatricesDist(int[][][] matrices, int numberOfElements, int numberOfElementsPerPartition, int elementarySize) {
		super(numberOfElementsPerPartition, elementarySize);
		this.matrices=matrices;
		this.numberOfElements=numberOfElements;
	}

	@Override
	public int[][][] distribution(int dummy) {
		return matrices;
	}
	
	@Override
	public float getAveragePartitionSize(int nParts) {
		return numberOfElements;
	}

	@Override
	public float getAverageLineSize(int nParts) {
		return 1;
	}

}
