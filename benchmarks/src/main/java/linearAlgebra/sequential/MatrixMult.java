package linearAlgebra.sequential;

import java.io.IOException;

import linearAlgebra.elina.matrixMult.MatrixData;
import elina.utils.Evaluation;
import elina.utils.PartitionedTimer;


public class MatrixMult {


	public static void matrixMult(int[][] m1, int[][] m2, int[][] resultMatrix) {

		for(int i=0; i < m1.length; i++)
		{
			for(int j=0; j < m2[0].length; j++)
			{
				for(int k=0; k < m1[0].length; k++)
					resultMatrix[i][j] +=  m1[i][k] * m2 [k][j];
			}
		}
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

		eval.writeAppName("Matmult Preallocated", size);
		
		for(int i=0;i<NEXECS;i++)
		{
			MatrixData mdata = new MatrixData();
			
			int[][] A = mdata.createMatrix(size);
			int[][] B = mdata.createMatrix(size);
			int[][] C = new int[A[0].length][B.length];
			
			eval.startTimer();
			matrixMult(A, B, C);
			eval.stopTimer();
			eval.writeTime();
			//MatrixData.validate(C, 0);	
		}
		eval.writeAverage();
		eval.writeSTDV();
		eval.end();
	}
}
