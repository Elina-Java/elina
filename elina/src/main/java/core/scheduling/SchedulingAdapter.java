package core.scheduling;

import java.util.List;

import core.communication.Node;
import service.Application;

/**
 * The interface for the Scheduling adpater
 * 
 * @author João Saramago
 *
 */

public interface SchedulingAdapter {

	/**
	 * Schedule an application across a list of nodes
	 * 
	 * @param app The application
	 * @param sort List of nodes
	 * @return The scheduling information
	 */
	SchedulingInfo schedule(Application app, List<Node<?>> nodes);
}
