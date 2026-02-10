package linearAlgebra.elina.saxpy;

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

public class saxpyData {

	static final int NUMBER_TESTS = 1;
	
	private static final long RANDOM_SEED = 10101010;
	public static final int[] ARRAY_SIZES = { 8192 /*, 100000, 500000*/ };
	
	private Random r;
	
	public saxpyData()
	{
		r = new Random(RANDOM_SEED);
	}
	
	public float[] createArray(int size)
	{
		float[] array = new float[size];
		
		for(int i=0;i<size;i++)
			array[i] = r.nextFloat()*5.0f;
		
		return array;
	}
	
	public float generateScalar()
	{
		return r.nextFloat()*5.0f;
	}

}
