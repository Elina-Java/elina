package communication;

import core.communication.Message;
import drivers.CommunicationDriver;

public class FakeCom implements CommunicationDriver {

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public <C> void send(Message<C> message) {
		System.out.println("New MESSAGE: "+message.getType());
	}


}
