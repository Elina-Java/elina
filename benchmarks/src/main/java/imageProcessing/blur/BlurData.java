package imageProcessing.blur;

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

public class BlurData {

	static final int NUMBER_TESTS = 1;
	
	private static final long RANDOM_SEED = 10101010;
	public static final int[] Matrix_sizes = { 30 /*, 100000, 500000*/ };
	
	private Random r;
	
	public BlurData()
	{
		r = new Random(RANDOM_SEED);
	}
	
	public double[][] createMatrix(int size)
	{
		double[][] matrix = new double[size][size];
		
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
				matrix[i][j] = r.nextDouble();
		
		return matrix;
	}

	public static boolean validate(int[][] A, int[][] B, int[][] C, int testNumber) {
		
		int[][] resultMatrix = new int[A.length][B[0].length];
		for(int i=0; i < A.length; i++)
		{
			for(int j=0; j < B[0].length; j++)
			{
				for(int k=0; k < A[0].length; k++)
					resultMatrix[i][j] +=  A[i][k] * B [k][j];
			}
		}
		
		int sum = 0;
		for(int i=0;i<C.length;i++)
			for(int j=0;j<C[0].length;j++)
			{
				sum += C[i][j];
				if(C[i][j]!=resultMatrix[i][j])
					return false;
			}
		
		return true;
		//System.out.println("Sum: " + sum);
	}
	
}
