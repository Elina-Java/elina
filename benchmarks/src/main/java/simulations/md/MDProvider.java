package simulations.md;

import instrumentation.definitions.DistRed;
import service.Service;
import core.collective.Distribution;
import elina.distributions.IndexDist;


@DistRed
public class MDProvider extends Service implements MDService {

	private static final long serialVersionUID = 1L;

	public void accel(Float3[] pos1, Float3[] pos2, float[] mass1, float[] mass2,
			float blockIndex1, float blockIndex2, float blockCount, Float3[] accel) {
		
		//Distributions
		Distribution<int[]> iDist = new IndexDist(pos1.length,1,16+16+4+4+16);
		
		dist(new MDTask(pos1,pos2,mass1,mass2,blockIndex1,blockIndex2,blockCount,accel), iDist);
	}
}
