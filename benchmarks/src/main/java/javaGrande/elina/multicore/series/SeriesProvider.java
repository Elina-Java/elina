package javaGrande.elina.multicore.series;

import service.Service;
import elina.distributions.IndexDist;
import elina.utils.SizeOf;

public class SeriesProvider extends Service implements SeriesService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public double[][]  getFourierCoefficients(double[][]  TestArray, int size) {


		// Calculate the fourier series. Begin by calculating A[0].
		
		TestArray[0][0] = JavaGrandeData.TrapezoidIntegrate((double)0.0, //Lower bound.
                (double)2.0,            // Upper bound.
                1000,                    // # of steps.
                (double)0.0,            // No omega*n needed.
                0) / (double)2.0;       // 0 = term A[0].

	    // Calculate the fundamental frequency.
	    // ( 2 * pi ) / period...and since the period
	    // is 2, omega is simply pi.
		
		SerieTask task = new SerieTask(TestArray);
		dist(task, new IndexDist(size, 1, SizeOf.Double));
		return TestArray;
	}

}
