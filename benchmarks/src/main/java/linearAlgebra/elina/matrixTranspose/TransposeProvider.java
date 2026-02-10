package linearAlgebra.elina.matrixTranspose;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Distribution;
import elina.distributions.Index2DBlockDist;
import elina.distributions.flatTuned.MatrixBlockDist;


@DistRed
public class TransposeProvider extends Service implements TransposeService {

	private static final long serialVersionUID = 1L;

	public void transpose(int[][] A, int[][] B) {
		
		//Distributions (B is the result)
		
		Distribution<int[][]> aDist = new Index2DBlockDist(A.length,A[0].length,1,4);
		Distribution<int[][]> bDist = new Index2DBlockDist(B.length,B[0].length,1,4);
		
		/*
		Distribution<int[][]> aDist = new MatrixBlockDist(A.length,A[0].length,1,4);
		Distribution<int[][]> bDist = new MatrixBlockDist(B.length,B[0].length,1,4);
		*/
		dist(new TransposeTask(A,B), aDist,bDist);
	}
}
