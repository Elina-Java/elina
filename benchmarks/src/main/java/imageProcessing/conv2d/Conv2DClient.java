package imageProcessing.conv2d;


import java.io.IOException;

import elina.utils.Evaluation;




public class Conv2DClient  {

	private final Conv2DService  service;
	private final int EXECS;

	public Conv2DClient(Conv2DService service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {
		
		int N = Conv2DData.Matrix_sizes[0];
		int M = N;
		//will assume N=M
		//int M = 8192;
		//int S = 4096;
		//int T = 4096;
		//will assume U=V
		int U = 9;
		//int V = 9;
		Conv2DData mdata = new Conv2DData();
		
		float[][] a = mdata.createMatrix(N+U-1, N+U-1);
		float[][] h = mdata.createMatrix(U);
		float[][] c = new float[N][M];
		
		for (int i = 0; i < Conv2DData.NUMBER_TESTS; i++) {

			Evaluation eval = new Evaluation(EXECS); 

			eval.writeAppName("Conv2D", N);
			for (int j = 0; j < EXECS; j++) {
				eval.startTimer();
				service.convolution(a, h, c, U);
				eval.stopTimer();
				eval.writeTime();
				System.gc();
			}
			eval.writeAverage();
			eval.writeSTDV();

			eval.end();
		}
	}
	
}
