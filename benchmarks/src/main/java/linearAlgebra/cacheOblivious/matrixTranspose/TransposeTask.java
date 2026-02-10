package linearAlgebra.cacheOblivious.matrixTranspose;

import service.SOMDTask;

public class TransposeTask extends SOMDTask<float[]> {

	private static final long serialVersionUID = 1L;

	private int[][] A, B;

	public TransposeTask(int[][] A, int[][] B) {
		this.A = A;
		this.B = B;
	}

	@Override
	public float[] call(Object[] partition) {
		int iBegin = ((int[][]) partition[0])[0][0];
		int iEnd = ((int[][]) partition[0])[0][1];
		int jBegin = ((int[][]) partition[0])[1][0];
		int jEnd = ((int[][]) partition[0])[1][1];
		//System.out.println("i="+iBegin+"-"+iEnd+" j="+jBegin+"-"+jEnd);
		transpose(iBegin,iEnd,jBegin,jEnd);
		return null;
	}
	
	private void transpose(int iBegin, int iEnd, int jBegin, int jEnd)
	{
		int m = iEnd-iBegin;
		int n = jEnd-jBegin;
		if(m==n && m==1)
			B[jBegin][iBegin] = A[iBegin][jBegin];
		else if(n>=m)
		{
			transpose(iBegin,iEnd,jBegin, jBegin + n/2);
			transpose(iBegin,iEnd,jBegin+n/2, jEnd);
		}
		else //m > n
		{
			transpose(iBegin,iBegin+m/2,jBegin, jEnd);
			transpose(iBegin+m/2,iEnd,jBegin, jEnd);
		}
	}

}
