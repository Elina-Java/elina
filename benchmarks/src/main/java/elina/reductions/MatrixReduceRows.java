package elina.reductions;

import core.collective.AbstractReduction;



public class MatrixReduceRows extends AbstractReduction<int[][]>  {

	private int n;
	private int m;

	public MatrixReduceRows(int n, int m) {
		this.n = n;
		this.m = m;
	}


	public int[][]  reduce(int[][][] data) {
		int[][] result = new int[n][m];

		int row = 0;
		
		for (int n = 0; n < data.length; n++) {	
			int[][] array = data[n];

			for (int i = 0; i < array.length; i++)
				for (int j = 0; j < array[i].length; j++) {
		//			 System.out.println(i+" "+j+ " "+array[i][j]);

					result[i+row][j] = array[i][j];
//					System.out.println(i+row+" "+j+col);
				}
		//	col = (col+ array[0].length)%m;
			row+=array.length;
		}

		return result;
	}



}
