package elina.distributions;

public class IndexDistMultipleOf extends IndexDist {

	
	public IndexDistMultipleOf(int length, int base, int unitSize) {
		super(length, base, unitSize);
	}

	public int[][] distribution() {		
		final int numberOfPartitions = getPartitions();
		
		final int[][] partitions = new int[numberOfPartitions][2];
		int chunk = length/numberOfPartitions;
//		System.out.println("chunk " + chunk);
		
		chunk = (chunk%this.numberOfElementsPerPartition == 0) ? 
				chunk : 
				(chunk-this.numberOfElementsPerPartition)/this.numberOfElementsPerPartition*this.numberOfElementsPerPartition;
		int remainder = length - chunk*numberOfPartitions;
		
	//	System.out.println("length " + (length));
	//	System.out.println("chunk*numberOfPartitions " + (chunk*numberOfPartitions));

		partitions[0][0] = 0;
		int k = 0;
		
	//	System.out.println("numberOfPartitions " + numberOfPartitions);
	//	System.out.println("chunk " + chunk);	
	//	System.out.println("remainder " + remainder);
		
		for ( ; k < numberOfPartitions-1; k++) {
			if (remainder > 0) {
				
				partitions[k+1][0] = partitions[k][1]  = partitions[k][0] + chunk + this.numberOfElementsPerPartition;
				remainder= Math.max(0, remainder-this.numberOfElementsPerPartition);
			}
			else 
				partitions[k+1][0] = partitions[k][1]  = partitions[k][0] + chunk;
		}
	//	partitions[k][1] = partitions[k][0] + chunk + (remainder - numberOfPartitions-1 > 0 ? 1 : 0);
			
		partitions[k][1] = Math.min(length, partitions[k][0] + chunk + (remainder - numberOfPartitions-1 > 0 ? 1 : 0));
		
		return partitions;
	}

}
