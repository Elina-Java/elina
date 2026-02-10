package javaGrande.elina.multicore.sparsematmult;


import service.IService;

public interface SparseMatmultService extends IService {

	
	void test(double y[], double val[], int row[],
			int col[], double x[], int NUM_ITERATIONS);
	
	
}
