package elina.distributions.flatTuned;

import drivers.Adapters;
import elina.distributions.Index2DBlockDist;
import elina.utils.PartitionedTimer;

/**
 * For use with Flat Tuned matrix multiplication
 * @author nuno
 *
 */
public class MatrixBlockCombDist extends Index2DBlockDist {

	public MatrixBlockCombDist(int rows, int cols, int unitSize, int elementarySize) {
		super(rows, cols, unitSize, elementarySize);
	}
	
	/**
	 * Assumes at least one previous call of setPartitions.
	 */
	public int[][][] distribution() {
		
		PartitionedTimer.isFlatTuned = true;
		//number of workers, at this point, for flat situations
		nparts = getPartitions();
		
		for(int i=1;i<Integer.MAX_VALUE;i++)
		{
			int n = nparts*i;
			double tmp = Math.cbrt(n);	
			if(Math.ceil(tmp)==tmp && n%Adapters.getTaskExecutor().getNumberOfWorkers()==0)
			{
				nparts*=i;
				div = (int) Math.round(Math.cbrt(nparts));
				break;
			}
		}
		
		int[][][] dist = new int[nparts][2][2];

		//Chunks
		int rowsPerPart = rows/div;
		int colsPerPart = cols/div;
		
		//Remainders
		int rowsRemainder = rows%div;
		int colsRemainder = cols%div;

		/**
		 * Order: 1 2 3
		 * 		  4 5 6
		 * 		  7 8 9 ...
		 */
		
		int iBegin=0;
		int iEnd=0;
		for(int i=0;i<div;i++)
		{
			iEnd = iBegin + rowsPerPart + (rowsRemainder - i > 0 ? 1 : 0);
			int jBegin=0;
			int jEnd=0;
			for(int j=0;j<div;j++)
			{
				jEnd = jBegin + colsPerPart + (colsRemainder - j > 0 ? 1 : 0);
				int[][] part = dist[i*div+j];
				part[0][0]=iBegin;
				part[0][1]=iEnd;
				part[1][0]=jBegin;
				part[1][1]=jEnd;
				jBegin = jEnd;
			}
			iBegin = iEnd;
		}
		
		return dist;
	}

	public int getNumberOfElements() {
		return cols*rows;
	}
}
