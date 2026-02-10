package imageProcessing.gaussianWeight;

import java.io.IOException;
import elina.utils.Evaluation;

public class GaussianWeightClient  {

	private final GaussianWeightService  service;
	private final int EXECS;

	public GaussianWeightClient (GaussianWeightService service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {
		
		int size = GaussianWeightData.Matrix_sizes[0];
		double sigma = 1.5;
		double[][] weightMatrix = new double[size][size];

		Evaluation eval = new Evaluation(EXECS); 
		eval.writeAppName("Gaussian_Weight_Matrix", size);
		
		for (int j = 0; j < EXECS; j++)
		{
			eval.startTimer();
			service.computeGaussianWeight(weightMatrix, sigma);
			eval.stopTimer();
			eval.writeTime();
			//useless?
			System.gc();
		}
		eval.writeAverage();
		eval.writeSTDV();

		eval.end();
	}

}
