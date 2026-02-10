package javaGrande.elina.multicore.crypt;

import service.IService;


public interface CryptService extends IService {

//	@Task
	public void encryptDecrypt(byte[] text1, byte[] text2);
	
//	@Task
	//public void decrypt(byte[] text1, byte[] text2);
	
}
