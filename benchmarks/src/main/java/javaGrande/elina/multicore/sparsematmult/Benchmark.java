package javaGrande.elina.multicore.sparsematmult;

import java.io.IOException;

import elina.ApplicationLauncher;


public class Benchmark extends elina.utils.Benchmark {

	public static void main(String[] args) throws IOException {

		parse(args, Benchmark.class.getName());
		if (PROBSIZE > 0) {
			JavaGrandeData.NUMBER_OF_PROBLEMS = PROBSIZE;
		}

		ApplicationLauncher.init();
		
		SparseMatmultService sparceService = new SparseMatmultProvider();
		new SparseMatmultClient(sparceService, NEXECS).run();

	}

}
