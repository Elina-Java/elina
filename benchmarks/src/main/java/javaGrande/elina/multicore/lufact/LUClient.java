package javaGrande.elina.multicore.lufact;

import java.io.IOException;

import elina.utils.Evaluation;




public class LUClient  {

	
	private final int EXECS;
	private final LUService service;


	public LUClient(LUService service, int nexecs) {
		super();
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() {

		try {

			for (int probClass = 0; probClass < JavaGrandeData.NUMBER_OF_PROBLEMS; probClass++) {					
				Evaluation eval = new Evaluation(EXECS); 
				eval.writeAppName("lufact", probClass);
				
				for (int j = 0; j < EXECS; j++) {
					JavaGrandeData data = new JavaGrandeData(probClass);
					
					eval.startTimer();	
					service.lufact(data.a, data.lda, data.n, data.ipvt);
					JavaGrandeData.dgesl(data.a, data.lda, data.n, data.ipvt, data.b, 0);
					
					eval.stopTimer();
					eval.writeTime();
					data.validate(probClass);
				}
				eval.writeAverage();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
