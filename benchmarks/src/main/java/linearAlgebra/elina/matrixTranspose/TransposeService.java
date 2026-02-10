package linearAlgebra.elina.matrixTranspose;

import service.IService;

public interface TransposeService extends IService {
	
	void transpose(int[][] A, int[][] B);
	
}