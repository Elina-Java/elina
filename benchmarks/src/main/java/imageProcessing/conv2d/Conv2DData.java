package imageProcessing.conv2d;

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

public class Conv2DData {

	static final int NUMBER_TESTS = 1;
	
	private static final long RANDOM_SEED = 10101010;
	public static final int[] Matrix_sizes = { 4096 /*, 100000, 500000*/ };
	
	private Random r;
	
	public Conv2DData()
	{
		r = new Random(RANDOM_SEED);
	}
	
	public float[][] createMatrix(int size)
	{
		float[][] matrix = new float[size][size];
		
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
				matrix[i][j] = r.nextFloat()*5;
		
		return matrix;
	}
	
	public float[][] createMatrix(int rows, int cols)
	{
		float[][] matrix = new float[rows][cols];
		
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				matrix[i][j] = r.nextFloat()*5;
		
		return matrix;
	}

}
