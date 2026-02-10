package javaGrande.elina.multicore.sparsematmult;

import service.SOMDTask;

public class SparseMatmultTask extends SOMDTask<Void> {

	private static final long serialVersionUID = 1L;

	private int NUM_ITERATIONS;
	private double val[];
	private int row[];
	private int col[];
	private double x[];
	private double[] yt;

	public SparseMatmultTask(double yt[], double val[], int row[], int col[],
			double x[], int NUM_ITERATIONS) {
		this.yt = yt;
		this.val = val;
		this.row = row;
		this.col = col;
		this.x = x;
		this.NUM_ITERATIONS = NUM_ITERATIONS;

	}

	@Override
	public Void call(Object[] partitions) {
		
		int ilow = ((int[]) partitions[0])[0];
		int iupper =  ((int[]) partitions[0])[1];
		
		for (int reps = 0; reps < NUM_ITERATIONS; reps++) {
			for (int i = ilow; i < iupper; i++) {
				yt[row[i]] += x[col[i]] * val[i];
			}
		}
		return null;
	}

}
