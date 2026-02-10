package communication;

import service.IService;

public interface EchoService extends IService {
	
	String echo(String s);

}
