package javaGrande.elina.multicore.series;

import service.SOMDTask;

public class SerieTask extends SOMDTask<int[]>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double[][] TestArray;

	public SerieTask(double[][] TestArray)
	{
		this.TestArray = TestArray;
	}


	@Override
	public int[] call(Object[] partition) {
		//SeriesResult result = new SeriesResult(size);
		double omega = (double) 3.1415926535897932;
				
		int xi = ((int[]) partition[0])[0];
		int xf =  ((int[]) partition[0])[1];

		
		for (int i = Math.max(xi,1); i < xf; i++)
		{
			// Calculate A[i] terms. Note, once again, that we
			// can ignore the 2/period term outside the integral
			// since the period is 2 and the term cancels itself
			// out.

			TestArray[0][i] = JavaGrandeData.TrapezoidIntegrate((double)0.0,
					(double)2.0,
					1000,
					omega * (double)(i), 
					1);                       // 1 = cosine term.

			// Calculate the B[i] terms.

			TestArray[1][i] = JavaGrandeData.TrapezoidIntegrate((double)0.0,
					(double)2.0,
					1000,
					omega * (double)(i),
					2);                       // 2 = sine term.
		}

		return null;
	}


	

}
