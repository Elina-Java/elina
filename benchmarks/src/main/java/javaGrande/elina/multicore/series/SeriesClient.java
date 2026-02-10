package javaGrande.elina.multicore.series;

import java.io.IOException;

import elina.utils.Evaluation;

public class SeriesClient  {


	private final int EXECS;
	private final SeriesService service;


	public SeriesClient(SeriesService service, int nexecs) {
		super();
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() {


		try {
			Evaluation eval = new Evaluation(EXECS); 
			int size = JavaGrandeData.sizes[0];
			eval.writeAppName("series", size);
			for (int j = 0; j < EXECS; j++) {
				double[][]  TestArray = new double [2][size];

				eval.startTimer();
				double[][] result =  service.getFourierCoefficients(TestArray, size);
				eval.stopTimer();
				eval.writeTime();
				JavaGrandeData.validate(result);
			}
			eval.writeAverage();
			eval.writeSTDV();
			eval.end();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
