package javaGrande.elina.multicore.lufact;

import service.SOMDTask;

public class LUTask extends SOMDTask<Void> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	double a[][];
	final int n;
	final int k;
	final int l;
	final double[] col_k;

	public LUTask(double a[][], int n, int k, double[] col_k, int l) {
		this.a = a;
		this.col_k = col_k;
		this.n = n;
		this.k = k;
		this.l = l;
	}

	@Override
	public Void call(Object[] partition) {
		int kp1 = k + 1;

		int ilow = ((int[]) partition[0])[0];
		int iupper =  ((int[]) partition[0])[1];
	
		
		for (int j = ilow; j < iupper; j++) {
			double[] col_j = a[j];
			double t = col_j[l];

			if (l != k) {
				col_j[l] = col_j[k];
				col_j[k] = t;
			}

			JavaGrandeData.daxpy(n - (kp1), t, col_k, kp1, 1, col_j, kp1, 1);
		}
		return null;
	}

}
