package linearAlgebra.elina.matrixTranspose;

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
		int iBeginA = ((int[][]) partition[0])[0][0];
		int iEndA = ((int[][]) partition[0])[0][1];
		int jBeginA = ((int[][]) partition[0])[1][0];
		int jEndA = ((int[][]) partition[0])[1][1];
		
		for (int i=iBeginA ; i < iEndA; i++)
		{
			for (int j=jBeginA; j < jEndA; j++)
			{
				B[j][i] = A[i][j];
			}
		}
		return null;
	}

}
