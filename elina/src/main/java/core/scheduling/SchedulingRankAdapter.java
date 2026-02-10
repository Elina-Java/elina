package core.scheduling;

import java.util.List;

import core.communication.Node;

/**
 * The interface for the adapter responsible for ranking a list of nodes
 * 
 * @author João Saramago
 *
 */
public interface SchedulingRankAdapter {

	/**
	 * Sort a given list of nodes
	 * @param nodes The original node list
	 * @return The sorted node list
	 */
	List<Node<?>> sort(List<Node<?>> nodes);
}
