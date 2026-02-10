package linearAlgebra.sequential;

import java.io.IOException;

import linearAlgebra.elina.sgemv.sgemvData;
import elina.utils.Evaluation;
import elina.utils.PartitionedTimer;

public class Sgemv {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int EXECS = 1;

		if(args.length==2)
		{
			sgemvData.Matrix_sizes[0]=Integer.parseInt(args[1]);
			EXECS=Integer.parseInt(args[0]);
		}
		PartitionedTimer.NEXECS=EXECS;

		int N = sgemvData.Matrix_sizes[0];
		int M = N;

		sgemvData mdata = new sgemvData();

		float[][] a = mdata.createMatrix(M, N);
		float[] x = mdata.createMatrix(1,M)[0];
		float[] y = new float[N];
		float alpha = mdata.generateScalar();

		Evaluation eval = new Evaluation(EXECS); 

		eval.writeAppName("SgemvSequential", N);
		for (int j = 0; j < EXECS; j++) {
			eval.startTimer();
			compute(a, x, y, alpha);
			eval.stopTimer();
			eval.writeTime();
			
			//useless?
			System.gc();
		}
		eval.writeAverage();
		eval.writeSTDV();
	}

	private static void compute(float[][] a, float[] x, float[] y, float alpha) {
		int iBeginA = 0;
		int iEndA = a.length;
		
		for(int J=0;J<a[0].length/(iEndA-iBeginA);J++)
			for (int i = iBeginA; i < iEndA; i++ )
			{
				float temp = 0.0f;
				for (int j = J*(iEndA-iBeginA); j < (J+1)*(iEndA-iBeginA); j++ )
					temp += a[i][j] * x[j];
				y[i] = alpha * temp + y[i]; 
			}
	}

}
