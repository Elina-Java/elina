package elina.distributions;

import core.collective.AbstractDistribution;

public class ByteArrayDist extends AbstractDistribution<byte[]> {

	private final byte[] array;
	
	public ByteArrayDist(byte[] array) {
		super(1,1);
		this.array = array;
	}

	public byte[][] distribution(final int nParts) {
		//int nParts = getPartitions();
		byte[][] dist = new byte[nParts][];

		final int perPlace = this.array.length / nParts;
		final int remainder = this.array.length % nParts;	
		int i = 0;
		for (int k = 0; k < nParts; k++) {
			final int restPlaces = remainder - k > 0 ? 1 : 0;
			final int begin = k * perPlace + java.lang.Math.min(k, remainder);
			final int end = begin + perPlace + restPlaces;
					
			byte[] a = new byte[end - begin];
			for (int j = 0; j < end - begin; j++) 
				a[j] = this.array[i++]; 
			dist[k] = a;
		}
		return dist;
	}
	

	@Override
	public float getAveragePartitionSize(int nParts) {
		return this.array.length / (float)nParts;
	}

	@Override
	public float getAverageLineSize(int nParts) {
		return this.array.length / (float)nParts;
	}

}
