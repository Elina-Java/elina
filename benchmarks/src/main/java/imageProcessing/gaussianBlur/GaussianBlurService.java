package imageProcessing.gaussianBlur;

import service.IService;

public interface GaussianBlurService extends IService {
	
	void gaussianBlur(double[][] weightMatrix, int radius, double[][] t0, double[][] t1);
	
}