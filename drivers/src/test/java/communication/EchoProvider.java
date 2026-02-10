package communication;

import service.Service;

public class EchoProvider extends Service implements EchoService {

	@Override
	public String echo(String s) {
		return s;
	}

}
