package linearAlgebra.elina.sgemv;

import java.util.Random;

public class sgemvData {

	static final int NUMBER_TESTS = 1;
	
	private static final long RANDOM_SEED = 10101010;
	public static final int[] Matrix_sizes = { 8192 /*, 100000, 500000*/ };
	
	private Random r;
	
	public sgemvData()
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
	
	public float generateScalar()
	{
		return r.nextFloat()*5.0f;
	}

}
