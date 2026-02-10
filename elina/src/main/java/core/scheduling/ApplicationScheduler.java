package core.scheduling;


import java.util.List;

import core.communication.Node;
import drivers.Adapters;
import service.Application;

/**
 * The class for scheduling applications.
 * It resorts to instances of SchedulingAdapter and SchedulingRankAdapter
 * 
 * @author João Saramago
 *
 */
public class ApplicationScheduler {

	/** 
	 * List of target nodes
	 */
	private final List<Node<?>> nodes;
	
	/**
	 * Scheduler Adapter
	 */
	private static SchedulingAdapter scheduler;
	
	/**
	 * Node rank adapter
	 */
	private static SchedulingRankAdapter rank;
	
	/**
	 * Constructor
	 * @param nodes List of nodes 
	 */
	public ApplicationScheduler(List<Node<?>> nodes){
		this.nodes = nodes;
		scheduler = Adapters.getSchDriver();
		rank = Adapters.getSchRankDriver();
	}
	
	/**
	 * Schedule an application according to the current adapters
	 * @param app The application
	 * @return
	 */
	public SchedulingInfo schedule(Application app) {		
		return scheduler.schedule(app,rank.sort(nodes));
	}

}
