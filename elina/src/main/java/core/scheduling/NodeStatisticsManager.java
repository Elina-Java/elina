package core.scheduling;

import java.util.HashMap;
import java.util.Map;

import core.communication.Message;
import core.communication.Node;

/**
 * A class that bookkeeps the current statistics of each node
 *  
 * @author João Saramago
 *
 */
public class NodeStatisticsManager {

	/**
	 * The Node --> Statistics' map
	 */
	private static Map<Node<?>,Statistics> st = new HashMap<Node<?>, Statistics>();
	
	/**
	 * Attach the statistics of the current node to a given message
	 * 
	 * @param msg The message
	 */
	public static <C> void attachStatistics(Message<C> msg){
		msg.setStatistics(new Statistics());
	}
	
	/**
	 * Retrives the statistics attached to a given message
	 * 
	 * @param sms The message
	 */
	public static <C> void getStatistics(Message<C> msg){
		st.put(msg.getSending_host(), msg.getStatistics());
	}
	
}
