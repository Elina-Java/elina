package linearAlgebra.elina.saxpy;


import java.io.IOException;
import elina.utils.Evaluation;

public class saxpyClient {

	private final saxpyService  service;
	private final int EXECS;

	public saxpyClient(saxpyService service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {
		
		int size = saxpyData.ARRAY_SIZES[0];
		saxpyData mdata = new saxpyData();
		float[] x = mdata.createArray(size);
		float[] y = new float[size];
		float a = mdata.generateScalar();
		
		for (int i = 0; i < saxpyData.NUMBER_TESTS; i++) {

			Evaluation eval = new Evaluation(EXECS); 

			eval.writeAppName("saxpy", size);
			for (int j = 0; j < EXECS; j++) {
				
				eval.startTimer();
				service.compute(x, y, a);
				eval.stopTimer();
				eval.writeTime();
				System.gc();
			}
			eval.writeAverage();
			eval.writeSTDV();

			eval.end();
		}
	}
	
}
