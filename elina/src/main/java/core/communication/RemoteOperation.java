package core.communication;

import java.io.Serializable;
import java.util.UUID;

/**
 * Classe que representa uma operação remota num futuro
 * @author João Saramago
 *
 */
public class RemoteOperation implements Serializable{

	private static final long serialVersionUID = 1L;
	private UUID id;
	private UUID client_id;
	private String op;
	private Object[] args;
	
	
	/**
	 * Construtor
	 * @param id Identificador do furuto remoto
	 * @param client_id Identificador do cliente a que pertence esta execução 
	 * @param op Operação
	 * @param args Argumentos da operação
	 */
	public RemoteOperation(UUID id, UUID client_id,String op, Object... args ) {
		this.id=id;
		this.op=op;
		this.args=args;
		this.client_id=client_id;
	}

	/**
	 * Método que retorna o identificador do cliente a que pertence esta execução
	 * @return Identificador do cliente a que pertence esta execução
	 */
	public UUID getClientID(){
		return this.client_id;
	}
	

	/**
	 * Método que retorna o identificador do furuto remoto
	 * @return Identificador do furuto remoto
	 */
	public UUID getId() {
		return id;
	}


	/**
	 * Método que retorna o nome da Operação
	 * @return Operação
	 */
	public String getOp() {
		return op;
	}


	/**
	 * Método que retorna os arqumentos da operação
	 * @return Arqumentos da operação
	 */
	public Object[] getArgs() {
		return args;
	}

}
