package linearAlgebra.elina.matrixTranspose;

import java.util.Random;

public class TransposeData {

	public static final int NUMBER_TESTS = 1;
	
	private static final long RANDOM_SEED = 10101010;
	public static final int[] MATRIX_SIZES = { 8192, 1000, 1000 };
	
	private static Random r = new Random(RANDOM_SEED);
	
	public static int[][] createMatrix(int size)
	{
		int[][] matrix = new int[size][size];
		
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
				matrix[i][j] = r.nextInt(5);
		
		return matrix;
	}
	
	public static int[][] createMatrix(int rows, int cols)
	{
		int[][] matrix = new int[rows][cols];
		
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				matrix[i][j] = r.nextInt(5);
		
		return matrix;
	}

	public static void validate(float[][] matrix, int testNumber) {
		int sum = 0;
		for(int i=0;i<matrix.length;i++)
			for(int j=0;j<matrix[0].length;j++)
				sum += matrix[i][j];
		
		System.out.println("Sum: " + sum);
	}
	
	public static void printMatrix(int[][] matrix)
	{
		for(int i=0;i<matrix.length;i++)
		{
			for(int j=0;j<matrix[0].length;j++)
				System.out.print(matrix[i][j]+",");
			System.out.println();
		}
	}
}
