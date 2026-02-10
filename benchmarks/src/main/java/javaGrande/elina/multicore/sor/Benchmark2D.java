package javaGrande.elina.multicore.sor;

import java.io.IOException;

import elina.ApplicationLauncher;


public class Benchmark2D extends elina.utils.Benchmark {

	public static void main(String[] args) throws IOException {

		parse(args, Benchmark.class.getName());
		if (PROBSIZE > 0) {
			JavaGrandeData.sizes[0] = PROBSIZE;
			JavaGrandeData.NUMBER_OF_PROBLEMS = 1;
		}
		
		ApplicationLauncher.init();
		SORService sorService = new SORServer2D();
		new SORClient(sorService, NEXECS).run();
	}

}
