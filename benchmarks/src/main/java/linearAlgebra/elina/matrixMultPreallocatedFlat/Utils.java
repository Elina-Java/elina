package linearAlgebra.elina.matrixMultPreallocatedFlat;

import core.collective.Distribution;
import drivers.Adapters;
import elina.combinations.MatrixDistsCombinationTuned;

public class Utils {

	public static int[][][] createResultMatricesArray(int[][] A, int[][] B, MatrixDistsCombinationTuned<int[][]> combin)
	{
		int nparts = Adapters.getTaskExecutor().getNumberOfWorkers();
		
		Distribution<int[][]>[] dists = combin.getDistributions();
		dists[0].setPartitions(nparts);
		int[][][] aDists = dists[0].distribution(nparts);
		dists[1].setPartitions(nparts);
		int[][][] bDists = dists[1].distribution(nparts);
		
		int[][] combins = combin.getCombinations();
		int[][][] results = new int[combins.length][][];
		
		
		for(int i=0;i<combins.length;i++)
		{
			int[] partition = combins[i];
			int[][] aPart = aDists[partition[0]];
			int[][] bPart = bDists[partition[1]];
			
			int iBeginA = aPart[0][0];
			int iEndA = aPart[0][1];
			int jBeginB = bPart[1][0];
			int jEndB = bPart[1][1];
			results[i] = new int[iEndA-iBeginA][jEndB-jBeginB];
		}
		
		return results;
	}
}
