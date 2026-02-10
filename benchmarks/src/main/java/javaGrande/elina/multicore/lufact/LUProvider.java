package javaGrande.elina.multicore.lufact;

import service.Service;
import elina.distributions.IndexDistOffset;
import elina.utils.SizeOf;

public class LUProvider extends Service implements LUService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void lufact(double a[][], int lda, int n, int ipvt[]) {
		double[] col_k;
		double t;
		int k, kp1, l, nm1;
		int info;

		// gaussian elimination with partial pivoting

		info = 0;
		nm1 = n - 1;

		if (nm1 >= 0) {
			for (k = 0; k < nm1; k++) {
				col_k = a[k];
				kp1 = k + 1;

				// find l = pivot index

				l = JavaGrandeData.idamax(n - k, col_k, k, 1) + k;
				ipvt[k] = l;

				// zero pivot implies this column already triangularized

				if (col_k[l] != 0) {
					if (l != k) {
						t = col_k[l];
						col_k[l] = col_k[k];
						col_k[k] = t;
					}

					// compute multipliers

					t = -1.0 / col_k[k];
					JavaGrandeData.dscal(n - (kp1), t, col_k, kp1, 1);

					// row elimination with column indexing

					dist(new LUTask(a, n, k, col_k,	l), new IndexDistOffset(n - kp1, kp1, n, SizeOf.Double));

					
					
				} else {
					info = k;
				}

			}
		}
		ipvt[n - 1] = n - 1;

		if (a[(n - 1)][(n - 1)] == 0)
			info = n - 1;

		return;

	}
}
