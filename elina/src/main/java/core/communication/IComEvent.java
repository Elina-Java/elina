package core.communication;


/**
 * Interface que repreenta o evento que recebe mensagens
 * @author João Saramago
 *
 */
public interface IComEvent {

	/**
	 * Evento que recebe mensagens
	 * @param sms Mensagem
	 */
	public <C> void processMessage(Message<C> sms);
	
}
