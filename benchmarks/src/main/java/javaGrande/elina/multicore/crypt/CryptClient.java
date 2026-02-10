package javaGrande.elina.multicore.crypt;

import java.io.IOException;

import elina.utils.Evaluation;




public class CryptClient {

	private int EXECS;
	private CryptService service;

	public CryptClient(CryptService service, int nexecs) {
		super();
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() {

		try {
			for (int i = 0; i < JavaGrandeData.NUMBER_OF_PROBLEMS; i++) {

				Evaluation eval = new Evaluation(EXECS); 
				int size = JavaGrandeData.sizes[i];

				eval.writeAppName("crypt", size);
				
				byte[] text = JavaGrandeData.getText(size);
				byte[] cypher = new byte[text.length];
				byte[] plain = new byte[text.length];
				
				for (int j = 0; j < EXECS; j++) {

					eval.startTimer();
					service.encryptDecrypt(text, cypher);
					//service.decrypt(cypher, plain);
					eval.stopTimer();

					eval.writeTime();
					JavaGrandeData.validate(text, plain);
				}
				eval.writeAverage();
				eval.writeSTDV();
				eval.end();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

}
