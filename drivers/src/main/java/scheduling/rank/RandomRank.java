package scheduling.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import core.communication.Node;
import core.scheduling.SchedulingRankAdapter;
import elina.Elina;

public class RandomRank implements SchedulingRankAdapter {

	private static Logger logger = Logger.getLogger(RandomRank.class);
	
	@Override
	public List<Node<?>> sort(List<Node<?>> nodes) {
		if(Elina.DEBUG)
			logger.debug("Sort Nodes ["+nodes.toString()+"]");
		
		ArrayList<Node<?>> out = new ArrayList<Node<?>>(nodes);
		Random r = new Random();
		for (Node<?> node : out) {
			node.setRank(r.nextInt());
		}
		Collections.sort(out, new Comparator<Node<?>>() {

			@Override
			public int compare(Node<?> o2, Node<?> o1) {
				return (o1.getRank()<o2.getRank() ? -1 : (o1.getRank()==o2.getRank() ? 0 : 1));
			}
		});
		return out;
	}

}
