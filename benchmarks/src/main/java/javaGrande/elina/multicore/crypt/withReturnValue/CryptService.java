package javaGrande.elina.multicore.crypt.withReturnValue;

import instrumentation.definitions.DistRed;
import instrumentation.definitions.DistRedTask;
import service.IService;

@DistRed
public interface CryptService extends IService {

	@DistRedTask
	byte[] encrypt(byte[] text);
	
	@DistRedTask
	byte[] decrypt(byte[] text);
	
}
