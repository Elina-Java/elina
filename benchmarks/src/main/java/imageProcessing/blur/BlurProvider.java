package imageProcessing.blur;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Distribution;
import elina.distributions.flatTuned.StencilFlatDist2D;
import elina.distributions.stencil.StencilDist2D;
import elina.utils.SizeOf;


@DistRed
public class BlurProvider extends Service implements BlurService {

	private static final long serialVersionUID = 1L;

	public void blur(double[][] t0, double[][] t1) {
		
		//Distributions
		Distribution<int[][]> t0dist = new StencilDist2D(t0.length,t0[0].length,9,SizeOf.Double,1);
		Distribution<int[][]> t1dist = new StencilDist2D(t1.length,t1[0].length,9,SizeOf.Double,1);
		/*
		Distribution<int[][]> t0dist = new StencilFlatDist2D(t0.length,t0[0].length,9,SizeOf.Double,1);
		Distribution<int[][]> t1dist = new StencilFlatDist2D(t1.length,t1[0].length,9,SizeOf.Double,1);
		*/
		dist(new BlurTask(t0,t1), t0dist, t1dist);
	}
}
