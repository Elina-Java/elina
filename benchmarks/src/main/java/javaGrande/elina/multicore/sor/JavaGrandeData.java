package javaGrande.elina.multicore.sor;

import java.util.Random;

public final class JavaGrandeData {
	
	static final int JACOBI_NUM_ITER = 100;
	 
	static int[] sizes = new int[] {
			1000, 1500, 2000 // reference JavaGrande configurations (A, B, C)
		};
	public static int NUMBER_OF_PROBLEMS = sizes.length;
	
	private static final long RANDOM_SEED = 10101010;
	
	static double[][] getRandomMatrix(int pclass) {
		int N = sizes[pclass];
		int M = N;
		double A[][] = new double[M][N];
		Random R = new Random(RANDOM_SEED);

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				A[i][j] = R.nextDouble() * 1e-6;
			}
		return A;
	}

	static void validate(double sol, int size) {

		double refval[] = { 0.4984199298207158, 1.123010681492097,
				1.9967774998523777 };
		double dev = Math.abs(sol - refval[size]);
		if (dev > 1.0e-12) {
			System.out.println("Validation failed");
			System.out.println("Gtotal = " + sol + "  " + dev + "  " + size);
		}

	}
}
