package imageProcessing.gaussianWeight;

import java.io.IOException;

import elina.ApplicationLauncher;
import elina.utils.PartitionedTimer;

public class Benchmark extends elina.utils.Benchmark {

	public static void main(String[] args) throws IOException {

		int nexecs = 1;//parse(args, Benchmark.class.getName());
		
		if(args.length>=2)
		{
			GaussianWeightData.Matrix_sizes[0]=Integer.parseInt(args[1]);
			nexecs=Integer.parseInt(args[0]);
			PartitionedTimer.NEXECS=nexecs;
		}
		
		ApplicationLauncher.init();
		
		GaussianWeightService gaussianService = new GaussianWeightProvider();

		new GaussianWeightClient(gaussianService, nexecs).run();
	}
}
