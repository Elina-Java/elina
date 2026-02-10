package imageProcessing.gaussianWeight;

import service.IService;

public interface GaussianWeightService extends IService {
	
	void computeGaussianWeight(double[][] weightMatrix, double sigma);
	
}