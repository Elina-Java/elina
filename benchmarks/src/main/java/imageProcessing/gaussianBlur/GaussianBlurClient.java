package imageProcessing.gaussianBlur;

import imageProcessing.gaussianWeight.GaussianWeightUtils;
import java.io.IOException;
import elina.utils.Evaluation;

public class GaussianBlurClient  {

	private final GaussianBlurService  service;
	private final int EXECS, radius;

	public GaussianBlurClient (GaussianBlurService service, int radius, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
		this.radius = radius;
	}

	public void run() throws IOException {
		int size = GaussianBlurData.Matrix_sizes[0];
		double sigma = 1000000000;//1.5;
		//double sigma = 1.5;

		double[][] t0 = GaussianBlurData.createMatrix(size);
		double[][] t1 = GaussianBlurData.createMatrix(size);
		double[][] weightMatrix = new double[t0.length][t0[0].length];

		GaussianWeightUtils.computeWeightMatrix(weightMatrix, sigma);

		Evaluation eval = new Evaluation(EXECS);
		eval.writeAppName("Gaussian_Blur"+"_radius="+radius, size);
		for (int j = 0; j < EXECS; j++) {
			eval.startTimer();

			service.gaussianBlur(weightMatrix, radius, t0, t1);

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
