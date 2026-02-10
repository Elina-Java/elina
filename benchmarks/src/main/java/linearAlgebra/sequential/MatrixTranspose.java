package linearAlgebra.sequential;

import java.io.IOException;

import linearAlgebra.elina.matrixMult.MatrixData;
import elina.utils.Evaluation;
import elina.utils.PartitionedTimer;

public class MatrixTranspose {

	/**
	 * @param args
	 */
	public static void matrixTranspose(int[][] A, int[][] T) {
		for(int i=0; i < A.length; i++)
			for(int j=0; j < A[0].length; j++)
				T[i][j] = A[j][i];
	}

	public static void main(String [] args) throws IOException
	{
		int NEXECS=1;
		if(args.length==2)
		{
			MatrixData.Matrix_sizes[0]=Integer.parseInt(args[1]);
			NEXECS=Integer.parseInt(args[0]);
		}
		PartitionedTimer.NEXECS=NEXECS;

		int size = MatrixData.Matrix_sizes[0]; 
		
		Evaluation eval = new Evaluation(NEXECS); 

		eval.writeAppName("Transpose", size);
		
		for(int i=0;i<NEXECS;i++)
		{
			MatrixData mdata = new MatrixData();
			
			int[][] O = mdata.createMatrix(size);
			int[][] T = new int[size][size];
			
			eval.startTimer();
			matrixTranspose(O, T);
			eval.stopTimer();
			eval.writeTime();
			//MatrixData.validate(C, 0);	
		}
		eval.writeAverage();
		eval.writeSTDV();
		eval.end();
	}

}
