package linearAlgebra.elina.matrixMult;

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

public class MatrixData {
	
	private static final long RANDOM_SEED = 10101010;
	public static final int[] Matrix_sizes = { 30 , 100000, 500000 };
	
	private Random r;
	
	public MatrixData()
	{
		r = new Random(RANDOM_SEED);
	}
	
	public int[][] createMatrix(int size)
	{
		int[][] matrix = new int[size][size];
		
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
				matrix[i][j] = r.nextInt(5);
		
		return matrix;
	}
	
	public int[][] createMatrix(int m, int n)
	{
		int[][] matrix = new int[m][n];
		
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				matrix[i][j] = r.nextInt(5);
		
		return matrix;
	}

	public static boolean validate(int[][] A, int[][] B, int[][] C) {
		
		int[][] resultMatrix = new int[A.length][B[0].length];
		for(int i=0; i < A.length; i++)
		{
			for(int j=0; j < B[0].length; j++)
			{
				for(int k=0; k < A[0].length; k++)
					resultMatrix[i][j] +=  A[i][k] * B [k][j];
			}
		}
		
		for(int i=0;i<C.length;i++)
			for(int j=0;j<C[0].length;j++)
				if(C[i][j]!=resultMatrix[i][j])
					return false;

		
		return true;
	}
	
}
