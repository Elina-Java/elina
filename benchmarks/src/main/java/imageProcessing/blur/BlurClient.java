package imageProcessing.blur;

import java.io.IOException;
import elina.utils.Evaluation;

public class BlurClient  {

	private final BlurService  service;
	private final int EXECS;

	public BlurClient (BlurService service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {

		int size = BlurData.Matrix_sizes[0];

		BlurData mdata = new BlurData();

		double[][] t0 = mdata.createMatrix(size);
		double[][] t1 = mdata.createMatrix(size);

		for (int i = 0; i < BlurData.NUMBER_TESTS; i++) {

			Evaluation eval = new Evaluation(EXECS); 

			eval.writeAppName("Blur", size);
			for (int j = 0; j < EXECS; j++) {
				eval.startTimer();

				service.blur(t0, t1);

				eval.stopTimer();
				eval.writeTime();
				System.gc();
				//RuntimeUtil.gc();
			}
			eval.writeAverage();
			eval.writeSTDV();

			eval.end();
		}
	}

}
