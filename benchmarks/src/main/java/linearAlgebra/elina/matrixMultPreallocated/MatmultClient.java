package linearAlgebra.elina.matrixMultPreallocated;


import java.io.IOException;

import linearAlgebra.elina.matrixMult.MatrixData;

import elina.combinations.MatrixDistsCombination;
import elina.distributions.Index2DBlockDist;
import elina.distributions.PreallocatedMatricesDist;
import elina.utils.Evaluation;


public class MatmultClient  {

	private final MatmultProvider  service;
	private final int EXECS;

	public MatmultClient(MatmultProvider service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {

		int m = MatrixData.Matrix_sizes[0];
		int n = MatrixData.Matrix_sizes[1];
		int p = MatrixData.Matrix_sizes[2];

		MatrixData mdata = new MatrixData();

		/*
		int[][] A = mdata.createMatrix(size);
		int[][] B = mdata.createMatrix(size);
		int[][] C = new int[A[0].length][B.length];
		*/
		int[][] A = mdata.createMatrix(m,n);
		int[][] B = mdata.createMatrix(n,p);
		int[][] C = new int[m][p];

		/**/
		Index2DBlockDist adist = new Index2DBlockDist(A.length,A[0].length,1,4);
		Index2DBlockDist bdist = new Index2DBlockDist(B.length,B[0].length,1,4);
		MatrixDistsCombination<int[][]> combin = new MatrixDistsCombination<int[][]>(adist,bdist);

		int[][][] results = Utils.createResultMatricesArray(A,B,combin);

		//PreallocatedMatricesDist cdist = new PreallocatedMatricesDist(results,results.length,1,0);
		PreallocatedMatricesDist cdist = new PreallocatedMatricesDist(results,0,1,1);
		
		Evaluation eval = new Evaluation(EXECS); 
		eval.writeAppName("Matmult Preallocated", m+" x "+n+" x "+ p);
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
