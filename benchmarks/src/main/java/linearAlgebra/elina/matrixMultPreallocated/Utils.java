package linearAlgebra.elina.matrixMultPreallocated;

import core.collective.Distribution;
import drivers.Adapters;
import drivers.PartitioningDriver;
import elina.combinations.MatrixDistsCombination;

public class Utils {

	public static int[][][] createResultMatricesArray(int[][] A, int[][] B, MatrixDistsCombination<int[][]> combin)
	{
		PartitioningDriver pd = Adapters.getPartitioningDriver();
		int nparts = pd.getNparts(combin.getDistributions(),
				Adapters.getHierarchyReadDriver().getHierarchyRoot(),
				new linearAlgebra.elina.matrixMult.MatmultTask(A,B));
		nparts = Math.max(nparts, Adapters.getTaskExecutor().getNumberOfWorkers());
		
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
