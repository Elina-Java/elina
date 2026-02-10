package javaGrande.elina.multicore.series;


import elina.ApplicationLauncher;
import elina.utils.PartitionedTimer;


public class Benchmark extends elina.utils.Benchmark {

	public static void main(String[] args) {

		parse(args, Benchmark.class.getName());
		if (PROBSIZE > 0) {
			JavaGrandeData.sizes[0] = PROBSIZE;
			JavaGrandeData.NUMBER_OF_PROBLEMS = 1;
			PartitionedTimer.NEXECS=NEXECS;
		}
			
		ApplicationLauncher.init();

		SeriesService seriesService = new SeriesProvider();
		new SeriesClient(seriesService, NEXECS).run();

	}

}
