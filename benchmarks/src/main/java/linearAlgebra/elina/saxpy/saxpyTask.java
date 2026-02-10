package linearAlgebra.elina.saxpy;

import service.SOMDTask;

public class saxpyTask extends SOMDTask<float[]> {

	private static final long serialVersionUID = 1L;

	private float[] x, y;
	private float a;

	public saxpyTask(float[] x, float[] y, float a) {
		this.x = x;
		this.y = y;
		this.a = a;
	}

	public float[] call(Object[] partition) {
		int beginIndex = ((int[])partition[0])[0];
		int endIndex = ((int[])partition[0])[1];
		
		//System.out.println(Thread.currentThread().getName()+"-"+beginIndex+"-"+endIndex);
		
		for (int i=beginIndex; i < endIndex; i++ )
			y[i] += a * x[i];
		return null;
	}
}
