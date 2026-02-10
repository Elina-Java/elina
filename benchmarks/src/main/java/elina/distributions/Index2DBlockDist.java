package elina.distributions;

import core.collective.AbstractDistribution;

public class Index2DBlockDist extends AbstractDistribution<int[][]> {

	protected final int rows;
	protected final int cols;

	public int div;
	public int nparts;

	public Index2DBlockDist(int rows, int cols, int unitSize, int elementarySize) {
		super(unitSize, elementarySize);
		this.rows=rows;
		this.cols=cols;

	}

	/**
	 * Assumes at least one previous call of setPartitions.
	 */
	public int[][][] distribution(final int nparts) {
		div = (int) Math.round(Math.sqrt(nparts));

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
	
	@Override
	public float getAveragePartitionSize(int nParts) {
		float sqrt = (float) Math.sqrt(nParts);
		return (rows/sqrt)*(cols/sqrt);
	}

	@Override
	public float getAverageLineSize(int nParts) {
		float sqrt = Math.round(Math.sqrt(nParts));
		if(sqrt==Math.sqrt(nParts))
			return rows/sqrt;
		else
			return 0;
	}
}
