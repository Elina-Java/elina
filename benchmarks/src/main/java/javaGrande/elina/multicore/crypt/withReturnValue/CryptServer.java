package javaGrande.elina.multicore.crypt.withReturnValue;

import javaGrande.elina.multicore.crypt.JavaGrandeData;
import service.SOMDTask;
import service.Service;
import elina.distributions.IndexDistMultipleOf;
import elina.reductions.ByteArrayAssembler;
import elina.utils.SizeOf;

public class CryptServer extends Service implements CryptService {

	JavaGrandeData data = new JavaGrandeData();

	/**
	 * 
	 * @param text1
	 * @param text2 Both texts are received as parameters to remove memory allocation from the measurements (compliance with JavaGrande)
	 * @return
	 */
	public byte[] encrypt(byte[] text) {
		SOMDTask<byte[]> task = new CryptTask(data.Z, text);
		return distReduce(task, new ByteArrayAssembler(),
				new IndexDistMultipleOf(text.length,8, SizeOf.Int), 
				new IndexDistMultipleOf(text.length, 8, SizeOf.Int)).get();
	}

	
	public byte[] decrypt(byte[] text) {
		SOMDTask<byte[]> task = new CryptTask(data.DK, text);
		return distReduce(task, new ByteArrayAssembler(),
				new IndexDistMultipleOf(text.length,8, SizeOf.Int), 
				new IndexDistMultipleOf(text.length, 8, SizeOf.Int)).get();
	}
}
