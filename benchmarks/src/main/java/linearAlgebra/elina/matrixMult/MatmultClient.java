package linearAlgebra.elina.matrixMult;


import java.io.IOException;

import elina.utils.Evaluation;


public class MatmultClient  {

	private final MatmultService  service;
	private final int EXECS;

	public MatmultClient(MatmultService service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {

		int size = MatrixData.Matrix_sizes[0];

		MatrixData mdata = new MatrixData();

		int[][] A = mdata.createMatrix(size);
		int[][] B = mdata.createMatrix(size);
		int[][] C = new int[A[0].length][B.length];
		
		Evaluation eval = new Evaluation(EXECS); 
		eval.writeAppName("Matmult", ""+size);
		for (int j = 0; j < EXECS; j++) {
			eval.startTimer();
			service.mult(A, B, C);
			eval.stopTimer();
			eval.writeTime();
			System.gc();
		}
		eval.writeAverage();
		eval.writeSTDV();

		eval.end();
	}
}
