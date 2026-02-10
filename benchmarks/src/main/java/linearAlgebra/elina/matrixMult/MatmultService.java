package linearAlgebra.elina.matrixMult;

import service.IService;

public interface MatmultService extends IService {
	
	void mult(int[][] A, int[][] B, int[][] C);
	
}