package javaGrande.elina.multicore.sor;

import java.util.concurrent.BrokenBarrierException;

import service.SynchedSOMDTask;

public class SORTaskUnRoll extends SynchedSOMDTask<Double> {

	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 1L;
	
	
	private final double omega;
	private final double[][] G;
	private final int n_iters;

	public SORTaskUnRoll(double omega, double[][] g, int n_iters) {
		this.omega = omega;
		this.G = g;
		this.n_iters = n_iters;
	}

	@Override
	public Double call(Object[] partition)  {

		int ilow = ((int[]) partition[0])[0];
		int iupper =  ((int[]) partition[0])[1];
		
			
		System.out.println((ilow+1) + " ----" +  (iupper-1));
		
		double omega_over_four = omega * 0.25;
		double one_minus_omega = 1.0 - omega;

		double partial_result = 0;

		for (int p = 0; p < 2* this.n_iters; p++) {
			for (int i = Math.max(1, ilow) + (p % 2); i < Math.min(iupper,G.length-1); i+=2) {
				double[] Gi = G[i];
				double[] Gim1 = G[i - 1];
				double[] Gip1 = G[i + 1];
				for (int j = 1; j < G[0].length-1; j++) {
					Gi[j] = omega_over_four
							* (Gim1[j] + Gip1[j] + Gi[j - 1] + Gi[j + 1])
							+ one_minus_omega * Gi[j];
					

					if ((i - 2) >1 && (j + 1) != G[0].length-1) {
						Gim1[j + 1] = omega_over_four
								* (G[i - 2][j + 1] + Gi[j + 1] + Gim1[j] + Gim1[j + 2])
								+ one_minus_omega * Gim1[j + 1];
					}
				}
			}
			try {
				this.barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
			
		
		}

		for (int i = Math.max(1, ilow); i < Math.min(G.length-1,iupper); i++) {
			for (int j = 1; j < G[0].length-1; j++) {
				partial_result += G[i][j];
			}
		}

		return partial_result;
	}

}
