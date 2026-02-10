package imageProcessing.blur;

import service.IService;

public interface BlurService extends IService {
	
	void blur(double[][] t0, double[][] t1);
	
}