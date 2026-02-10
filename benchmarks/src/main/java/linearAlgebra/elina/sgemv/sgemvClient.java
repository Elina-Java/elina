package linearAlgebra.elina.sgemv;


import java.io.IOException;

import elina.utils.Evaluation;




public class sgemvClient  {

	private final sgemvService  service;
	private final int EXECS;

	public sgemvClient(sgemvService service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {
		
		int N = sgemvData.Matrix_sizes[0];
		int M = N;
		
		sgemvData mdata = new sgemvData();
		
		float[][] a = mdata.createMatrix(M, N);
		float[] x = mdata.createMatrix(1,M)[0];
		float alpha = mdata.generateScalar();
		float[] y = new float[N];
		
		for (int i = 0; i < sgemvData.NUMBER_TESTS; i++) {

			Evaluation eval = new Evaluation(EXECS); 

			eval.writeAppName("sgemv", N);
			for (int j = 0; j < EXECS; j++) {
				eval.startTimer();
				service.compute(a, x, y, alpha);
				eval.stopTimer();
				eval.writeTime();
				System.gc();
			}
			eval.writeAverage();
			eval.writeSTDV();
		}
		System.out.println("number its " + sgemvTask.counter);
	}
	
}
