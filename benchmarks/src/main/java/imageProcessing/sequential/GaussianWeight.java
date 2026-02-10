package imageProcessing.sequential;

import java.io.IOException;

import imageProcessing.gaussianWeight.GaussianWeightData;
import imageProcessing.gaussianWeight.GaussianWeightUtils;
import elina.utils.Evaluation;
import elina.utils.PartitionedTimer;

public class GaussianWeight {

	public static void main(String[] args) throws IOException {
		int EXECS = 1;

		if(args.length>=2)
		{
			GaussianWeightData.Matrix_sizes[0]=Integer.parseInt(args[1]);
			EXECS=Integer.parseInt(args[0]);
			PartitionedTimer.NEXECS=EXECS;
		}
		
		int size = GaussianWeightData.Matrix_sizes[0];
		double sigma = 1.5;
		double[][] weightMatrix = new double[size][size];

		Evaluation eval = new Evaluation(EXECS); 
		eval.writeAppName("Gaussian_Weight_MatrixSequential", size);
		
		for (int j = 0; j < EXECS; j++)
		{
			eval.startTimer();
			GaussianWeightUtils.computeWeightMatrix(weightMatrix, sigma);
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
