package javaGrande.elina.sequential;

import java.io.IOException;
import javaGrande.elina.multicore.series.JavaGrandeData;

import elina.utils.Benchmark;
import elina.utils.Evaluation;
import elina.utils.PartitionedTimer;

public class Series extends elina.utils.Benchmark {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		parse(args, Benchmark.class.getName());
		if (PROBSIZE > 0) {
			JavaGrandeData.sizes[0] = PROBSIZE;
			JavaGrandeData.NUMBER_OF_PROBLEMS = 1;
		}
		PartitionedTimer.NEXECS=NEXECS;

		Evaluation eval = new Evaluation(NEXECS); 
		int size = JavaGrandeData.sizes[0];

		eval.writeAppName("series", size);
		for (int j = 0; j < NEXECS; j++) {
			double[][] TestArray = new double [2][size];
			eval.startTimer();
			double[][] result = series(TestArray, size);// getFourierCoefficients(TestArray, size);
			eval.stopTimer();
			eval.writeTime();
			JavaGrandeData.validate(result);
		}
		eval.writeAverage();
		eval.writeSTDV();
		eval.end();
	}
	
	private static double[][] series(double[][] TestArray, int size)
	{
		TestArray[0][0] = JavaGrandeData.TrapezoidIntegrate((double)0.0, //Lower bound.
                (double)2.0,            // Upper bound.
                1000,                    // # of steps.
                (double)0.0,            // No omega*n needed.
                0) / (double)2.0;       // 0 = term A[0].
		getFourierCoefficients(TestArray, size);
		return TestArray;
	}

	private static double[][] getFourierCoefficients(double[][] TestArray, int size) {
		double omega = (double) 3.1415926535897932;
		
		int xi = 0;
		int xf =  TestArray[0].length;

		
		for (int i = Math.max(xi,1); i < xf; i++)
		{
			// Calculate A[i] terms. Note, once again, that we
			// can ignore the 2/period term outside the integral
			// since the period is 2 and the term cancels itself
			// out.

			TestArray[0][i] = JavaGrandeData.TrapezoidIntegrate((double)0.0,
					(double)2.0,
					1000,
					omega * (double)(i), 
					1);                       // 1 = cosine term.

			// Calculate the B[i] terms.

			TestArray[1][i] = JavaGrandeData.TrapezoidIntegrate((double)0.0,
					(double)2.0,
					1000,
					omega * (double)(i),
					2);                       // 2 = sine term.
		}

		return null;
	}

}
