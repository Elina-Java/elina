package linearAlgebra.elina.sgemv;

import service.SOMDTask;

public class sgemvTask extends SOMDTask<float[][]> {
	static Object lock = new Object();
	static long counter =  0;
	
	
	private static final long serialVersionUID = 1L;

	private float[][] a;
	private float[] x, y;
	private float alpha;

	public sgemvTask(float[][] a, float[] x, float[] y, float alpha) {
		this.a = a;
		this.x = x;
		this.y = y;
		this.alpha = alpha;
	}

	@Override
	public float[][] call(Object[] partition) {
		int iBeginA = ((int[]) partition[0])[0];
		int iEndA = ((int[]) partition[0])[1];
		
		for(int J=0;J<a[0].length/(iEndA-iBeginA);J++)
			for (int i = iBeginA; i < iEndA; i++ )
			{
				float temp = 0.0f;
				for (int j = J*(iEndA-iBeginA); j < (J+1)*(iEndA-iBeginA); j++ )
					temp += a[i][j] * x[j];
				y[i] = alpha * temp + y[i]; 
			}
		return null;
	}
}
