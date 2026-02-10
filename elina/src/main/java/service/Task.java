package service;

import java.util.concurrent.Callable;

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

public abstract class Task<R> extends Schedulable implements Callable<R> {

	private final Object[] args;

	public Task(Object ...args)
	{
		this.args = args;
	}

}