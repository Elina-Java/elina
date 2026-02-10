package javaGrande.elina.multicore.sparsematmult;

/**************************************************************************
 *                                                                         *
 *             Java Grande Forum Benchmark Suite - Version 2.0             *
 *                                                                         *
 *                            produced by                                  *
 *                                                                         *
 *                  Java Grande Benchmarking Project                       *
 *                                                                         *
 *                                at                                       *
 *                                                                         *
 *                Edinburgh Parallel Computing Centre                      *
 *                                                                         * 
 *                email: epcc-javagrande@epcc.ed.ac.uk                     *
 *                                                                         *
 *                                                                         *
 *      This version copyright (c) The University of Edinburgh, 1999.      *
 *                         All rights reserved.                            *
 *                                                                         *
 **************************************************************************/

import java.util.Random;

class JavaGrandeData {



	private static final long RANDOM_SEED = 10101010;

	private static final int datasizes_N[] = { 
			50000, 100000, 500000  // reference JavaGrande configurations (A, B, C)
		};
	private static final int datasizes_M[] = { 50000, 100000, 500000 };
	private static final int datasizes_nz[] = { 250000, 500000, 2500000 };
	static final int SPARSE_NUM_ITER = 200;

	static int NUMBER_OF_PROBLEMS = datasizes_M.length;
	
	private static Random R;  

	double[] x;
	double[] y;
	double[] val;
	int[] col;
	int[] row;


	
	JavaGrandeData(int size) {
		R = new Random(RANDOM_SEED);
		x = RandomVector(datasizes_N[size]);
		y = new double[datasizes_M[size]];
		val = new double[datasizes_nz[size]];
		col = new int[datasizes_nz[size]];
		row = new int[datasizes_nz[size]];

		for (int i = 0; i < datasizes_nz[size]; i++) {

			// generate random row index (0, M-1)
			row[i] = Math.abs(R.nextInt()) % datasizes_M[size];

			// generate random column index (0, N-1)
			col[i] = Math.abs(R.nextInt()) % datasizes_N[size];

			val[i] = R.nextDouble();

		}

	}

	static double[] RandomVector(int N) {
		double A[] = new double[N];

		for (int i = 0; i < N; i++)
			A[i] = R.nextDouble() * 1e-6;

		return A;
	}

	static void validate(double ytotal, int size) {
		double refval[] = {75.02484945753453,150.0130719633895,749.5245870753752};
	    double dev = Math.abs(ytotal - refval[size]);
	    if (dev > 1.0e-12 ){
	      System.out.println("Validation failed");
	      System.out.println("ytotal = " + ytotal + "  " + dev + "  " + size);
	    }

	}

}
