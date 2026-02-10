package javaGrande.elina.cluster.crypt;

import instrumentation.definitions.DistRed;
import instrumentation.definitions.DistributionPolicy;
import instrumentation.definitions.ReductionPolicy;
import javaGrande.elina.multicore.crypt.JavaGrandeData;
import javaGrande.elina.multicore.crypt.withReturnValue.CryptTask;
import service.SOMDTask;
import service.Service;
import elina.distributions.ByteArrayDist;
import elina.distributions.IndexDistMultipleOf;
import elina.reductions.ByteArrayAssembler;
import elina.utils.SizeOf;

@DistRed
public class CryptServer  extends Service 
	implements javaGrande.elina.multicore.crypt.withReturnValue.CryptService {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@ReductionPolicy(reduction = ByteArrayAssembler.class)
	public byte[] encrypt(
			@DistributionPolicy(distribution = ByteArrayDist.class, params = { "text" }) byte[] text) {
	//	return super.encrypt(text);
		JavaGrandeData data = new JavaGrandeData();
	System.out.println("CryptServer : encrypt");
		
		SOMDTask<byte[]> task = new CryptTask(data.Z, text);
		return distReduce(task, new ByteArrayAssembler(),
				new IndexDistMultipleOf(text.length,8, SizeOf.Int), 
				new IndexDistMultipleOf(text.length, 8, SizeOf.Int)).get();
	}

	
	@ReductionPolicy(reduction = ByteArrayAssembler.class)
	public byte[] decrypt(
			@DistributionPolicy(distribution = ByteArrayDist.class, params = { "text" }) byte[] text) {
	//	return super.decrypt(text);
		JavaGrandeData data = new JavaGrandeData();
		SOMDTask<byte[]> task = new CryptTask(data.DK, text);
		return distReduce(task, new ByteArrayAssembler(),
				new IndexDistMultipleOf(text.length,8, SizeOf.Int), 
				new IndexDistMultipleOf(text.length, 8, SizeOf.Int)).get();
	}
}
