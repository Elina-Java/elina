package linearAlgebra.cacheOblivious.matrixTranspose;

import java.io.IOException;

import linearAlgebra.elina.matrixTranspose.TransposeData;
import linearAlgebra.elina.matrixTranspose.TransposeService;

import elina.utils.Evaluation;

public class TransposeClient {

	private final TransposeService  service;
	private final int EXECS;

	public TransposeClient(TransposeService service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {
		
		int m = TransposeData.MATRIX_SIZES[0];
		int n = TransposeData.MATRIX_SIZES[1];
		int[][] A = TransposeData.createMatrix(m,n);
		int[][] B = new int[n][m];
		
		for (int i = 0; i < TransposeData.NUMBER_TESTS; i++) {

			Evaluation eval = new Evaluation(EXECS); 

			eval.writeAppName("Transpose", m+" x "+ n);
			for (int j = 0; j < EXECS; j++) {
				
				eval.startTimer();
				service.transpose(A, B);
				eval.stopTimer();
				eval.writeTime();
				System.gc();
			}
			eval.writeAverage();
			eval.writeSTDV();
			
			eval.end();
		}
		/*TransposeData.printMatrix(A);
		System.out.println();
		TransposeData.printMatrix(B);*/
	}
	
}
