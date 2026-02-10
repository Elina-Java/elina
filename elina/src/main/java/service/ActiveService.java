package service;

import java.util.UUID;

/**
 * An abstract class for the specification of active services, 
 * which are services that are Runnable, and whose run method is automatically executed upon the service's deployment
 * 
 * @author João Saramago
 * @author Hervé Paulino
 *
 */

public abstract class ActiveService extends Service implements Runnable {

	/**
	 * Service constructor with given identifier
	 * @param id
	 */
	public ActiveService(UUID id) {
		super(id);
	}
	
	/**
	 * Service constructor with automatically generated identifier
	 */
	public ActiveService() {
		super();
	}

}
