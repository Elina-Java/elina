package javaGrande.elina.multicore.montecarlo;

import core.collective.AbstractDistribution;
import elina.utils.SizeOf;

public class DemoDist extends AbstractDistribution<ToTask[]> {

	private static final String MCheader = "MC run ";
	private int from, to;
	
	public DemoDist(int[] nRunsMC) {
		super(1, MCheader.length() + String.valueOf(nRunsMC[1]).length() + SizeOf.Int);
		this.to = nRunsMC[1];
		this.from = nRunsMC[0];
	}

	public ToTask[][] distribution(final int n_parts) {

		//int n_parts = getPartitions();
		System.out.println(" (to - from)" + (to - from));
		System.out.println(" size " + getNumberOfElements());
		System.out.println(" nparts " + n_parts);
		int slice = (int) Math.floor(((to - from) + n_parts - 1) / n_parts);

		int n = from;
		ToTask[][] out = new ToTask[n_parts][];
		for (int j = 0; j < n_parts; j++) {
			int limit = j == n_parts - 1 ? (to - n) : slice;
			ToTask[] tasks = new ToTask[limit];
			// System.out.println("Lower: " + n + " Upper: " + (n+limit));
			for (int i = 0; i < limit; i++, n++) 				
				tasks[i] = new ToTask(MCheader + String.valueOf(n), (long) n * 11);
			
			out[j] = tasks;
		}

		return out;
	}


	public int getNumberOfElements() {
	 return  (to-from) * getMiminumPartitionSize();
	}

	public int getMiminumPartitionSize() {
		final int len = (to-from);

		int n = 1, p = 0;
		while(n < len) {
			p++;
			n = n*10;
		}
		
		return MCheader.length() + p  + SizeOf.Int;
	}

	@Override
	public float getAveragePartitionSize(int nParts) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAverageLineSize(int nParts) {
		// TODO Auto-generated method stub
		return 0;
	}

}
