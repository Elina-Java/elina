package linearAlgebra.elina.matrixMult;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Distribution;
import core.collective.DistributionsCombination;
import core.collective.Reduction;
import elina.combinations.MatrixDistsCombination;
import elina.combinations.MatrixDistsCombinationTuned;
import elina.distributions.Index2DBlockDist;
import elina.distributions.flatTuned.MatrixBlockCombDist;
import elina.reductions.MatrixReduceBlocks;


@DistRed
public class MatmultProvider extends Service implements MatmultService {

	private static final long serialVersionUID = 1L;

	public void mult(int[][] A, int[][] B, int[][] C) {
		
		//Distributions
		Index2DBlockDist adist = new Index2DBlockDist(A.length,A[0].length,1,4);
		Index2DBlockDist bdist = new Index2DBlockDist(B.length,B[0].length,1,4);
		DistributionsCombination dist = new MatrixDistsCombination<int[][]>(adist,bdist);
		
		/*
		Distribution<int[][]> adist = new MatrixBlockCombDist(A.length,A[0].length,1,4);
		Distribution<int[][]> bdist = new MatrixBlockCombDist(B.length,B[0].length,1,4);
		DistributionsCombination dist = new MatrixDistsCombinationTuned<int[][]>(adist,bdist);
		*/
		
		Reduction<int[][]> red = new MatrixReduceBlocks(C);
		
		//Reduction
		distReduce(new MatmultTask(A,B), red, dist, dist.getDistributions()).get();
	}
}
