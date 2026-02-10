package imageProcessing.gaussianBlur;

public class GaussianBlurUtils {

	public static double[][] createWeightMatrix(int n, int m, double sigma)
	{
		double[][] weightMatrix = new double[n][m];
		
		for(int i=0;i<n;i++)
			for(int j=0;j<m;j++)
			{
				weightMatrix[i][j] = gaussian(i,j,sigma);
			}
		return weightMatrix;
	}

	public static double gaussian(int x, int y, double sigma) {
		return (1/(2*Math.PI*sigma*sigma))*Math.pow(Math.E, -(x*x+y*y)/(2*sigma*sigma));
	}

}
