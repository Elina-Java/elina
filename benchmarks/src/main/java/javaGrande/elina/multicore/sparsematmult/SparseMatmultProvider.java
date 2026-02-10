package javaGrande.elina.multicore.sparsematmult;

import instrumentation.definitions.DistRed;
import service.Service;


@DistRed
public class SparseMatmultProvider extends Service implements
		SparseMatmultService {

	private static final long serialVersionUID = 1L;

	public void test(double y[], double val[], int row[],
			int col[], double x[], int NUM_ITERATIONS) {
			
		dist(new SparseMatmultTask(
				y, val, row, col,
				x, NUM_ITERATIONS),new SparseDist(y, x, row, col, val));
	}
}
