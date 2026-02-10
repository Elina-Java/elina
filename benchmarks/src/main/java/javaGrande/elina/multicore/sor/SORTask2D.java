package javaGrande.elina.multicore.sor;

import java.util.concurrent.BrokenBarrierException;

import service.SynchedSOMDTask;


public class SORTask2D extends SynchedSOMDTask<Void> {

	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 1L;


	private final double omega;
	private final double[][] G;
	private final int n_iters;

	public SORTask2D(double omega, double[][] g, int n_iters) {
		this.omega = omega;
		this.G = g;
		this.n_iters = n_iters;
	}

	@Override
	public Void call(Object[] partition) {

		int ilow = ((int[]) partition[0])[0];
		int iupper =  ((int[]) partition[0])[1];
		int rilow = ((int[]) partition[0])[2];
		int riupper = ((int[]) partition[0])[3];

		//	System.out.println("ilow " +ilow+ "  iupper " + iupper + " \n"+
		//			"rilow " + rilow + "  riupper " + riupper);
		double omega_over_four = omega * 0.25;
		double one_minus_omega = 1.0 - omega;


		for (int p = 0; p < this.n_iters; p++) {

			for (int i=Math.max(ilow,1); i<Math.min(iupper, G.length-1); i++)
			{
				double [] Gi = G[i];
				double [] Gim1 = G[i-1];
				double [] Gip1 = G[i+1];
				for (int j=Math.max(rilow,1); j<Math.min(riupper, G[0].length-1); j++)
					Gi[j] = omega_over_four * (Gim1[j] + Gip1[j] + Gi[j-1] 
							+ Gi[j+1]) + one_minus_omega * Gi[j];
			}

			try {
				System.out.println("barrier");
				barrier.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;

	}

}
