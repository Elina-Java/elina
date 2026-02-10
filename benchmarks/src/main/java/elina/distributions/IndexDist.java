package elina.distributions;


import core.collective.AbstractDistribution;


public class IndexDist extends AbstractDistribution<int[]> {

	protected final int length;


	public IndexDist(int length, int minimumPartitionSize, int elementSize) {
		super(minimumPartitionSize, elementSize);
		this.length = length;
	}


	public IndexDist(int length, int elementSize) {
		this(length, elementSize, elementSize);
	}

	public int[][] distribution(final int numberOfPartitions) {		
		//final int numberOfPartitions = getPartitions();

		final int[][] partitions = new int[numberOfPartitions][2];
		final int chunk = length/numberOfPartitions;
		final int remainder = length%numberOfPartitions;

		partitions[0][0] = 0;
		int k = 0;
		for ( ; k < numberOfPartitions-1; k++) {	
			partitions[k+1][0] = partitions[k][1]  = partitions[k][0] + chunk + (remainder - k > 0 ? 1 : 0);
		}
		partitions[k][1] = partitions[k][0] + chunk + (remainder - numberOfPartitions-1 > 0 ? 1 : 0);
		return partitions;
	}
	
	@Override
	public float getAveragePartitionSize(int nParts) {
		return length/(float)nParts;
	}

	@Override
	public float getAverageLineSize(int nParts) {
		return length/(float)nParts;
	}
}
