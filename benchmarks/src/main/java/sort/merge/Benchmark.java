package sort.merge;

import elina.ApplicationLauncher;
import elina.utils.PartitionedTimer;


public class Benchmark extends elina.utils.Benchmark {


	public static void main(String[] args) {

		parse(args, Benchmark.class.getName());
		if (PROBSIZE > 0) {
			MergeSortClient.sizes[0] = PROBSIZE;
			MergeSortClient.NUMBER_OF_PROBLEMS = 1;
		}
		PartitionedTimer.NEXECS = NEXECS;
		
		ApplicationLauncher.init();

		SortService service = new SortServer();
		new MergeSortClient(service, NEXECS).run();
		
	}

}
