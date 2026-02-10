package linearAlgebra.multithreaded.matrixMult;

import java.util.concurrent.Callable;

public class MultTask implements Callable<Void>{

	private int [][] m1, m2, result;
	private int li, lf;
	
	public MultTask(int [][] m1, int [][] m2, int [][] result, int li, int lf)
	{
		this.m1 = m1;
		this.m2 = m2;
		this.result = result;
		this.li = li;
		this.lf = lf;
	}
	
	public Void call() throws Exception {
		for(int i=li; i <= lf; i++)
			for(int j=0; j < m2[0].length; j++)
				for(int k=0; k < m1[0].length; k++)
					result[i][j] +=  m1[i][k] * m2 [k][j];
		return null;
	}

}
