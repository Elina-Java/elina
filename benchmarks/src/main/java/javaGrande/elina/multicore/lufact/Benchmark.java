package javaGrande.elina.multicore.lufact;


import elina.ApplicationLauncher;


public class Benchmark extends elina.utils.Benchmark {

	public static void main(String[] args) {

		parse(args, Benchmark.class.getName());
		if (PROBSIZE > 0) {
			JavaGrandeData.sizes[0] = PROBSIZE;
			JavaGrandeData.NUMBER_OF_PROBLEMS = 1;
		}

		ApplicationLauncher.init();

		LUService service = new LUProvider();
		new LUClient(service, NEXECS).run();

	}

}
