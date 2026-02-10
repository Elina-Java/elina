package javaGrande.elina.multicore.montecarlo;

import java.util.ArrayList;
import java.util.List;

import service.SOMDTask;

public class AppDemoTask extends SOMDTask<List<ToResult>> {

	private static final long serialVersionUID = 1L;
	private ToInitAllTasks initAllTasks;

	public AppDemoTask(ToInitAllTasks initAllTasks) {
		this.initAllTasks = initAllTasks;
	}

	@Override
	public List<ToResult> call(Object[] partition) {
		// Now do the computation.
		ToTask[] arg = (ToTask[]) partition[0];
		List<ToResult> results = new ArrayList<ToResult>();

		for (ToTask toTask : arg) {
			PriceStock ps = new PriceStock(); 
			ps.setInitAllTasks(initAllTasks);
			ps.setTask(toTask);
			ps.run();
			results.add(ps.getResult());
		}
		return results;
	}
}
