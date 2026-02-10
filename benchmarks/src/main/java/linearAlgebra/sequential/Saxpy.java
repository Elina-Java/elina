package linearAlgebra.sequential;

import java.io.IOException;

import linearAlgebra.elina.saxpy.saxpyData;
import elina.utils.Evaluation;
import elina.utils.PartitionedTimer;

public class Saxpy {

	public static void main(String[] args) throws IOException {

		int EXECS = 1;

		if(args.length==2)
		{
			saxpyData.ARRAY_SIZES[0]=Integer.parseInt(args[1]);
			EXECS=Integer.parseInt(args[0]);
		}
		PartitionedTimer.NEXECS=EXECS;

		int size = saxpyData.ARRAY_SIZES[0];
		saxpyData mdata = new saxpyData();
		float[] x = mdata.createArray(size);
		float[] y = new float[size];
		float a = mdata.generateScalar();

		Evaluation eval = new Evaluation(EXECS); 

		eval.writeAppName("saxpy", size);
		for (int j = 0; j < EXECS; j++) {

			eval.startTimer();
			computeSaxpy(x, y, a);
			eval.stopTimer();
			eval.writeTime();

			//useless?
			System.gc();
		}
		eval.writeAverage();
		eval.writeSTDV();

		eval.end();
	}
	
	public static void computeSaxpy(float[] x, float[] y, float a)
	{
		for (int i=0; i < x.length; i++ )
			y[i] += a * x[i];
	}

}
