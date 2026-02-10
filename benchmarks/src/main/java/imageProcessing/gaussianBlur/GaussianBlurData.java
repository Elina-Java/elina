package imageProcessing.gaussianBlur;

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

public class GaussianBlurData {

	static final int NUMBER_TESTS = 1;
	
	private static final long RANDOM_SEED = 10101010;
	public static final int[] Matrix_sizes = { 30 /*, 100000, 500000*/ };
	
	private static Random r = new Random(RANDOM_SEED);
	
	public static double[][] createMatrix(int size)
	{
		double[][] matrix = new double[size][size];
		
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
				matrix[i][j] = r.nextDouble();
		
		return matrix;
	}
	
}
