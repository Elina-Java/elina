package service;

import java.util.concurrent.CyclicBarrier;


/**
 * An abstract for SOMD tasks that require synchornization points
 * 
 * @author Hervé Paulino
 *
 * @param <R> The type of the task's return value
 */
public abstract class SynchedSOMDTask<R> extends SOMDTask<R> {
	
	private static final long serialVersionUID = 1L;
	
	//FIXME: Should be Elina Barriers
	/**
	 * The synchronization barrier
	 */
	protected CyclicBarrier barrier;
	
}
