package imageProcessing.gaussianWeight;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Distribution;
import elina.distributions.Index2DBlockDist;
import elina.utils.SizeOf;


@DistRed
public class GaussianWeightProvider extends Service implements GaussianWeightService {

	private static final long serialVersionUID = 1L;

	public void computeGaussianWeight(double[][] wm, double sigma) {
		//Distributions
		Distribution<int[][]> wmdist = new Index2DBlockDist(wm.length,wm[0].length,1,SizeOf.Double);

		dist(new GaussianWeightTask(wm,sigma), wmdist);
	}
}
