package javaGrande.elina.multicore.crypt;

import service.SOMDTask;
import service.Service;
import elina.distributions.IndexDistMultipleOf;
import elina.utils.SizeOf;

public class CryptServerSingleDist extends Service implements CryptService {

	JavaGrandeData data = new JavaGrandeData();

	/**
	 * 
	 * @param text1
	 * @param text2 Both texts are received as parameters to remove memory allocation from the measurements (compliance with JavaGrande)
	 * @return
	 */
	public void encryptDecrypt(byte[] text1, byte[] text2) {
		SOMDTask<Void> task = new CryptTask(data.Z, data.DK, text1, text2);
		dist(task, new IndexDistMultipleOf(text1.length,8, SizeOf.Int));
	}

	
	/*public void decrypt(byte[] text1, byte[] text2) {
		SOMDTask<Void> task = new CryptTask(data.DK, text1, text2);
		dist(task, new IndexDistMultipleOf(text1.length,8, SizeOf.Int));

	}*/
}
