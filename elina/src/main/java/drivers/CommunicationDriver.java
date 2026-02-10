package drivers;

import core.communication.Message;


/**
 * 
 * @author Diogo Mourão
 * @author João Saramago
 *
 */

public interface CommunicationDriver {
	void init();
	<C> void send(Message<C> message);
}
