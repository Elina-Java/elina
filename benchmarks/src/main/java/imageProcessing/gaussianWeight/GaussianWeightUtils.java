package imageProcessing.gaussianWeight;

public class GaussianWeightUtils {

	public static void computeWeightMatrix(double[][] weightMatrix, double sigma) {
		for(int i=0;i<weightMatrix.length;i++)
			for(int j=0;j<weightMatrix[0].length;j++)
				weightMatrix[i][j] = gaussian(i,j,sigma);
	}
	
	public static double gaussian(int x, int y, double sigma) {
		return (1/(2*Math.PI*sigma*sigma))*Math.pow(Math.E, -(x*x+y*y)/(2*sigma*sigma));
	}

}
