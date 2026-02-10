package javaGrande.elina.multicore.sparsematmult;

import service.Service;
import elina.distributions.IndexDist;
import elina.utils.SizeOf;

public class SparseMatmultProviderIndexDist extends Service implements
		SparseMatmultService {

	private static final long serialVersionUID = 1L;

	public void test(double y[], double val[], int row[],
			int col[], double x[], int NUM_ITERATIONS) {
			
		dist(new SparseMatmultTask(
				y, val, row, col,
				x, NUM_ITERATIONS),new IndexDist(row.length, SizeOf.Double, SizeOf.Double));

	}
}
