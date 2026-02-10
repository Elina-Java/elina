package imageProcessing.gaussianWeight;

import service.SOMDTask;

public class GaussianWeightTask extends SOMDTask<Void> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[][] w;
	private double sigma;

	public GaussianWeightTask(double[][] weightMatrix, double sigma) {
		this.w = weightMatrix;
		this.sigma=sigma;
	}

	@Override
	public Void call(Object[] partition) {
		int iBeginW = ((int[][]) partition[0])[0][0];
		int iEndW = ((int[][]) partition[0])[0][1];
		int jBeginW = ((int[][]) partition[0])[1][0];
		int jEndW = ((int[][]) partition[0])[1][1];

		for(int i=iBeginW;i<iEndW;i++)
			for(int j=jBeginW;j<jEndW;j++)
				w[i][j] = GaussianWeightUtils.gaussian(i,j,sigma);
		
		return null;
	}

}
