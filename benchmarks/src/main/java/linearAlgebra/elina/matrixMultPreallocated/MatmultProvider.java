package linearAlgebra.elina.matrixMultPreallocated;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Reduction;
import elina.combinations.MatrixDistsCombinationPreallocated;
import elina.distributions.Index2DBlockDist;
import elina.distributions.PreallocatedMatricesDist;
import elina.reductions.MatrixReduceBlocks;

@DistRed
public class MatmultProvider extends Service {

	private static final long serialVersionUID = 1L;

	public void mult(int[][] A, int[][] B, int[][] C, PreallocatedMatricesDist cdist) {
		//Distributions
		Index2DBlockDist adist = new Index2DBlockDist(A.length,A[0].length,1,4);
		Index2DBlockDist bdist = new Index2DBlockDist(B.length,B[0].length,1,4);
		
		MatrixDistsCombinationPreallocated<int[][]> combins = 
				new MatrixDistsCombinationPreallocated<int[][]>(adist,bdist);
				
		Reduction<int[][]> red = new MatrixReduceBlocks(C);
		distReduce(new MatmultTask(A,B), red, combins, adist, bdist, cdist).get();
	}
}
