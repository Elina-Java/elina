package javaGrande.elina.cluster.series;

import instrumentation.definitions.DistributionPolicy;
import instrumentation.definitions.ReductionPolicy;

import javaGrande.elina.multicore.series.JavaGrandeData;
import javaGrande.elina.multicore.series.SerieTask;

import service.Service;
import elina.distributions.IndexDist2D;
import elina.utils.SizeOf;

public class SeriesProvider extends Service implements SeriesService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
	@ReductionPolicy(reduction=SeriesReduction.class,params={"size"})
	public SeriesResult getFourierCoefficients(
			@DistributionPolicy(distribution=SeriesDistribution.class,params={"size"}) SeriesInput size) {

		double[][]  TestArray = new double [2][size.size];
		
		if (size.pos == 0) {
			// Calculate the fourier series. Begin by calculating A[0].
		
			TestArray[0][0] = JavaGrandeData.TrapezoidIntegrate((double) 0.0, //Lower bound.
                (double)2.0,            // Upper bound.
                1000,                    // # of steps.
                (double)0.0,            // No omega*n needed.
                0) / (double)2.0;       // 0 = term A[0].
		
		}

		dist(new SerieTask(TestArray), new IndexDist2D(size.size, 2, SizeOf.Double, SizeOf.Double));
		SeriesResult result = new SeriesResult(TestArray, size.pos);
		return result;
	}
	

}
