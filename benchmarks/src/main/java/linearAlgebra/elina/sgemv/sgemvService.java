package linearAlgebra.elina.sgemv;

import service.IService;

public interface sgemvService extends IService {
	
	void compute(float[][] a, float[] x, float[] y, float alpha);
	
}