package linearAlgebra.elina.matrixTranspose;

import java.io.IOException;
import elina.ApplicationLauncher;
import elina.utils.PartitionedTimer;

public class Benchmark extends elina.utils.Benchmark {

	public static void main(String[] args) throws IOException {

		int nexecs = 1;
		
		if(args.length==2)
		{
			TransposeData.MATRIX_SIZES[0]=Integer.parseInt(args[1]);
			TransposeData.MATRIX_SIZES[1]=Integer.parseInt(args[1]);
			nexecs=Integer.parseInt(args[0]);
			PartitionedTimer.NEXECS=nexecs;
		}
		if(args.length==3)
		{
			nexecs=Integer.parseInt(args[0]);
			TransposeData.MATRIX_SIZES[0]=Integer.parseInt(args[1]);
			TransposeData.MATRIX_SIZES[1]=Integer.parseInt(args[2]);
			PartitionedTimer.NEXECS=nexecs;
		}
		
		ApplicationLauncher.init();

		TransposeService transposeService = new TransposeProvider();

		new TransposeClient(transposeService, nexecs).run();
	}
}
