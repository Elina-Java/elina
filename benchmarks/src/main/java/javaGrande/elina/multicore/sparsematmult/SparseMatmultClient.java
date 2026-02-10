package javaGrande.elina.multicore.sparsematmult;


import java.io.IOException;

import elina.utils.Evaluation;




public class SparseMatmultClient  {


	private final SparseMatmultService  service;

	private final int EXECS;

	public SparseMatmultClient(SparseMatmultService service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {
		for (int size = 0; size < JavaGrandeData.NUMBER_OF_PROBLEMS; size++) {

			Evaluation eval = new Evaluation(EXECS); 

			eval.writeAppName("sparse", size);
			for (int j = 0; j < EXECS; j++) {
				JavaGrandeData data = new JavaGrandeData(size);
				
				eval.startTimer();
				service.test(data.y, data.val, data.row, data.col, data.x, JavaGrandeData.SPARSE_NUM_ITER);
				eval.stopTimer();
				eval.writeTime();
				
				double ytotal=0;
				for (int i = 0; i < data.row.length; i++) {
					ytotal += data.y[data.row[i]];
				}
				
				JavaGrandeData.validate(ytotal, size);
			}
			eval.writeAverage();

			eval.end();
		}
	}
	



}
