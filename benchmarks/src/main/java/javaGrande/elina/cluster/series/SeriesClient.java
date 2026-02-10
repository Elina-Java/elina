package javaGrande.elina.cluster.series;

import java.io.IOException;

import javaGrande.elina.multicore.series.JavaGrandeData;
import service.ActiveService;
import elina.utils.Evaluation;

public class SeriesClient extends ActiveService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int EXECS;
	
	private final SeriesService service;
	
	private final int NSITES;

	public SeriesClient(SeriesService service, int nExecs, int nSites) {
		super();
		this.service = service;
		this.EXECS = nExecs;
		this.NSITES = nSites;
	}

	public void run() {

		try {

			for (int i = 0; i < JavaGrandeData.NUMBER_OF_PROBLEMS; i++) {

				Evaluation eval = new Evaluation(EXECS); 
				int size = JavaGrandeData.sizes[i];
				
				eval.writeAppName("series_" + NSITES, size);
				for (int j = 0; j < EXECS; j++) {

					
					
					eval.startTimer();
					double[][] result =  service.getFourierCoefficients(new SeriesInput(size)).result;

					eval.stopTimer();
					eval.writeTime();
					
					JavaGrandeData.validate(result);
				}
				eval.writeAverage();
				eval.end();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}