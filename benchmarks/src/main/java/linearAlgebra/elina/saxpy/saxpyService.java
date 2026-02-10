package linearAlgebra.elina.saxpy;

import service.IService;

public interface saxpyService extends IService {
	
	void compute(float[] x, float[] y, float a);
	
}