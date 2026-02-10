package javaGrande.elina.multicore.crypt.withReturnValue;

import javaGrande.elina.multicore.crypt.JavaGrandeData;

import elina.ApplicationLauncher;
import elina.utils.PartitionedTimer;


public class Benchmark extends javaGrande.elina.multicore.crypt.Benchmark {

	public static void main(String[] args) {

		parse(args, Benchmark.class.getName());
		if (PROBSIZE > 0) {
			JavaGrandeData.sizes[0] = PROBSIZE;
			JavaGrandeData.NUMBER_OF_PROBLEMS = 1;
			PartitionedTimer.NEXECS=NEXECS;
		}

		ApplicationLauncher.init();

		CryptService service = new CryptServer();
		new CryptClient(service, NEXECS).run();
	
	}
}
