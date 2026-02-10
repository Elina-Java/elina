package imageProcessing.conv2d;

import service.IService;

public interface Conv2DService extends IService {
	
	void convolution(float[][] a, float[][] h, float[][] c, int U);
	
}