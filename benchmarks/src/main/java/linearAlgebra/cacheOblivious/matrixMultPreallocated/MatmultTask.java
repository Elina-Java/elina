package linearAlgebra.cacheOblivious.matrixMultPreallocated;

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

		recmult(iBeginA,iEndA,jBeginA,jEndA,iBeginB,jBeginB,jEndB,result);
		return result;
	}

	public void recmult(int iBeginA, int iEndA, int jBeginA, int jEndA, 
			int iBeginB, int jBeginB, int jEndB, int[][] result)
	{
		int m = iEndA-iBeginA;
		int n = jEndA-jBeginA;
		int p = jEndB-jBeginB;
		
		if(m==n && n==p && p==1)
			result[iBeginA][jBeginB]+=A[iBeginA][jBeginA]*B[iBeginB][jBeginB];
		if(m>=max(n,p))
		{
			recmult(iBeginA,iBeginA+m/2,jBeginA,jEndA,iBeginB,jBeginB,jEndB,result);
			recmult(iBeginA+m/2,iEndA,jBeginA,jEndA,iBeginB,jBeginB,jEndB,result);
		}
		else if(n>=max(m,p))
		{
			recmult(iBeginA,iEndA,jBeginA,jBeginA+n/2,iBeginB,jBeginB,jEndB,result);
			recmult(iBeginA,iEndA,jBeginA+n/2,jEndA,iBeginB+n/2,jBeginB,jEndB,result);
		}
		else
		{
			recmult(iBeginA,iEndA,jBeginA,jEndA,iBeginB,jBeginB,jBeginB+p/2,result);
			recmult(iBeginA,iEndA,jBeginA,jEndA,iBeginB,jBeginB+p/2,jEndB,result);
		}
	}

	private int max(int a, int b)
	{
		if(a>b)
			return a;
		else
			return b;
	}
}
