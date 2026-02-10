package service;

import java.io.Serializable;

/**
 * 
 * Classe abstracta que representa uma tarefa que conterá código para ser
 * executado numa localidade.
 * 
 * @author Diogo Mourão
 * 
 * 
 * @param <R>
 *            - Tipo do resultado da execução da tarefa.
 * 
 */

abstract class Schedulable implements Serializable, Cloneable {

	protected  IService service;
	
	private static final long serialVersionUID = -2819169027892556068L;

	public IService getService() {
		return this.service;
	}

	public void setService(IService service) {
		this.service = service;
	}
	
}