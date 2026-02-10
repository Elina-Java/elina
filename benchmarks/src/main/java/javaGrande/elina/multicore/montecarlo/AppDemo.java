package javaGrande.elina.multicore.montecarlo;


import java.util.List;

import service.Service;

public class AppDemo extends Service implements MonteCarloService {

	public List<ToResult> runThread(int[] nRunsMC, ToInitAllTasks initAllTasks) {
		return distReduce(new AppDemoTask(initAllTasks), 
				new AppDemoRed(), new DemoDist(nRunsMC)).get();
	}
}
