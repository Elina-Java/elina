package javaGrande.elina.multicore.sor;

import service.Service;
import elina.distributions.IndexDist2D;
import elina.utils.SizeOf;

public class SORServer2D extends Service implements SORService {

	public void getSolution(double omega, double[][] g, int n_iters) {		
		dist(new SORTask2D(omega, g, n_iters), new IndexDist2D(g.length, g[0].length, SizeOf.Double));
				
	}
}
 