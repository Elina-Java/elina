package elina.reductions;

import core.collective.AbstractReduction;


public class MatrixReduceCols extends AbstractReduction<int[][]> {

	private int n;
	private int m;

	public MatrixReduceCols(int n, int m) {
		this.n = n;
		this.m = m;
	}

	
	public int[][]  reduce(int[][][] data) {
		int[][] result = new int[n][m];

		int count = 0;
		
			
		for (int n = 0; n < data.length; n++) {	
			int[][] array = data[n];
			
			for (int i = 0; i < array.length; i++)
				for (int j = 0; j < array[i].length; j++) {
//					System.out.println(k + " " + i + " " + j + " "
//							+ array[k][i][j]);

					result[i][count + j] = array[i][j];
				}
			count += array[0].length;
		}
//		System.out.println();
//		for (int[] r : result)
//			System.out.println(Arrays.toString(r));
		// System.out.println();
		// for(int[][] k : array)
		// for(int[] r : k)
		// System.out.println(Arrays.toString(r));
		return result;
	}


	

}
