package elina.reductions;

import core.collective.AbstractReduction;

public class MatrixReduceBlocks extends AbstractReduction<int[][]> {

	private int[][] C;

	public MatrixReduceBlocks(int[][] C) {
		this.C=C;
	}

	public int[][] reduce(int[][][] data) {
		int nparts = data.length;
		int div = (int) Math.round(Math.cbrt(nparts));
		
		int iBegin = 0;
		int jBegin = 0;
		for(int i=0;i<div;i++)
		{
			for(int j=0;j<div;j++)
			{
				for(int k=0;k<div;k++)
				{
					int[][] part = data[i*div*div+k*div+j];
					for(int pi=0;pi<part.length;pi++)
						for(int pj=0;pj<part[0].length;pj++)
						{
							C[iBegin+pi][jBegin+pj]+=part[pi][pj];
						}
				}
				jBegin+=data[i*div*div+j][0].length;
			}
			iBegin+=data[i*div*div].length;
			jBegin=0;
		}
		
		return C;
	}
	
	/**
	 * For debug reasons.
	 * @param m - the matrix to be printed
	 */
	public static void printMatrix(int[][] m)
	{
		for(int i=0;i < m.length;i++)
		{
			for(int j=0;j<m[0].length;j++)
			{
				System.out.print(m[i][j] + "\t");
			}
			System.out.println();
			System.out.println();
		}
	}

}
