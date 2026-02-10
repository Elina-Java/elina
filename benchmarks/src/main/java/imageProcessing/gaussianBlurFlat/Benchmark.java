package imageProcessing.gaussianBlurFlat;

import imageProcessing.gaussianBlur.GaussianBlurClient;
import imageProcessing.gaussianBlur.GaussianBlurData;
import imageProcessing.gaussianBlur.GaussianBlurService;

import java.io.IOException;

import elina.ApplicationLauncher;
import elina.utils.PartitionedTimer;

public class Benchmark extends elina.utils.Benchmark {

	public static void main(String[] args) throws IOException {

		int nexecs = 1;
		int radius = 1;

		if(args.length>=2)
		{
			nexecs=Integer.parseInt(args[0]);
			if(args[1].contains("-"))
			{
				String[] nr = args[1].split("-");
				GaussianBlurData.Matrix_sizes[0] = Integer.parseInt(nr[0]);
				radius = Integer.parseInt(nr[1]);
			}
			else
				GaussianBlurData.Matrix_sizes[0] = Integer.parseInt(args[1]);
		}
		PartitionedTimer.NEXECS=nexecs;

		ApplicationLauncher.init();

		GaussianBlurService gaussianService = new GaussianBlurProvider();

		new GaussianBlurClient(gaussianService, radius, nexecs).run();
	}
}
