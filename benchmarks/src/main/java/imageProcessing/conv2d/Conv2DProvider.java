package imageProcessing.conv2d;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Distribution;
import core.collective.DistributionsCombination;
import elina.combinations.NestedLoopCombination;
import elina.distributions.Index2DBlockDist;


@DistRed
public class Conv2DProvider extends Service implements Conv2DService {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public void convolution(float[][] a, float[][] h, float[][] c, int U) {
		
		//Distributions
		Distribution<int[][]> adist = new Index2DBlockDist(a.length,a[0].length,U,4);
		Distribution<int[][]> cdist = new Index2DBlockDist(c.length,c[0].length,1,4);
		Distribution<int[][]>[] distrs = (Distribution<int[][]>[]) new Distribution[2];
		distrs[0] = adist;
		distrs[1] = cdist;
		
		DistributionsCombination dist = new NestedLoopCombination(distrs);
		
		distReduce( new Conv2DTask(a,h,c,U), null, dist, dist.getDistributions());
	}
}
