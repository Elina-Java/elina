package javaGrande.elina.multicore.sor;

import java.io.IOException;

import elina.utils.Evaluation;





public class SORClient {

	private final SORService service;

	private static int EXECS;

	public SORClient(SORService service, int nexecs) {
		this.service = service;
		SORClient.EXECS = nexecs;
	}

	public void run() throws IOException {
		for (int pclass = 0 ; pclass < JavaGrandeData.NUMBER_OF_PROBLEMS; pclass++) {

			Evaluation eval = new Evaluation(EXECS); 

			eval.writeAppName("sor", pclass);
			for (int n = 0; n < EXECS; n++) {

				double[][] G = JavaGrandeData.getRandomMatrix(pclass);

				eval.startTimer();

				service.getSolution(1.25, G, JavaGrandeData.JACOBI_NUM_ITER);

				eval.stopTimer();
				eval.writeTime();
				
				double 	Gtotal = 0;
				for (int i=1; i<G.length-1; i++) 
	                 for (int j=1; j<G[0].length-1; j++) 
	                  Gtotal += G[i][j];
	                 
				
				
				JavaGrandeData.validate(Gtotal, pclass);

			}
			eval.writeAverage();

			eval.end();
		}
	}

}
