package scheduling;

import java.util.List;

import service.Application;
import core.communication.Node;
import core.scheduling.SchedulingAdapter;
import core.scheduling.SchedulingInfo;

public class Local implements SchedulingAdapter {

	@Override
	public SchedulingInfo schedule(Application app, List<Node<?>> sort) {
		return null;
	}

}
