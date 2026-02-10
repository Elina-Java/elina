package imageProcessing.gaussianBlur;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Distribution;
import elina.distributions.stencil.StencilDist2D;
import elina.distributions.stencil.StencilDistInner2D;
import elina.utils.SizeOf;

@DistRed
public class GaussianBlurProvider extends Service implements GaussianBlurService {

	private static final long serialVersionUID = 1L;

	public void gaussianBlur(double[][] wm, int radius, double[][] t0, double[][] t1) {
		
		int window = (1 + 2*radius)*(1 + 2*radius);
		
		//Distributions
		Distribution<int[][]> t0dist = new StencilDist2D(t0.length,t0[0].length,window,SizeOf.Double,radius);
		Distribution<int[][]> t1dist = new StencilDistInner2D(t1.length,t1[0].length,window,SizeOf.Double,radius);
		Distribution<int[][]> wmdist = new StencilDist2D(wm.length,wm[0].length,window,SizeOf.Double,radius);
		
		dist(new GaussianBlurTask(wm,radius,t0,t1), t0dist, t1dist, wmdist);
	}
}
