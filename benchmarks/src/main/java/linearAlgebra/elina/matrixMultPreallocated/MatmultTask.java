package linearAlgebra.elina.matrixMultPreallocated;

import service.SOMDTask;

public class MatmultTask extends SOMDTask<int[][]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[][] A, B;

	public MatmultTask(int[][] A, int[][] B) {
		this.A = A;
		this.B = B;
	}
	
	
	@Override
	public int getDynamicDataSize(int nparts) {
		int div;
		
		while(true)
		{
			double tmp = Math.round(Math.sqrt(nparts));
			if(tmp==Math.sqrt(nparts))
			{
				div = (int) Math.round(Math.sqrt(nparts));
				break;
			}
			nparts++;
		}
		
		//Chunks
		int rowsPerPart = A.length/div;
		int colsPerPart = B[0].length/div;
		
		// overestimated
		return  4 * (colsPerPart + 1) * (rowsPerPart + 1);
	}
	
	@Override
	public int[][] call(Object[] partition) {
		int iBeginA = ((int[][]) partition[0])[0][0];
		int iEndA = ((int[][]) partition[0])[0][1];
		int jBeginA = ((int[][]) partition[0])[1][0];
		int jEndA = ((int[][]) partition[0])[1][1];

		int iBeginB = ((int[][]) partition[1])[0][0];
		int jBeginB = ((int[][]) partition[1])[1][0];
		int jEndB = ((int[][]) partition[1])[1][1];

		int[][] result = ((int[][]) partition[2]);

		for(int i=0;i<iEndA-iBeginA;i++)
			for(int j=0;j<jEndB-jBeginB;j++)
			{
				result[i][j] = 0;
				for(int k=0;k<jEndA-jBeginA;k++)
					result[i][j] += A[i+iBeginA][k+jBeginA] * B[k+iBeginB][j+jBeginB];
			}
		return result;
	}

}
