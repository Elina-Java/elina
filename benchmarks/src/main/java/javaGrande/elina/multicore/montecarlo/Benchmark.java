package javaGrande.elina.multicore.montecarlo;

import java.io.IOException;
import elina.ApplicationLauncher;
import elina.utils.PartitionedTimer;


public class Benchmark extends elina.utils.Benchmark {

	public static void main(String[] args) throws DemoException, IOException {

		parse(args, Benchmark.class.getName());
		
		if (PROBSIZE > 0) {
			JavaGrandeData.sizes[0] = PROBSIZE;
			JavaGrandeData.NUMBER_OF_PROBLEMS = 1;
			PartitionedTimer.NEXECS = NEXECS;
		}
		
		ApplicationLauncher.init();

		MonteCarloService service = new AppDemo();
		new MonteCarloClient(service, NEXECS).run();		
	}
}
