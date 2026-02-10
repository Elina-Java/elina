package core.communication;


import drivers.Adapters;
import drivers.CommunicationDriver;

/**
 * 
 * Classe que funciona como um factory de objectos que contêm a implementação da comunicação para uma localidade remota.
 * Esta classe será usada na implementação do stub {@link PlaceStub}.
 * 
 * @author Diogo Mourão
 * 
 */
public class CommunicationFactory
{
	
	/**
	 * Método que retorna o driver de comunicação
	 * @return Driver de comunicação
	 */
	public static CommunicationDriver createCommunication(Node node)
	{
		CommunicationDriver comm = Adapters.getCommDriver(); 
		return comm;
	}
}
