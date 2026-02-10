package linearAlgebra.elina.matrixMultPreallocatedFlat;


import java.io.IOException;

import linearAlgebra.elina.matrixMult.MatrixData;

import elina.combinations.MatrixDistsCombinationTuned;
import elina.distributions.PreallocatedMatricesDist;
import elina.distributions.flatTuned.MatrixBlockCombDist;
import elina.utils.Evaluation;


public class MatmultClient  {

	private final MatmultProvider  service;
	private final int EXECS;

	public MatmultClient(MatmultProvider service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {

		int size = MatrixData.Matrix_sizes[0];

		MatrixData mdata = new MatrixData();

		int[][] A = mdata.createMatrix(size);
		int[][] B = mdata.createMatrix(size);
		int[][] C = new int[A[0].length][B.length];

		/**/
		MatrixBlockCombDist adist = new MatrixBlockCombDist(A.length,A[0].length,1,4);
		MatrixBlockCombDist bdist = new MatrixBlockCombDist(B.length,B[0].length,1,4);
		MatrixDistsCombinationTuned<int[][]> combin = new MatrixDistsCombinationTuned<int[][]>(adist,bdist);

		int[][][] results = Utils.createResultMatricesArray(A,B,combin);

		PreallocatedMatricesDist cdist = 
				new PreallocatedMatricesDist(results,results.length,1,0);
		/**/
		Evaluation eval = new Evaluation(EXECS); 

		eval.writeAppName("Matmult Preallocated", ""+size);
		for (int j = 0; j < EXECS; j++) {
			eval.startTimer();
			service.mult(A, B, C, cdist);
			eval.stopTimer();
			eval.writeTime();
			System.gc();
		}
		eval.writeAverage();
		eval.writeSTDV();

		eval.end();
	}
}
