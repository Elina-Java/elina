package javaGrande.elina.cluster.series;

import core.collective.AbstractDistribution;
import elina.utils.SizeOf;

public class SeriesDistribution extends AbstractDistribution<SeriesInput>{

	private final int size;

	public SeriesDistribution(int size) {
		super(SizeOf.Double, SizeOf.Double);
		this.size = size;
	}
	
	@Override
	public SeriesInput[] distribution(final int n_parts) {
		SeriesInput partitions[] = new SeriesInput[n_parts];
		int xi, slice;

		slice = (size + n_parts - 1) / n_parts;

		for (int i = 0; i < n_parts; i++) {
			xi = i * slice;

			partitions[i] = new SeriesInput(slice,xi);
		}

		return partitions;
	}
	
	public int getNumberOfElements() {
		return 2*size;
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
