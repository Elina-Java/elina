package core.remote;

import java.util.UUID;

public class ClientUUIDFactory {

	private static UUID id;
	
	public static UUID getUUID(){
		//TODO:Mudar para random
		if(id==null){
			id=UUID.randomUUID();
//			id=UUID.fromString("1e8c2de3-8c3b-4840-bc66-8320df97fb9b");
		}
		return id;
	}
	
}
