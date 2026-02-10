package linearAlgebra.elina.sgemv;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Distribution;
import elina.distributions.IndexDist;


@DistRed
public class sgemvProvider extends Service implements sgemvService {

	private static final long serialVersionUID = 1L;

	public void compute(float[][] a, float[] x, float[] y, float alpha) {
		//Distributions
		Distribution<int[]> jdist = new IndexDist(a.length,1,a[0].length*4 + 4 +4);
		
		dist(new sgemvTask(a,x,y,alpha), jdist);
	}
}
