package javaGrande.elina.cluster.crypt;

import java.io.IOException;

import javaGrande.elina.multicore.crypt.JavaGrandeData;
import javaGrande.elina.multicore.crypt.withReturnValue.CryptService;
import service.ActiveService;
import elina.utils.Evaluation;


public class CryptClient extends ActiveService {

	private static final long serialVersionUID = 1L;
	
	private CryptService service;
	
	private final int EXECS;
	
	private final String applicationName;
	
	
	public CryptClient(CryptService service, int nExecs, int nSites) {
		this.service = service;
		this.EXECS = nExecs;
		this.applicationName = "crypt_" + nSites;
	}

	public void run() {

		System.out.println(Thread.currentThread() + "client");
		
		try {
			for (int i = 0; i < JavaGrandeData.NUMBER_OF_PROBLEMS; i++) {
				int size = JavaGrandeData.sizes[i];

				Evaluation eval = new Evaluation(EXECS); 
				eval.writeAppName(this.applicationName, size);
				
				byte[] text = JavaGrandeData.getText(size);
				
				for (int j = 0; j < EXECS; j++) {

					eval.startTimer();
					byte[] cypher = service.encrypt(text);
			//		byte[] plain = service.decrypt(cypher);
					eval.stopTimer();

					eval.writeTime();
				//	JavaGrandeData.validate(text, plain);
				}
				
				eval.writeAverage();
				eval.end();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

}
