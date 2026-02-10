package javaGrande.elina.multicore.crypt.withReturnValue;

import java.io.IOException;
import javaGrande.elina.multicore.crypt.JavaGrandeData;

import elina.utils.Evaluation;




public class CryptClient {

	private int EXECS;
	private CryptService service;
	protected String applicationName;

	public CryptClient(CryptService service, int nexecs) {
		super();
		this.service = service;
		this.EXECS = nexecs;
		this.applicationName = "crypt";
	}

	public void run() {

		try {
			for (int i = 0; i < JavaGrandeData.NUMBER_OF_PROBLEMS; i++) {

				Evaluation eval = new Evaluation(EXECS); 
				int size = JavaGrandeData.sizes[i];

				eval.writeAppName(this.applicationName, size);
				
				byte[] text = JavaGrandeData.getText(size);
				
				for (int j = 0; j < EXECS; j++) {

					eval.startTimer();
					byte[] cypher = service.encrypt(text);
					byte[] plain = service.decrypt(cypher);
					eval.stopTimer();

					eval.writeTime();
					JavaGrandeData.validate(text, plain);
				}
				eval.writeAverage();
				eval.end();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

}
