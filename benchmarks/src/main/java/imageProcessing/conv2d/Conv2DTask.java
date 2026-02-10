package imageProcessing.conv2d;

import service.SOMDTask;

public class Conv2DTask extends SOMDTask<float[][]> {

	private static final long serialVersionUID = 1L;

	private float[][] a, h, c;
	private int U;

	public Conv2DTask(float[][] a, float[][] h, float[][] c, int U) {
		this.a = a;
		this.h = h;
		this.c = c;
		this.U = U;
	}

	@Override
	public float[][] call(Object[] partition) {
		int iBeginA = ((int[][]) partition[0])[0][0];
		int iEndA = ((int[][]) partition[0])[0][1] - U +1;
		int jBeginA = ((int[][]) partition[0])[1][0];
		int jEndA = ((int[][]) partition[0])[1][1] - U +1;

		for (int n = iBeginA; n < iEndA; n++ )
			for (int m = jBeginA; m < jEndA; m++ )
			{
				c[n][m] = 0.0f;
				for (int u = 0; u < U; u++ )
					for (int v = 0; v < U; v++ )
						c[n][m] += h[u][v] * a[n+u][m+v];
			}
		return null;
	}

}
