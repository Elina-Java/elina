package linearAlgebra.elina.saxpy;

import java.io.IOException;
import elina.ApplicationLauncher;
import elina.utils.PartitionedTimer;

public class Benchmark extends elina.utils.Benchmark {

	public static void main(String[] args) throws IOException {

		int nexecs = 10;
		
		if(args.length==2)
		{
			saxpyData.ARRAY_SIZES[0]=Integer.parseInt(args[1]);
			nexecs=Integer.parseInt(args[0]);
			PartitionedTimer.NEXECS=nexecs;
		}
		
		ApplicationLauncher.init();

		saxpyService sparceService = new saxpyProvider();

		new saxpyClient(sparceService, nexecs).run();
	}
}
