package linearAlgebra.elina.saxpy;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Distribution;
import elina.distributions.IndexDist;
import elina.utils.SizeOf;


@DistRed
public class saxpyProvider extends Service implements saxpyService {

	private static final long serialVersionUID = 1L;

	public void compute(float[] x, float[] y, float a) {
		
		//Distributions  (y is the result)
		Distribution<int[]> xdist = new IndexDist(x.length,1,SizeOf.Float*2);
		
		dist(new saxpyTask(x,y,a), xdist);
	}
}
