package scheduling.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import core.communication.Node;
import core.scheduling.SchedulingRankAdapter;
import elina.Elina;

public class LexicalRank implements SchedulingRankAdapter {

	private static Logger logger = Logger.getLogger(RandomRank.class);
	
	@Override
	public List<Node<?>> sort(List<Node<?>> nodes) {
		if(Elina.DEBUG)
			logger.debug("Sort Nodes ["+nodes.toString()+"]");
		
		ArrayList<Node<?>> out = new ArrayList<Node<?>>(nodes);
		
		for (Node<?> node : out) {
			node.setRank(node.hashCode());
		}
		Collections.sort(out, new Comparator<Node<?>>() {

			@Override
			public int compare(Node<?> o1, Node<?> o2) {
				return o1.getAddr().compareTo(o2.getAddr());
			}
		});
		return out;
	}

}
